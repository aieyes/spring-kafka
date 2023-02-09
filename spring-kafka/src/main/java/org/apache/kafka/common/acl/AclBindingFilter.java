package org.apache.kafka.common.acl;

import java.util.Objects;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceFilter;
import org.apache.kafka.common.resource.ResourcePatternFilter;

@Evolving
public class AclBindingFilter {
    private final ResourcePatternFilter patternFilter;

    private final AccessControlEntryFilter entryFilter;

    public static final AclBindingFilter ANY = new AclBindingFilter(ResourcePatternFilter.ANY, AccessControlEntryFilter.ANY);

    public AclBindingFilter(ResourcePatternFilter patternFilter, AccessControlEntryFilter entryFilter) {
        this.patternFilter = Objects.<ResourcePatternFilter>requireNonNull(patternFilter, "patternFilter");
        this.entryFilter = Objects.<AccessControlEntryFilter>requireNonNull(entryFilter, "entryFilter");
    }

    @Deprecated
    public AclBindingFilter(ResourceFilter resourceFilter, AccessControlEntryFilter entryFilter) {
        this(new ResourcePatternFilter(resourceFilter.resourceType(), resourceFilter.name(), PatternType.LITERAL), entryFilter);
    }

    public boolean isUnknown() {
        return (this.patternFilter.isUnknown() || this.entryFilter.isUnknown());
    }

    public ResourcePatternFilter patternFilter() {
        return this.patternFilter;
    }

    public final AccessControlEntryFilter entryFilter() {
        return this.entryFilter;
    }

    public String toString() {
        return "(patternFilter=" + this.patternFilter + ", entryFilter=" + this.entryFilter + ")";
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AclBindingFilter that = (AclBindingFilter)o;
        return (Objects.equals(this.patternFilter, that.patternFilter) &&
                Objects.equals(this.entryFilter, that.entryFilter));
    }

    public boolean matchesAtMostOne() {
        return (this.patternFilter.matchesAtMostOne() && this.entryFilter.matchesAtMostOne());
    }

    public String findIndefiniteField() {
        String indefinite = this.patternFilter.findIndefiniteField();
        if (indefinite != null)
            return indefinite;
        return this.entryFilter.findIndefiniteField();
    }

    public boolean matches(AclBinding binding) {
        return (this.patternFilter.matches(binding.pattern()) && this.entryFilter.matches(binding.entry()));
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.patternFilter, this.entryFilter });
    }
}
