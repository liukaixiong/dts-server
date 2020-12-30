package com.elab.data.dts.components;

import com.elab.data.dts.boot.Boot;
import com.elab.data.dts.common.RecordListener;
import com.elab.data.dts.config.props.DTSProperties;
import com.elab.data.dts.recordgenerator.Names;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * 启动回调
 *
 * @author ： liukx
 * @time ： 2020/9/22 - 17:15
 */
@Component
public class StartCallback implements ApplicationRunner {

    @Autowired
    private DTSProperties dtsProperties;

    @Autowired
    private Map<String, RecordListener> recordListenerMap;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Boot.boot(getProperties(dtsProperties), recordListenerMap);
    }

    public static Properties getProperties(DTSProperties pros) {
        Properties properties = new Properties();
        // user password and sid for auth
        properties.setProperty(Names.USER_NAME, pros.getUsername());
        properties.setProperty(Names.PASSWORD_NAME, pros.getPassword());
        properties.setProperty(Names.SID_NAME, pros.getSidName());
        // kafka consumer group general same with sidrdsdt_dtsacct
        properties.setProperty(Names.GROUP_NAME, pros.getGroupName());
        // topic to consume, partition is 0
        properties.setProperty(Names.KAFKA_TOPIC, pros.getKafkaTopic());
        // kafka broker url
        properties.setProperty(Names.KAFKA_BROKER_URL_NAME, pros.getKafkaBrokerUrlName());
        // initial checkpoint for first seek(a timestamp to set, eg 1566180200 if you want (Mon Aug 19 10:03:21 CST 2019))
        //long startTimestamp = System.currentTimeMillis() / 1000;
//        System.out.println("-------->" + startTimestamp);
        if (pros.getInitialCheckpointName() != null) {
            properties.setProperty(Names.INITIAL_CHECKPOINT_NAME, pros.getInitialCheckpointName());
        } else {
            properties.setProperty(Names.INITIAL_CHECKPOINT_NAME, (System.currentTimeMillis() / 1000) + "");
        }
        // if force use config checkpoint when start. for checkpoint reset
        properties.setProperty(Names.USE_CONFIG_CHECKPOINT_NAME, pros.getUseConfigCheckpointName());
        // use consumer assign or subscribe interface
        // when use subscribe mode, group config is required. kafka consumer group is enabled
        // assign - subscribe
        properties.setProperty(Names.SUBSCRIBE_MODE_NAME, pros.getSubscribeModeName());
        properties.setProperty(Names.MAX_POLL_RECORDS, pros.getMaxPollRecords().toString());
        properties.setProperty(Names.MAX_POLL_INTERVAL_MS_CONFIG, pros.getMaxPollIntervalMs().toString());
        return properties;
    }
}
