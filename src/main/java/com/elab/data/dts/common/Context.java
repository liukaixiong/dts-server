package com.elab.data.dts.common;

import com.elab.data.dts.recordgenerator.RecordGenerator;
import com.elab.data.dts.recordprocessor.EtlRecordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Context {
    private static final Logger log = LoggerFactory.getLogger(Context.class);
    private RecordGenerator streamSource;
    private EtlRecordProcessor recordProcessor;
    public void setStreamSource(RecordGenerator streamSource) {
        this.streamSource = streamSource;
    }

    public EtlRecordProcessor getRecordProcessor() {
        return recordProcessor;
    }

    public  void setRecordProcessor(EtlRecordProcessor recordProcessor) {
        this.recordProcessor = recordProcessor;
    }

}
