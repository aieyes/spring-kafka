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

package org.apache.kafka.clients.admin;

import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.annotation.InterfaceStability;
import org.apache.kafka.common.quota.ClientQuotaEntity;

import java.util.Map;

/**
 * The API of this class is evolving, see {@link Admin} for details.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })

@InterfaceStability.Evolving
public class AlterClientQuotasResult {

    private final Map<ClientQuotaEntity, KafkaFuture<Void>> futures;

    /**
     * Maps an entity to its alteration result.
     *
     * @param futures maps entity to its alteration result
     */
    public AlterClientQuotasResult(Map<ClientQuotaEntity, KafkaFuture<Void>> futures) {
        this.futures = futures;
    }

    /**
     * Returns a map from quota entity to a future which can be used to check the status of the operation.
     */
    public Map<ClientQuotaEntity, KafkaFuture<Void>> values() {
        return futures;
    }

    /**
     * Returns a future which succeeds only if all quota alterations succeed.
     */
    public KafkaFuture<Void> all() {
        return KafkaFuture.allOf(futures.values().toArray(new KafkaFuture[0]));
    }
}