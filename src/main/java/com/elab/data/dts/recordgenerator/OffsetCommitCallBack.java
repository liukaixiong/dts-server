package com.elab.data.dts.recordgenerator;

import org.apache.kafka.common.TopicPartition;

public interface OffsetCommitCallBack {
    void commit(TopicPartition tp, long timestamp, long offset, String metadata);
}
