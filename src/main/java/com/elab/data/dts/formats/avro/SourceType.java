/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.elab.data.dts.formats.avro;
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public enum SourceType {
  MySQL, Oracle, SQLServer, PostgreSQL, MongoDB, Redis, DB2, PPAS, DRDS, HBASE, HDFS, FILE, OTHER  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"SourceType\",\"namespace\":\"com.elab.data.dts.formats.avro\",\"symbols\":[\"MySQL\",\"Oracle\",\"SQLServer\",\"PostgreSQL\",\"MongoDB\",\"Redis\",\"DB2\",\"PPAS\",\"DRDS\",\"HBASE\",\"HDFS\",\"FILE\",\"OTHER\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
}
