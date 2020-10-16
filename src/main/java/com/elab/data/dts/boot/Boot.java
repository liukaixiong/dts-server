package com.elab.data.dts.boot;

import com.elab.data.dts.common.*;
import com.elab.data.dts.recordgenerator.ConsumerWrapFactory;
import com.elab.data.dts.recordgenerator.Names;
import com.elab.data.dts.recordgenerator.OffsetCommitCallBack;
import com.elab.data.dts.recordgenerator.RecordGenerator;
import com.elab.data.dts.recordprocessor.EtlRecordProcessor;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


public class Boot {
    private static final Logger log = LoggerFactory.getLogger(Boot.class);
    private static final AtomicBoolean existed = new AtomicBoolean(false);


    public static void boot(String configFile, Map<String, RecordListener> recordListeners) {
        boot(loadConfig(configFile), recordListeners);
    }

    public static void boot(Properties properties, Map<String, RecordListener> recordListeners) {
        // first init log4j
        // 使用SpringBoot的默认日志体系
        // initLog4j();
        Util.require(null != recordListeners && !recordListeners.isEmpty(), "record listener required");
        Context context = getStreamContext(properties);
        // check config
        checkConfig(properties);
        RecordGenerator recordGenerator = getRecordGenerator(context, properties);
        EtlRecordProcessor etlRecordProcessor = getEtlRecordProcessor(context, properties, recordGenerator);
        recordListeners.forEach((k, v) -> {
            log.info("Boot: register record listener " + k);
            etlRecordProcessor.registerRecordListener(k, v);
        });
        registerSignalHandler(context);
        List<WorkThread> startStream = startWorker(etlRecordProcessor, recordGenerator);
        // Spring 默认会启动容器,而
//        while (!existed.get() ) {
//            Util.sleepMS(1000);
//        }
//        log.info("StreamBoot: shutting down...");
//        for (WorkThread workThread : startStream) {
//            workThread.stop();
//        }

    }

    private static List<WorkThread> startWorker(EtlRecordProcessor etlRecordProcessor, RecordGenerator recordGenerator) {
        List<WorkThread> ret = new LinkedList<>();
        ret.add(new WorkThread(etlRecordProcessor));
        ret.add(new WorkThread(recordGenerator));
        for (WorkThread workThread : ret) {
            workThread.start();
        }
        return ret;
    }

    private static void registerSignalHandler(Context context) {
        SignalHandler signalHandler = new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                // SIG_INT
                if (signal.getNumber() == 2) {
                    existed.compareAndSet(false, true);
                }
            }
        };
        Signal.handle(new Signal("INT"), signalHandler);
    }

    private static Context getStreamContext(Properties properties) {
        Context ret =  new Context();
        return ret;
    }


    // offset@timestamp or timestamp
    private static Checkpoint parseCheckpoint(String checkpoint) {
        Util.require(null != checkpoint, "checkpoint should not be null");
        String[] offsetAndTS = checkpoint.split("@");
        Checkpoint streamCheckpoint = null;
        if (offsetAndTS.length == 1) {
            streamCheckpoint =  new Checkpoint(null, Long.valueOf(offsetAndTS[0]), -1, "");
        } else if (offsetAndTS.length >= 2) {
            streamCheckpoint =  new Checkpoint(null, Long.valueOf(offsetAndTS[0]), Long.valueOf(offsetAndTS[1]), "");
        }
        return streamCheckpoint;
    }

    private static RecordGenerator getRecordGenerator(Context context, Properties properties) {

        RecordGenerator recordGenerator = new RecordGenerator(properties, context,
                parseCheckpoint(properties.getProperty(Names.INITIAL_CHECKPOINT_NAME)),
                new ConsumerWrapFactory.KafkaConsumerWrapFactory());
        context.setStreamSource(recordGenerator);
        return recordGenerator;
    }

    private static EtlRecordProcessor getEtlRecordProcessor(Context context, Properties properties, RecordGenerator recordGenerator) {

        EtlRecordProcessor etlRecordProcessor = new EtlRecordProcessor(new OffsetCommitCallBack() {
            @Override
            public void commit(TopicPartition tp, long timestamp, long offset, String metadata) {
                recordGenerator.setToCommitCheckpoint(new Checkpoint(tp, timestamp, offset, metadata));
            }
        }, context);
        context.setRecordProcessor(etlRecordProcessor);
        return etlRecordProcessor;
    }


    // may check some fo config value
    private static void checkConfig(Properties properties) {
        Util.require(null != properties.getProperty(Names.USER_NAME), "use should supplied");
        Util.require(null != properties.getProperty(Names.PASSWORD_NAME), "password should supplied");
        Util.require(null != properties.getProperty(Names.SID_NAME), "sid should supplied");
        Util.require(null != properties.getProperty(Names.KAFKA_TOPIC), "kafka topic should supplied");
        Util.require(null != properties.getProperty(Names.KAFKA_BROKER_URL_NAME), "broker url should supplied");
    }

//    private static Properties initLog4j() {
//        Properties properties = new Properties();
//        InputStream log4jInput = null;
//        try {
//            log4jInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("log4j.properties");
//            PropertyConfigurator.configure(log4jInput);
//        } catch (Exception e) {
//        } finally {
//            swallowErrorClose(log4jInput);
//        }
//        return properties;
//    }


    private static Properties loadConfig(String filePath) {
        Properties ret = new Properties();
        InputStream toLoad = null;
        try {
            toLoad = new BufferedInputStream(new FileInputStream(filePath));
            ret.load(toLoad);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }  finally {
            Util.swallowErrorClose(toLoad);
        }
        return ret;
    }
}
