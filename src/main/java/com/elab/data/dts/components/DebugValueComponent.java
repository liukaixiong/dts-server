package com.elab.data.dts.components;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Module 调试
 * @Description 临时存储器
 * @Author liukaixiong
 * @Date 2020/12/30 10:46
 */
@Component
public class DebugValueComponent {

    private Map<String, Object> dataMap = new HashMap<>();

    private String excludeTablePrefix = "excludeTablePrefix";

    /**
     * 获取临时注册表
     *
     * @return
     */
    public Set<String> getExcludeTableName() {
        return (Set<String>) dataMap.getOrDefault(excludeTablePrefix, new HashSet<>());
    }


    /**
     * 临时注册表名
     *
     * @param tableName
     */
    public void registerExcludeTableName(String tableName) {
        Set<String> dataList = getExcludeTableName();
        dataList.add(tableName);
        coverMap(excludeTablePrefix, dataList);
    }

    private void coverMap(String key, Object value) {
        dataMap.put(key, value);
    }

    /**
     * 删除特定表
     *
     * @param tableName
     * @return
     */
    public boolean clearExcludeTableName(String tableName) {
        Set<String> dataList = getExcludeTableName();
        dataList.remove(tableName);
        coverMap(excludeTablePrefix, dataList);
        return true;
    }

    /**
     * 清空所有数据
     */
    public void clearAllExcludeTableName() {
        Set<String> dataList = getExcludeTableName();
        dataList.clear();
        coverMap(excludeTablePrefix, dataList);
    }

    public boolean isExcludeTableName(String tableName) {
        Set<String> dataList = getExcludeTableName();
        return dataList.contains(tableName);
    }
}
