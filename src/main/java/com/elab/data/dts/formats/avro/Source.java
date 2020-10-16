/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.elab.data.dts.formats.avro;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Source extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 8841831948671771482L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Source\",\"namespace\":\"com.elab.data.dts.formats.avro\",\"fields\":[{\"name\":\"sourceType\",\"type\":{\"type\":\"enum\",\"name\":\"SourceType\",\"symbols\":[\"MySQL\",\"Oracle\",\"SQLServer\",\"PostgreSQL\",\"MongoDB\",\"Redis\",\"DB2\",\"PPAS\",\"DRDS\",\"HBASE\",\"HDFS\",\"FILE\",\"OTHER\"]}},{\"name\":\"version\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"source datasource version information\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Source> ENCODER =
      new BinaryMessageEncoder<Source>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Source> DECODER =
      new BinaryMessageDecoder<Source>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<Source> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<Source> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Source>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this Source to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a Source from a ByteBuffer. */
  public static Source fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public SourceType sourceType;
  /** source datasource version information */
  @Deprecated public java.lang.String version;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Source() {}

  /**
   * All-args constructor.
   * @param sourceType The new value for sourceType
   * @param version source datasource version information
   */
  public Source(SourceType sourceType, java.lang.String version) {
    this.sourceType = sourceType;
    this.version = version;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return sourceType;
    case 1: return version;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: sourceType = (SourceType)value$; break;
    case 1: version = (java.lang.String)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'sourceType' field.
   * @return The value of the 'sourceType' field.
   */
  public SourceType getSourceType() {
    return sourceType;
  }

  /**
   * Sets the value of the 'sourceType' field.
   * @param value the value to set.
   */
  public void setSourceType(SourceType value) {
    this.sourceType = value;
  }

  /**
   * Gets the value of the 'version' field.
   * @return source datasource version information
   */
  public java.lang.String getVersion() {
    return version;
  }

  /**
   * Sets the value of the 'version' field.
   * source datasource version information
   * @param value the value to set.
   */
  public void setVersion(java.lang.String value) {
    this.version = value;
  }

  /**
   * Creates a new Source RecordBuilder.
   * @return A new Source RecordBuilder
   */
  public static Source.Builder newBuilder() {
    return new Source.Builder();
  }

  /**
   * Creates a new Source RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Source RecordBuilder
   */
  public static Source.Builder newBuilder(Source.Builder other) {
    return new Source.Builder(other);
  }

  /**
   * Creates a new Source RecordBuilder by copying an existing Source instance.
   * @param other The existing instance to copy.
   * @return A new Source RecordBuilder
   */
  public static Source.Builder newBuilder(Source other) {
    return new Source.Builder(other);
  }

  /**
   * RecordBuilder for Source instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Source>
    implements org.apache.avro.data.RecordBuilder<Source> {

    private SourceType sourceType;
    /** source datasource version information */
    private java.lang.String version;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(Source.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.sourceType)) {
        this.sourceType = data().deepCopy(fields()[0].schema(), other.sourceType);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.version)) {
        this.version = data().deepCopy(fields()[1].schema(), other.version);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Source instance
     * @param other The existing instance to copy.
     */
    private Builder(Source other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.sourceType)) {
        this.sourceType = data().deepCopy(fields()[0].schema(), other.sourceType);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.version)) {
        this.version = data().deepCopy(fields()[1].schema(), other.version);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'sourceType' field.
      * @return The value.
      */
    public SourceType getSourceType() {
      return sourceType;
    }

    /**
      * Sets the value of the 'sourceType' field.
      * @param value The value of 'sourceType'.
      * @return This builder.
      */
    public Source.Builder setSourceType(SourceType value) {
      validate(fields()[0], value);
      this.sourceType = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'sourceType' field has been set.
      * @return True if the 'sourceType' field has been set, false otherwise.
      */
    public boolean hasSourceType() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'sourceType' field.
      * @return This builder.
      */
    public Source.Builder clearSourceType() {
      sourceType = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'version' field.
      * source datasource version information
      * @return The value.
      */
    public java.lang.String getVersion() {
      return version;
    }

    /**
      * Sets the value of the 'version' field.
      * source datasource version information
      * @param value The value of 'version'.
      * @return This builder.
      */
    public Source.Builder setVersion(java.lang.String value) {
      validate(fields()[1], value);
      this.version = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'version' field has been set.
      * source datasource version information
      * @return True if the 'version' field has been set, false otherwise.
      */
    public boolean hasVersion() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'version' field.
      * source datasource version information
      * @return This builder.
      */
    public Source.Builder clearVersion() {
      version = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Source build() {
      try {
        Source record = new Source();
        record.sourceType = fieldSetFlags()[0] ? this.sourceType : (SourceType) defaultValue(fields()[0]);
        record.version = fieldSetFlags()[1] ? this.version : (java.lang.String) defaultValue(fields()[1]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Source>
    WRITER$ = (org.apache.avro.io.DatumWriter<Source>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Source>
    READER$ = (org.apache.avro.io.DatumReader<Source>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
