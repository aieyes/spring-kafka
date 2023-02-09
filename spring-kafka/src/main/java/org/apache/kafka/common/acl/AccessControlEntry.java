package org.apache.kafka.common.acl;

import java.util.Objects;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class AccessControlEntry {
    final AccessControlEntryData data;

    public AccessControlEntry(String principal, String host, AclOperation operation, AclPermissionType permissionType) {
        Objects.requireNonNull(principal);
        Objects.requireNonNull(host);
        Objects.requireNonNull(operation);
        if (operation == AclOperation.ANY)
            throw new IllegalArgumentException("operation must not be ANY");
        Objects.requireNonNull(permissionType);
        if (permissionType == AclPermissionType.ANY)
            throw new IllegalArgumentException("permissionType must not be ANY");
        this.data = new AccessControlEntryData(principal, host, operation, permissionType);
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

    public AccessControlEntryFilter toFilter() {
        return new AccessControlEntryFilter(this.data);
    }

    public String toString() {
        return this.data.toString();
    }

    public boolean isUnknown() {
        return this.data.isUnknown();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AccessControlEntry))
            return false;
        AccessControlEntry other = (AccessControlEntry)o;
        return this.data.equals(other.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}
