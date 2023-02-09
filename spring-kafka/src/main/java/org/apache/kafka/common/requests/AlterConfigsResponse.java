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

import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.message.AlterConfigsResponseData;
import org.apache.kafka.common.protocol.ByteBufferAccessor;
import org.apache.kafka.common.protocol.Errors;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AlterConfigsResponse extends AbstractResponse {

    private final AlterConfigsResponseData data;

    public AlterConfigsResponse(AlterConfigsResponseData data) {
        super(null);
        this.data = data;
    }

    public Map<ConfigResource, ApiError> errors() {
        return data.responses().stream().collect(Collectors.toMap(
            response -> new ConfigResource(
                    ConfigResource.Type.forId(response.resourceType()),
                    response.resourceName()),
            response -> new ApiError(Errors.forCode(response.errorCode()), response.errorMessage())
        ));
    }

    public Map<Errors, Integer> errorCounts() {
        Map<Errors, Integer> counts = new HashMap<>();
        errors().values().forEach(entry -> {
                    Errors errors = Errors.forCode(entry.error().code());
                    Integer count = counts.get(errors);
                    counts.put(errors, count == null ? 1 : ++ count);
                }
        );
        return counts;
    }

    public int throttleTimeMs() {
        return data.throttleTimeMs();
    }

    public AlterConfigsResponseData data() {
        return data;
    }

    public static AlterConfigsResponse parse(ByteBuffer buffer, short version) {
        return new AlterConfigsResponse(new AlterConfigsResponseData(new ByteBufferAccessor(buffer), version));
    }

    public boolean shouldClientThrottle(short version) {
        return version >= 1;
    }
}
