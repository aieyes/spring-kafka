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

import org.apache.kafka.common.message.ListTransactionsRequestData;
import org.apache.kafka.common.message.ListTransactionsResponseData;
import org.apache.kafka.common.protocol.ByteBufferAccessor;
import org.apache.kafka.common.protocol.Errors;

import java.nio.ByteBuffer;

public class ListTransactionsRequest extends AbstractRequest {
    @Override
    public AbstractResponse getErrorResponse(Throwable e) {
        return getErrorResponse(0, e);
    }

    public static class Builder extends AbstractRequest.Builder<ListTransactionsRequest> {
        public final ListTransactionsRequestData data;

        public Builder(ListTransactionsRequestData data) {
            super(null);
            this.data = data;
        }

        public ListTransactionsRequest build(short version) {
            return new ListTransactionsRequest(data, version);
        }

        @Override
        public String toString() {
            return data.toString();
        }

        @Override
        public ListTransactionsRequest build() {
            return build((short) 0);
        }
    }

    private final ListTransactionsRequestData data;

    private ListTransactionsRequest(ListTransactionsRequestData data, short version) {
        super(null, version);
        this.data = data;
    }

    public ListTransactionsRequestData data() {
        return data;
    }

    public ListTransactionsResponse getErrorResponse(int throttleTimeMs, Throwable e) {
        Errors error = Errors.forException(e);
        ListTransactionsResponseData response = new ListTransactionsResponseData()
            .setErrorCode(error.code())
            .setThrottleTimeMs(throttleTimeMs);
        return new ListTransactionsResponse(response);
    }

    public static ListTransactionsRequest parse(ByteBuffer buffer, short version) {
        return new ListTransactionsRequest(new ListTransactionsRequestData(
            new ByteBufferAccessor(buffer), version), version);
    }

    public String toString(boolean verbose) {
        return data.toString();
    }

}
