package com.elab.data.dts.recordgenerator;

import com.elab.data.dts.common.Checkpoint;
import com.elab.data.dts.common.Context;
import com.elab.data.dts.common.Util;
import com.elab.data.dts.metastore.KafkaMetaStore;
import com.elab.data.dts.metastore.LocalFileMetaStore;
import com.elab.data.dts.metastore.MetaStoreCenter;
import com.elab.data.dts.recordprocessor.EtlRecordProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class RecordGenerator implements Runnable, Closeable {
    private static final Logger log = LoggerFactory.getLogger(RecordGenerator.class);
    private static final String LOCAL_FILE_STORE_NAME = "localCheckpointStore";
    private static final String KAFKA_STORE_NAME = "kafkaCheckpointStore";
    private final Properties properties;
    private final int tryTime;
    private final Context context;
    private final TopicPartition topicPartition;
    private final String groupID;
    private final ConsumerWrapFactory consumerWrapFactory;
    private final Checkpoint initialCheckpoint;
    private volatile Checkpoint toCommitCheckpoint = null;
    private final MetaStoreCenter metaStoreCenter = new MetaStoreCenter();
    private final AtomicBoolean useCheckpointConfig;
    private final ConsumerSubscribeMode subscribeMode;
    private final long tryBackTimeMS;
    private volatile boolean existed;

    public RecordGenerator(Properties properties, Context context, Checkpoint initialCheckpoint, ConsumerWrapFactory consumerWrapFactory) {
        this.properties = properties;
        this.tryTime = Integer.valueOf(properties.getProperty(Names.TRY_TIME, "150"));
        this.tryBackTimeMS = Long.valueOf(properties.getProperty(Names.TRY_BACK_TIME_MS, "10000"));
        this.context = context;
        this.consumerWrapFactory = consumerWrapFactory;
        this.initialCheckpoint = initialCheckpoint;
        this.topicPartition = new TopicPartition(properties.getProperty(Names.KAFKA_TOPIC), 0);
        this.groupID = properties.getProperty(Names.GROUP_NAME);
        this.subscribeMode = parseConsumerSubscribeMode(properties.getProperty(Names.SUBSCRIBE_MODE_NAME, "assign"));
        this.useCheckpointConfig = new AtomicBoolean(StringUtils.equalsIgnoreCase(properties.getProperty(Names.USE_CONFIG_CHECKPOINT_NAME), "true"));
        existed = false;
        metaStoreCenter.registerStore(LOCAL_FILE_STORE_NAME, new LocalFileMetaStore(LOCAL_FILE_STORE_NAME));
        log.info("RecordGenerator: try time [" + tryTime + "], try backTimeMS [" + tryBackTimeMS + "]");
    }


    private ConsumerWrap getConsumerWrap() {
        return consumerWrapFactory.getConsumerWrap(properties);
    }


    @Override
    public void run() {
        int haveTryTime = 0;
        String message = "first start";
        ConsumerWrap kafkaConsumerWrap = getConsumerWrap(message);
        while (!existed) {
            EtlRecordProcessor recordProcessor = context.getRecordProcessor();
            try {

                while (!existed) {
                    // kafka consumer is not threadsafe, so if you want commit checkpoint to kafka, commit it in same thread
                    mayCommitCheckpoint();
                    ConsumerRecords<byte[], byte[]> records = kafkaConsumerWrap.poll();
                    for (ConsumerRecord<byte[], byte[]> record : records) {
                        int offerTryCount = 0;
                        if (record.value() == null || record.value().length <= 2) {
                            // dStore may generate special mock record to push up consumer offset for next fetchRequest if all data is filtered
                            continue;
                        }
                        while (!recordProcessor.offer(1000, TimeUnit.MILLISECONDS, record) && !existed) {
                            if (++offerTryCount % 10 == 0) {
                                log.info("RecordGenerator: offer record has failed for a period (10s) [ " + record + "]");
                            }
                        }
                    }

                }
            } catch (Throwable e) {
                if (isErrorRecoverable(e) && haveTryTime++ < tryTime) {
                    log.warn("RecordGenerator: error meet cause " + e.getMessage() + ", recover time [" + haveTryTime + "]", e);
                    Util.sleepMS(tryBackTimeMS);
                    message = "reconnect";
                } else {
                    log.error("RecordGenerator: unrecoverable error  " + e.getMessage() + ", have try time [" + haveTryTime + "]", e);
                    this.existed = true;
                }
            } finally {
                Util.swallowErrorClose(kafkaConsumerWrap);
            }
        }

    }

    private void mayCommitCheckpoint() {
        if (null != toCommitCheckpoint) {
            commitCheckpoint(toCommitCheckpoint.getTopicPartition(), toCommitCheckpoint);
            toCommitCheckpoint = null;
        }
    }

    public void setToCommitCheckpoint(Checkpoint committedCheckpoint) {
        this.toCommitCheckpoint = committedCheckpoint;
    }

    private ConsumerWrap getConsumerWrap(String message) {
        ConsumerWrap kafkaConsumerWrap = getConsumerWrap();
        Checkpoint checkpoint = null;
        // we encourage  user impl their own checkpoint store, but plan b is also  supported
        metaStoreCenter.registerStore(KAFKA_STORE_NAME, new KafkaMetaStore(kafkaConsumerWrap.getRawConsumer()));
        boolean isForceUseCheckpoint = useCheckpointConfig.get();
        if (useCheckpointConfig.compareAndSet(true, false)) {
            log.info("RecordGenerator: force use initial checkpoint [{}] to start", checkpoint);
            checkpoint = initialCheckpoint;
        } else {
            checkpoint = getCheckpoint();
            if (null == checkpoint || Checkpoint.INVALID_STREAM_CHECKPOINT == checkpoint) {
                checkpoint = initialCheckpoint;
                log.info("RecordGenerator: use initial checkpoint [{}] to start", checkpoint);
            } else {
                log.info("RecordGenerator: load checkpoint from checkpoint store success, current checkpoint [{}]", checkpoint);
            }
        }
        switch (subscribeMode) {
            case SUBSCRIBE: {
                kafkaConsumerWrap.subscribeTopic(topicPartition, () -> {
                    if (isForceUseCheckpoint && initialCheckpoint != null) {
                        String formatDate = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(initialCheckpoint.getTimeStamp() * 1000);
                        log.warn("使用强制位点来消费topic数据,起始位置时间点:" + formatDate + " \t " + initialCheckpoint.getTimeStamp() + "\t offset:" + initialCheckpoint.getOffset() + "\t 通过[spring.dts.use-config-checkpoint-name]关闭!");
                        return initialCheckpoint;
                    }
                    Checkpoint ret = metaStoreCenter.seek(KAFKA_STORE_NAME, topicPartition, groupID);
                    if (null == ret) {
                        ret = initialCheckpoint;
                    } else {
                        String formatDate = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(ret.getTimeStamp() * 1000);
                        log.info("使用kafka存储的位点: [" + formatDate + "] \t timeStamp:" + ret.getTimeStamp() + "\t offset:" + ret.getOffset());
                    }
                    return ret;
                });
                break;
            }
            case ASSIGN: {
                kafkaConsumerWrap.assignTopic(topicPartition, checkpoint);
                break;
            }
            default: {
                throw new RuntimeException("RecordGenerator: unknown mode not support");
            }
        }

        log.info("RecordGenerator:" + message + ", checkpoint " + checkpoint);
        return kafkaConsumerWrap;
    }

    private Checkpoint getCheckpoint() {
        // use local checkpoint priority
        Checkpoint checkpoint = metaStoreCenter.seek(LOCAL_FILE_STORE_NAME, topicPartition, groupID);
        if (null == checkpoint) {
            checkpoint = metaStoreCenter.seek(KAFKA_STORE_NAME, topicPartition, groupID);
        }
        return checkpoint;

    }

    public void commitCheckpoint(TopicPartition topicPartition, Checkpoint checkpoint) {
        if (null != topicPartition && null != checkpoint) {
            metaStoreCenter.store(topicPartition, groupID, checkpoint);
        }
    }

    private boolean isErrorRecoverable(Throwable e) {
        return true;
    }

    public Checkpoint getInitialCheckpoint() {
        return initialCheckpoint;
    }

    @Override
    public void close() {
        existed = true;
    }

    private static enum ConsumerSubscribeMode {
        ASSIGN,
        SUBSCRIBE,
        UNKNOWN;
    }

    private ConsumerSubscribeMode parseConsumerSubscribeMode(String value) {
        if (StringUtils.equalsIgnoreCase("assign", value)) {
            return ConsumerSubscribeMode.ASSIGN;
        } else if (StringUtils.equalsIgnoreCase("subscribe", value)) {
            return ConsumerSubscribeMode.SUBSCRIBE;
        } else {
            throw new RuntimeException("RecordGenerator: unknown subscribe mode [" + value + "]");
        }
    }
}

