package org.apache.kafka.common.resource;

import java.util.Objects;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class ResourceFilter {
    private final ResourceType resourceType;

    private final String name;

    public static final ResourceFilter ANY = new ResourceFilter(ResourceType.ANY, null);

    public ResourceFilter(ResourceType resourceType, String name) {
        Objects.requireNonNull(resourceType);
        this.resourceType = resourceType;
        this.name = name;
    }

    public ResourceType resourceType() {
        return this.resourceType;
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return "(resourceType=" + this.resourceType + ", name=" + ((this.name == null) ? "<any>" : this.name) + ")";
    }

    public boolean isUnknown() {
        return this.resourceType.isUnknown();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ResourceFilter))
            return false;
        ResourceFilter other = (ResourceFilter)o;
        return (this.resourceType.equals(other.resourceType) && Objects.equals(this.name, other.name));
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.resourceType, this.name });
    }

    public boolean matches(Resource other) {
        if (this.name != null && !this.name.equals(other.name()))
            return false;
        return (this.resourceType == ResourceType.ANY || this.resourceType.equals(other.resourceType()));
    }

    public boolean matchesAtMostOne() {
        return (findIndefiniteField() == null);
    }

    public String findIndefiniteField() {
        if (this.resourceType == ResourceType.ANY)
            return "Resource type is ANY.";
        if (this.resourceType == ResourceType.UNKNOWN)
            return "Resource type is UNKNOWN.";
        if (this.name == null)
            return "Resource name is NULL.";
        return null;
    }
}
