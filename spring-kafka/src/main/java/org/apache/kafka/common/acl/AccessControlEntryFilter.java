package org.apache.kafka.common.acl;

import java.util.Objects;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class AccessControlEntryFilter {
    private final AccessControlEntryData data;

    public static final AccessControlEntryFilter ANY = new AccessControlEntryFilter(null, null, AclOperation.ANY, AclPermissionType.ANY);

    public AccessControlEntryFilter(String principal, String host, AclOperation operation, AclPermissionType permissionType) {
        Objects.requireNonNull(operation);
        Objects.requireNonNull(permissionType);
        this.data = new AccessControlEntryData(principal, host, operation, permissionType);
    }

    AccessControlEntryFilter(AccessControlEntryData data) {
        this.data = data;
    }

    public String principal() {
        return this.data.principal();
    }

    public String host() {
        return this.data.host();
    }

    public AclOperation operation() {
        return this.data.operation();
    }

    public AclPermissionType permissionType() {
        return this.data.permissionType();
    }

    public String toString() {
        return this.data.toString();
    }

    public boolean isUnknown() {
        return this.data.isUnknown();
    }

    public boolean matches(AccessControlEntry other) {
        if (principal() != null && !this.data.principal().equals(other.principal()))
            return false;
        if (host() != null && !host().equals(other.host()))
            return false;
        if (operation() != AclOperation.ANY && !operation().equals(other.operation()))
            return false;
        return (permissionType() == AclPermissionType.ANY || permissionType().equals(other.permissionType()));
    }

    public boolean matchesAtMostOne() {
        return (findIndefiniteField() == null);
    }

    public String findIndefiniteField() {
        return this.data.findIndefiniteField();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AccessControlEntryFilter))
            return false;
        AccessControlEntryFilter other = (AccessControlEntryFilter)o;
        return this.data.equals(other.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}
