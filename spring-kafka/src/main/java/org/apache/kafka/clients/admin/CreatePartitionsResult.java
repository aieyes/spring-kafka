package org.apache.kafka.clients.admin;

import java.util.Map;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class CreatePartitionsResult {
    private final Map<String, KafkaFuture<Void>> values;

    CreatePartitionsResult(Map<String, KafkaFuture<Void>> values) {
        this.values = values;
    }

    public Map<String, KafkaFuture<Void>> values() {
        return this.values;
    }

    public KafkaFuture<Void> all() {
        return KafkaFuture.allOf((KafkaFuture<?>[])this.values.values().toArray((Object[]) new KafkaFuture<?>[0]));
    }
}
