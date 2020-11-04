package com.elab.data.dts.utils;


import org.junit.Test;

/**
 * @Module TODO
 * @Description TODO
 * @Author Administrator
 * @Date 2020/10/20 14:45
 */
public class DataUtils {

    @Test
    public void testTable() {
        String binLog = "ALTER TABLE `marketing_db_prod`.`banner` \n" +
                "MODIFY COLUMN `title` varchar(53) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题' AFTER `id`";
        int startIndex = binLog.indexOf(".`") + 2;
        int endIndex = binLog.indexOf("` \n");
        String tableName = binLog.substring(startIndex, endIndex);
        System.out.println(tableName);
    }

    @Test
    public void testIndex(){
        String indexSQL = "ALTER TABLE `c_report_log` \n" +
                "ADD INDEX `idx_pub`(`brand_id`, `house_id`, `user_id`, `report_id`, `report_time`, `status`) USING BTREE";
        int startIndex = indexSQL.indexOf("`") + 1;
        int endIndex = indexSQL.indexOf("` ");
        String tableName = indexSQL.substring(startIndex, endIndex);
        System.out.println(tableName);

    }


}
