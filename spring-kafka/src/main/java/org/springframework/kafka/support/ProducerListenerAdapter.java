//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.kafka.support;

import org.apache.kafka.clients.producer.RecordMetadata;

public abstract class ProducerListenerAdapter<K, V> implements ProducerListener<K, V> {
    public ProducerListenerAdapter() {
    }

    public void onSuccess(String topic, Integer partition, K key, V value, RecordMetadata recordMetadata) {
    }

    public void onError(String topic, Integer partition, K key, V value, Exception exception) {
    }

    public boolean isInterestedInSuccess() {
        return false;
    }
}
