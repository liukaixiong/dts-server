package com.elab.data.dts.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author ： liukx
 * @time ： 2020/10/16 - 12:29
 */
public class Test {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Test.class);
        System.setProperty("java.security.auth.login.config", "E:/temp/java/kafka_client_jaas.conf");

        //加载kafka.properties。
//        Properties kafkaProperties = JavaKafkaConfigurer.getKafkaProperties();

        Properties props = getProperties2();
        //构造Producer对象，注意，该对象是线程安全的，一般来说，一个进程内一个Producer对象即可。
        //如果想提高性能，可以多构造几个对象，但不要太多，最好不要超过5个。
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        //构造一个Kafka消息。
        String topic = "prod_rds"; //消息所属的Topic，请在控制台申请之后，填写在这里。
        String value = "this is the message's value"; //消息的内容。
        System.out.println("准备发送...");
        try {
            for (int i = 0; i < 10; i++) {
                ProducerRecord<String, String> kafkaMessage = new ProducerRecord<String, String>(topic, value + ": " + 1);
                Future<RecordMetadata> send = producer.send(kafkaMessage);
                RecordMetadata recordMetadata = send.get();
                logger.info("Produce ok:" + recordMetadata.toString());
            }

            //批量获取Future对象可以加快速度,。但注意，批量不要太大。
//            List<Future<RecordMetadata>> futures = new ArrayList<Future<RecordMetadata>>(128);
//            for (int i = 0; i < 2; i++) {
//                //发送消息，并获得一个Future对象。
//                ProducerRecord<String, String> kafkaMessage = new ProducerRecord<String, String>(topic, value + ": " + i);
//                Future<RecordMetadata> metadataFuture = producer.send(kafkaMessage);
//                futures.add(metadataFuture);
//
//            }
//           // producer.flush();
//            for (Future<RecordMetadata> future : futures) {
//                //同步获得Future对象的结果。
//                try {
//                    RecordMetadata recordMetadata = future.get();
//                    System.out.println("Produce ok:" + recordMetadata.toString());
//                } catch (Throwable t) {
//                    t.printStackTrace();
//                }
//            }
        } catch (Exception e) {
            //客户端内部重试之后，仍然发送失败，业务要应对此类错误。
            System.out.println("error occurred");
            e.printStackTrace();
        }
    }

    private static Properties getProperties1() {
        Properties props = new Properties();
        //设置接入点，请通过控制台获取对应Topic的接入点。
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9093");

        //Kafka消息的序列化方式。
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //请求的最长等待时间。
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        //设置客户端内部重试次数。
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        //设置客户端内部重试间隔。
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);
        return props;
    }

    private static Properties getProperties2() {
        Properties props = getProperties1();
        //设置SSL根证书的路径，请记得将XXX修改为自己的路径。
        //与SASL路径类似，该文件也不能被打包到JAR中。
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "E:/temp/java/kafka.client.truststore.jks");
        //根证书存储的密码，保持不变。
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "KafkaOnsClient");
        //接入协议，目前支持使用SASL_SSL协议接入。
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        //SASL鉴权方式，保持不变。
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        //两次Poll之间的最大允许间隔。
        //消费者超过该值没有返回心跳，服务端判断消费者处于非存活状态，服务端将消费者从Consumer Group移除并触发Rebalance，默认30s。
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        //每次Poll的最大数量。
        //注意该值不要改得太大，如果Poll太多数据，而不能在下次Poll之前消费完，则会触发一次负载均衡，产生卡顿。
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
        //消息的反序列化方式。
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //当前消费实例所属的消费组，请在控制台申请之后填写。
        //属于同一个组的消费实例，会负载消费消息。
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getProperty("group.id"));
        //构造消息对象，也即生成一个消费实例。

        //Hostname校验改成空。
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        return props;
    }
}
