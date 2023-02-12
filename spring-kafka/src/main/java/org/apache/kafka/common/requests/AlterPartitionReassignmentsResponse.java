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

import org.apache.kafka.common.message.AlterPartitionReassignmentsResponseData;
import org.apache.kafka.common.protocol.ByteBufferAccessor;
import org.apache.kafka.common.protocol.Errors;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class AlterPartitionReassignmentsResponse extends AbstractResponse {

    private final AlterPartitionReassignmentsResponseData data;

    public AlterPartitionReassignmentsResponse(AlterPartitionReassignmentsResponseData data) {
        super(null);
        this.data = data;
    }

    public static AlterPartitionReassignmentsResponse parse(ByteBuffer buffer, short version) {
        return new AlterPartitionReassignmentsResponse(
                new AlterPartitionReassignmentsResponseData(new ByteBufferAccessor(buffer), version));
    }

    public AlterPartitionReassignmentsResponseData data() {
        return data;
    }

    public boolean shouldClientThrottle(short version) {
        return true;
    }

    public int throttleTimeMs() {
        return data.throttleTimeMs();
    }

    public Map<Errors, Integer> errorCounts() {
        Map<Errors, Integer> counts = new HashMap<>();
        counts.put(Errors.forCode(data.errorCode()), 1);

        data.responses().forEach(topicResponse ->
                topicResponse.partitions().forEach(partitionResponse -> {
                            Errors errors = Errors.forCode(partitionResponse.errorCode());
                            Integer count = counts.get(errors);
                            counts.put(errors, ++count);
                        }
                ));
        return counts;
    }
}