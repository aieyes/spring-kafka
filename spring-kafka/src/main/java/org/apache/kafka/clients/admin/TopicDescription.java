package org.apache.kafka.clients.admin;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.utils.Utils;

public class TopicDescription {
    private final String name;

    private final boolean internal;

    private final List<TopicPartitionInfo> partitions;

    private Set<AclOperation> authorizedOperations;

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TopicDescription that = (TopicDescription)o;
        return (this.internal == that.internal &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.partitions, that.partitions) &&
                Objects.equals(this.authorizedOperations, that.authorizedOperations));
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.name, Boolean.valueOf(this.internal), this.partitions, this.authorizedOperations });
    }

    public TopicDescription(String name, boolean internal, List<TopicPartitionInfo> partitions) {
        this(name, internal, partitions, Collections.emptySet());
    }

    TopicDescription(String name, boolean internal, List<TopicPartitionInfo> partitions, Set<AclOperation> authorizedOperations) {
        this.name = name;
        this.internal = internal;
        this.partitions = partitions;
        this.authorizedOperations = authorizedOperations;
    }

    public String name() {
        return this.name;
    }

    public boolean isInternal() {
        return this.internal;
    }

    public List<TopicPartitionInfo> partitions() {
        return this.partitions;
    }

    public Set<AclOperation> authorizedOperations() {
        return this.authorizedOperations;
    }

    public String toString() {
        return "(name=" + this.name + ", internal=" + this.internal + ", partitions=" +
                Utils.join(this.partitions, ",") + ", authorizedOperations=" + this.authorizedOperations + ")";
    }
}
