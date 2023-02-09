package org.apache.kafka.common;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.kafka.common.utils.Utils;

public class TopicPartitionInfo {
    private final int partition;

    private final Node leader;

    private final List<Node> replicas;

    private final List<Node> isr;

    public TopicPartitionInfo(int partition, Node leader, List<Node> replicas, List<Node> isr) {
        this.partition = partition;
        this.leader = leader;
        this.replicas = Collections.unmodifiableList(replicas);
        this.isr = Collections.unmodifiableList(isr);
    }

    public int partition() {
        return this.partition;
    }

    public Node leader() {
        return this.leader;
    }

    public List<Node> replicas() {
        return this.replicas;
    }

    public List<Node> isr() {
        return this.isr;
    }

    public String toString() {
        return "(partition=" + this.partition + ", leader=" + this.leader + ", replicas=" +
                Utils.join(this.replicas, ", ") + ", isr=" + Utils.join(this.isr, ", ") + ")";
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TopicPartitionInfo that = (TopicPartitionInfo)o;
        return (this.partition == that.partition &&
                Objects.equals(this.leader, that.leader) &&
                Objects.equals(this.replicas, that.replicas) &&
                Objects.equals(this.isr, that.isr));
    }

    public int hashCode() {
        int result = this.partition;
        result = 31 * result + ((this.leader != null) ? this.leader.hashCode() : 0);
        result = 31 * result + ((this.replicas != null) ? this.replicas.hashCode() : 0);
        result = 31 * result + ((this.isr != null) ? this.isr.hashCode() : 0);
        return result;
    }
}
