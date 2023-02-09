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

import org.apache.kafka.common.message.DescribeUserScramCredentialsRequestData;
import org.apache.kafka.common.message.DescribeUserScramCredentialsResponseData;
import org.apache.kafka.common.protocol.ByteBufferAccessor;

import java.nio.ByteBuffer;

public class DescribeUserScramCredentialsRequest extends AbstractRequest {

    @Override
    public AbstractResponse getErrorResponse(Throwable e) {
        return getErrorResponse(0, e);
    }

    public static class Builder extends AbstractRequest.Builder<DescribeUserScramCredentialsRequest> {
        private final DescribeUserScramCredentialsRequestData data;

        public Builder(DescribeUserScramCredentialsRequestData data) {
            super(null);
            this.data = data;
        }

        public DescribeUserScramCredentialsRequest build(short version) {
            return new DescribeUserScramCredentialsRequest(data, version);
        }

        @Override
        public String toString() {
            return data.toString();
        }

        @Override
        public DescribeUserScramCredentialsRequest build() {
            return build((short) 0);
        }
    }

    private final DescribeUserScramCredentialsRequestData data;
    private final short version;

    private DescribeUserScramCredentialsRequest(DescribeUserScramCredentialsRequestData data, short version) {
        super(null, version);
        this.data = data;
        this.version = version;
    }

    public static DescribeUserScramCredentialsRequest parse(ByteBuffer buffer, short version) {
        return new DescribeUserScramCredentialsRequest(new DescribeUserScramCredentialsRequestData(
                new ByteBufferAccessor(buffer), version), version);
    }

    public DescribeUserScramCredentialsRequestData data() {
        return data;
    }

    public AbstractResponse getErrorResponse(int throttleTimeMs, Throwable e) {
        ApiError apiError = ApiError.fromThrowable(e);
        DescribeUserScramCredentialsResponseData response = new DescribeUserScramCredentialsResponseData()
                .setThrottleTimeMs(throttleTimeMs)
                .setErrorCode(apiError.error().code())
                .setErrorMessage(apiError.message());
        for (DescribeUserScramCredentialsRequestData.UserName user : data.users()) {
            response.results().add(new DescribeUserScramCredentialsResponseData.DescribeUserScramCredentialsResult()
                    .setErrorCode(apiError.error().code())
                    .setErrorMessage(apiError.message()));
        }
        return new DescribeUserScramCredentialsResponse(response);
    }
}
