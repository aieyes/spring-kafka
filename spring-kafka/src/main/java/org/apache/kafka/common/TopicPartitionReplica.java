package org.apache.kafka.common;

import java.io.Serializable;
import org.apache.kafka.common.utils.Utils;

public final class TopicPartitionReplica implements Serializable {
    private static final long serialVersionUID = -5155213080730928688L;
    private int hash = 0;

    private final int brokerId;

    private final int partition;

    private final String topic;

    public TopicPartitionReplica(String topic, int partition, int brokerId) {
        this.topic = Utils.notNull(topic);
        this.partition = partition;
        this.brokerId = brokerId;
    }

    public String topic() {
        return this.topic;
    }

    public int partition() {
        return this.partition;
    }

    public int brokerId() {
        return this.brokerId;
    }

    public int hashCode() {
        if (this.hash != 0)
            return this.hash;
        int prime = 31;
        int result = 1;
        result = 31 * result + this.topic.hashCode();
        result = 31 * result + this.partition;
        result = 31 * result + this.brokerId;
        this.hash = result;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopicPartitionReplica other = (TopicPartitionReplica)obj;
        return (this.partition == other.partition && this.brokerId == other.brokerId && this.topic.equals(other.topic));
    }

    public String toString() {
        return String.format("%s-%d-%d", this.topic, this.partition, this.brokerId);
    }
}
