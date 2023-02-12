/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.requests;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.message.ListOffsetsResponseData;
import org.apache.kafka.common.message.ListOffsetsResponseData.ListOffsetsPartitionResponse;
import org.apache.kafka.common.message.ListOffsetsResponseData.ListOffsetsTopicResponse;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.ByteBufferAccessor;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.protocol.ProtoUtils;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.record.RecordBatch;

import static org.apache.kafka.common.utils.ExtraUtil.updateErrorCounts;

/**
 * Possible error codes:
 *
 * - {@link Errors#UNSUPPORTED_FOR_MESSAGE_FORMAT} If the message format does not support lookup by timestamp
 * - {@link Errors#TOPIC_AUTHORIZATION_FAILED} If the user does not have DESCRIBE access to a requested topic
 * - {@link Errors#REPLICA_NOT_AVAILABLE} If the request is received by a broker with version < 2.6 which is not a replica
 *     matches the known leader epoch on the broker or is empty
 * - {@link Errors#UNKNOWN_TOPIC_OR_PARTITION} If the broker does not have metadata for a topic or partition
 * - {@link Errors#LEADER_NOT_AVAILABLE} The leader's HW has not caught up after recent election (v4 protocol)
 */
public class ListOffsetsResponse extends AbstractResponse {
    public static final long UNKNOWN_TIMESTAMP = -1L;
    public static final long UNKNOWN_OFFSET = -1L;
    public static final int UNKNOWN_EPOCH = RecordBatch.NO_PARTITION_LEADER_EPOCH;

    private final ListOffsetsResponseData data;

    public ListOffsetsResponse(ListOffsetsResponseData data) {
        super(new Struct(ProtoUtils.requestSchema(ApiKeys.LIST_OFFSETS.id, 0)));
        this.data = data;
    }

    public int throttleTimeMs() {
        return data.throttleTimeMs();
    }

    public ListOffsetsResponseData data() {
        return data;
    }

    public List<ListOffsetsTopicResponse> topics() {
        return data.topics();
    }

    public Map<Errors, Integer> errorCounts() {
        Map<Errors, Integer> errorCounts = new HashMap<>();
        topics().forEach(topic ->
            topic.partitions().forEach(partition ->
                updateErrorCounts(errorCounts, Errors.forCode(partition.errorCode()))
            )
        );
        return errorCounts;
    }

    public static ListOffsetsResponse parse(ByteBuffer buffer, short version) {
        return new ListOffsetsResponse(new ListOffsetsResponseData(new ByteBufferAccessor(buffer), version));
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public boolean shouldClientThrottle(short version) {
        return version >= 3;
    }

    public static ListOffsetsTopicResponse singletonListOffsetsTopicResponse(TopicPartition tp, Errors error, long timestamp, long offset, int epoch) {
        return new ListOffsetsTopicResponse()
                 .setName(tp.topic())
                 .setPartitions(Collections.singletonList(new ListOffsetsPartitionResponse()
                         .setPartitionIndex(tp.partition())
                         .setErrorCode(error.code())
                         .setTimestamp(timestamp)
                         .setOffset(offset)
                         .setLeaderEpoch(epoch)));
    }
}