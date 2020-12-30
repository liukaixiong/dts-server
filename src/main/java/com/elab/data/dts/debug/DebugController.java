package com.elab.data.dts.debug;

import com.alibaba.fastjson.JSON;
import com.elab.data.dts.components.DebugValueComponent;
import com.elab.data.dts.config.props.DTSProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Module 调试器
 * @Description 调试器
 * @Author liukaixiong
 * @Date 2020/12/29 17:37
 */
@RestController
public class DebugController {

    private String defaultToken = "elab2020";

    @Autowired
    private DebugValueComponent debugValueComponent;

    @Autowired
    private DTSProperties dtsProperties;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/debug/register/tableName/add", produces = "application/json;charset=UTF-8")
    public String tableNameAdd(@RequestParam("token") String token, @RequestParam("tb") String tableName) {
        if (!checkToken(token)) {
            return "token 验证失败";
        }
        debugValueComponent.registerExcludeTableName(tableName);
        logger.info("临时注册过滤表 : " + tableName);
        return "true";
    }

    @GetMapping(value = "/debug/register/tableName/list", produces = "application/json;charset=UTF-8")
    public String tableNameList(@RequestParam("token") String token) {
        if (!checkToken(token)) {
            return "token 验证失败";
        }
        Set<String> excludeTableName = debugValueComponent.getExcludeTableName();

        Map<String, List<String>> excludeDataInfo = dtsProperties.getExcludeDataInfo();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("config", excludeDataInfo);
        resultMap.put("temp", excludeTableName);

        String tableList = JSON.toJSONString(resultMap);
        logger.info("获取过滤表集合 : " + tableList);
        return tableList;
    }

    @GetMapping(value = "/debug/register/tableName/remove", produces = "application/json;charset=UTF-8")
    public String tableNameRemove(@RequestParam("token") String token, @RequestParam("tb") String tableName) {
        if (!checkToken(token)) {
            return "token 验证失败";
        }
        boolean result = debugValueComponent.clearExcludeTableName(tableName);
        logger.info("删除临时表 : " + tableName);
        return result + "";
    }

    @GetMapping(value = "/debug/register/tableName/clear", produces = "application/json;charset=UTF-8")
    public String tableNameClear(@RequestParam("token") String token) {
        if (!checkToken(token)) {
            return "token 验证失败";
        }
        debugValueComponent.clearAllExcludeTableName();
        return "true";
    }

    public boolean checkToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        return defaultToken.equals(token);
    }

}
