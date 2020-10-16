package com.elab.data.dts.config;

import com.elab.data.dts.config.props.DTSProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * dts配置
 *
 * @author ： liukx
 * @time ： 2020/9/22 - 15:06
 */
@Configuration
@EnableConfigurationProperties(value = {DTSProperties.class})
public class DTSConfguration {


}
