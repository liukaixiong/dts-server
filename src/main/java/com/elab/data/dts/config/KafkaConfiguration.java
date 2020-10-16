package com.elab.data.dts.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * kafka日志集成
 *
 * @author ： liukx
 * @time ： 2019/8/7 - 14:46
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfiguration {

    //    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.ssl.truststore-location}")
//    private String truststoreLocation;
//
    @Value("${java.security.auth.login.config:kafka_client_jaas.conf}")
    private String authLoginConfig;
//

    @Value("${spring.profiles.active}")
    private String profiles;

    @Value("${java.security.auth.login.truststore-location}")
    private String truststoreLocation;

    @Bean
    public KafkaProducer<String, String> kafkaProducer(@Autowired KafkaProperties kafkaProperties) {
        Properties props = new Properties();
        //设置接入点，请通过控制台获取对应 Topic 的接入点
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        //消息队列 Kafka 消息的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //请求的最长等待时间
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        //设置客户端内部重试次数。
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        //设置客户端内部重试间隔。
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        if ("dev".equals(profiles)) {
            // 外网配置
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "KafkaOnsClient");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            //SASL鉴权方式，保持不变
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            System.setProperty("java.security.auth.login.config", authLoginConfig);
        }
        //构造 Producer 对象，注意，该对象是线程安全的。
        //一般来说，一个进程内一个 Producer 对象即可。如果想提高性能，可构造多个对象，但最好不要超过 5 个
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;
    }
}
