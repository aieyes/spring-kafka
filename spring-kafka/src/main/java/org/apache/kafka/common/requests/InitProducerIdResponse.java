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

import org.apache.kafka.common.message.InitProducerIdResponseData;
import org.apache.kafka.common.protocol.ByteBufferAccessor;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.utils.ExtraUtil;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Possible error codes:
 *
 *   - {@link Errors#CLUSTER_AUTHORIZATION_FAILED}
 */
public class InitProducerIdResponse extends AbstractResponse {
    private final InitProducerIdResponseData data;

    public InitProducerIdResponse(InitProducerIdResponseData data) {
        super(null);
        this.data = data;
    }

    public int throttleTimeMs() {
        return data.throttleTimeMs();
    }

    public Map<Errors, Integer> errorCounts() {
        return ExtraUtil.errorCounts(Errors.forCode(data.errorCode()));
    }

    public InitProducerIdResponseData data() {
        return data;
    }

    public static InitProducerIdResponse parse(ByteBuffer buffer, short version) {
        return new InitProducerIdResponse(new InitProducerIdResponseData(new ByteBufferAccessor(buffer), version));
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public Errors error() {
        return Errors.forCode(data.errorCode());
    }

    public boolean shouldClientThrottle(short version) {
        return version >= 1;
    }
}
