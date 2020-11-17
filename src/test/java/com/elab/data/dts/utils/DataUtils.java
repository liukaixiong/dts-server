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
        String tableName = DataParseUtils.getDDLTableName(binLog);
        System.out.println(tableName);
    }

    @Test
    public void testIndex() {
        String indexSQL = "ALTER TABLE `c_report_log` \n" +
                "ADD INDEX `idx_pub`(`brand_id`, `house_id`, `user_id`, `report_id`, `report_time`, `status`) USING BTREE";
        String tableName = DataParseUtils.getDDLTableName(indexSQL);
        System.out.println(tableName);
    }

    @Test
    public void testAlterTable() {
        String sql = "alter table customer_material modify intention_layout varchar(100) COMMENT '意向房型'";
        String tableName = DataParseUtils.getDDLTableName(sql);
        System.out.println(tableName);
    }


}
