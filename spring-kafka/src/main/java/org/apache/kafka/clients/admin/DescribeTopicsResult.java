package org.apache.kafka.clients.admin;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class DescribeTopicsResult {
    private final Map<String, KafkaFuture<TopicDescription>> futures;

    DescribeTopicsResult(Map<String, KafkaFuture<TopicDescription>> futures) {
        this.futures = futures;
    }

    public Map<String, KafkaFuture<TopicDescription>> values() {
        return this.futures;
    }

    @SuppressWarnings({"rawtypes"})
    public KafkaFuture<Map<String, TopicDescription>> all() {
        return KafkaFuture.allOf((KafkaFuture[])this.futures.values().toArray((Object[])new KafkaFuture[0]))
                .thenApply(v -> {
                    Map<String, TopicDescription> descriptions = new HashMap<>(DescribeTopicsResult.this.futures.size());
                    for (Map.Entry<String, KafkaFuture<TopicDescription>> entry : DescribeTopicsResult.this.futures.entrySet()) {
                        try {
                            descriptions.put(entry.getKey(), (TopicDescription) ((KafkaFuture)entry.getValue()).get());
                        } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return descriptions;
                });
    }
}
