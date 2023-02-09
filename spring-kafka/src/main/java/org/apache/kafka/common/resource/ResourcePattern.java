package org.apache.kafka.common.resource;

import java.util.Objects;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class ResourcePattern {
    public static final String WILDCARD_RESOURCE = "*";

    private final ResourceType resourceType;

    private final String name;

    private final PatternType patternType;

    public ResourcePattern(ResourceType resourceType, String name, PatternType patternType) {
        this.resourceType = Objects.<ResourceType>requireNonNull(resourceType, "resourceType");
        this.name = Objects.<String>requireNonNull(name, "name");
        this.patternType = Objects.<PatternType>requireNonNull(patternType, "patternType");
        if (resourceType == ResourceType.ANY)
            throw new IllegalArgumentException("resourceType must not be ANY");
        if (patternType == PatternType.MATCH || patternType == PatternType.ANY)
            throw new IllegalArgumentException("patternType must not be " + patternType);
    }

    public ResourceType resourceType() {
        return this.resourceType;
    }

    public String name() {
        return this.name;
    }

    public PatternType patternType() {
        return this.patternType;
    }

    public ResourcePatternFilter toFilter() {
        return new ResourcePatternFilter(this.resourceType, this.name, this.patternType);
    }

    public String toString() {
        return "ResourcePattern(resourceType=" + this.resourceType + ", name=" + ((this.name == null) ? "<any>" : this.name) + ", patternType=" + this.patternType + ")";
    }

    public boolean isUnknown() {
        return (this.resourceType.isUnknown() || this.patternType.isUnknown());
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ResourcePattern resource = (ResourcePattern)o;
        return (this.resourceType == resource.resourceType &&
                Objects.equals(this.name, resource.name) && this.patternType == resource.patternType);
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.resourceType, this.name, this.patternType });
    }
}
