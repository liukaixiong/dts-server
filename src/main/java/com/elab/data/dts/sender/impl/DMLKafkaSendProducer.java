package com.elab.data.dts.sender.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * kafka推送消息
 *
 * @author ： liukx
 * @time ： 2020/10/16 - 14:08
 */
@Component
@Lazy
public class DMLKafkaSendProducer extends AbstractKafkaSender {

    @Override
    protected String topic() {
        return kafkaProperties.getTemplate().getDefaultTopic();
    }
}
