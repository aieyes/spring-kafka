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
package org.apache.kafka.clients.admin.internals;

import org.apache.kafka.clients.admin.TransactionDescription;
import org.apache.kafka.clients.admin.TransactionState;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.message.DescribeTransactionsRequestData;
import org.apache.kafka.common.message.DescribeTransactionsResponseData;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.AbstractResponse;
import org.apache.kafka.common.requests.DescribeTransactionsRequest;
import org.apache.kafka.common.requests.DescribeTransactionsResponse;
import org.apache.kafka.common.requests.FindCoordinatorRequest;
import org.apache.kafka.common.requests.FindCoordinatorRequest.CoordinatorType;
import org.apache.kafka.common.utils.LogContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

public class DescribeTransactionsHandler extends AdminApiHandler.Batched<CoordinatorKey, TransactionDescription> {
    private final Logger log;
    private final AdminApiLookupStrategy<CoordinatorKey> lookupStrategy;

    public DescribeTransactionsHandler(
        LogContext logContext
    ) {
        this.log = logContext.logger(DescribeTransactionsHandler.class);
        this.lookupStrategy = new CoordinatorStrategy(CoordinatorType.TRANSACTION, logContext);
    }

    public static AdminApiFuture.SimpleAdminApiFuture<CoordinatorKey, TransactionDescription> newFuture(
        Collection<String> transactionalIds
    ) {
        return AdminApiFuture.forKeys(buildKeySet(transactionalIds));
    }

    private static Set<CoordinatorKey> buildKeySet(Collection<String> transactionalIds) {
        return transactionalIds.stream()
            .map(CoordinatorKey::byTransactionalId)
            .collect(Collectors.toSet());
    }

    @Override
    public String apiName() {
        return "describeTransactions";
    }

    @Override
    public AdminApiLookupStrategy<CoordinatorKey> lookupStrategy() {
        return lookupStrategy;
    }

    @Override
    public DescribeTransactionsRequest.Builder buildBatchedRequest(
        int brokerId,
        Set<CoordinatorKey> keys
    ) {
        DescribeTransactionsRequestData request = new DescribeTransactionsRequestData();
        List<String> transactionalIds = keys.stream().map(key -> {
            if (key.type != FindCoordinatorRequest.CoordinatorType.TRANSACTION) {
                throw new IllegalArgumentException("Invalid group coordinator key " + key +
                    " when building `DescribeTransaction` request");
            }
            return key.idValue;
        }).collect(Collectors.toList());
        request.setTransactionalIds(transactionalIds);
        return new DescribeTransactionsRequest.Builder(request);
    }

    @Override
    public ApiResult<CoordinatorKey, TransactionDescription> handleResponse(
        Node broker,
        Set<CoordinatorKey> keys,
        AbstractResponse abstractResponse
    ) {
        DescribeTransactionsResponse response = (DescribeTransactionsResponse) abstractResponse;
        Map<CoordinatorKey, TransactionDescription> completed = new HashMap<>();
        Map<CoordinatorKey, Throwable> failed = new HashMap<>();
        List<CoordinatorKey> unmapped = new ArrayList<>();

        for (DescribeTransactionsResponseData.TransactionState transactionState : response.data().transactionStates()) {
            CoordinatorKey transactionalIdKey = CoordinatorKey.byTransactionalId(
                transactionState.transactionalId());
            if (!keys.contains(transactionalIdKey)) {
                log.warn("Response included transactionalId `{}`, which was not requested",
                    transactionState.transactionalId());
                continue;
            }

            Errors error = Errors.forCode(transactionState.errorCode());
            if (error != Errors.NONE) {
                handleError(transactionalIdKey, error, failed, unmapped);
                continue;
            }

            OptionalLong transactionStartTimeMs = transactionState.transactionStartTimeMs() < 0 ?
                OptionalLong.empty() :
                OptionalLong.of(transactionState.transactionStartTimeMs());

            completed.put(transactionalIdKey, new TransactionDescription(
                broker.id(),
                TransactionState.parse(transactionState.transactionState()),
                transactionState.producerId(),
                transactionState.producerEpoch(),
                transactionState.transactionTimeoutMs(),
                transactionStartTimeMs,
                collectTopicPartitions(transactionState)
            ));
        }

        return new ApiResult<>(completed, failed, unmapped);
    }

    private Set<TopicPartition> collectTopicPartitions(
        DescribeTransactionsResponseData.TransactionState transactionState
    ) {
        Set<TopicPartition> res = new HashSet<>();
        for (DescribeTransactionsResponseData.TopicData topicData : transactionState.topics()) {
            String topic = topicData.topic();
            for (Integer partitionId : topicData.partitions()) {
                res.add(new TopicPartition(topic, partitionId));
            }
        }
        return res;
    }

    private void handleError(
        CoordinatorKey transactionalIdKey,
        Errors error,
        Map<CoordinatorKey, Throwable> failed,
        List<CoordinatorKey> unmapped
    ) {
        switch (error) {
            default:
                failed.put(transactionalIdKey, error.exception());
        }
    }

}