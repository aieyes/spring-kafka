package org.apache.kafka.common.resource;

import java.util.Objects;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class Resource {
    private final ResourceType resourceType;

    private final String name;

    public static final String CLUSTER_NAME = "kafka-cluster";

    public static final Resource CLUSTER = new Resource(ResourceType.CLUSTER, "kafka-cluster");

    public Resource(ResourceType resourceType, String name) {
        Objects.requireNonNull(resourceType);
        this.resourceType = resourceType;
        Objects.requireNonNull(name);
        this.name = name;
    }

    public ResourceType resourceType() {
        return this.resourceType;
    }

    public String name() {
        return this.name;
    }

    public ResourceFilter toFilter() {
        return new ResourceFilter(this.resourceType, this.name);
    }

    public String toString() {
        return "(resourceType=" + this.resourceType + ", name=" + ((this.name == null) ? "<any>" : this.name) + ")";
    }

    public boolean isUnknown() {
        return this.resourceType.isUnknown();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Resource))
            return false;
        Resource other = (Resource)o;
        return (this.resourceType.equals(other.resourceType) && Objects.equals(this.name, other.name));
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.resourceType, this.name });
    }
}
