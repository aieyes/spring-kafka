package org.apache.kafka.common.acl;

import java.util.HashMap;
import java.util.Locale;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public enum AclPermissionType {
    UNKNOWN((byte)0),
    ANY((byte)1),
    DENY((byte)2),
    ALLOW((byte)3);

    private static final HashMap<Byte, AclPermissionType> CODE_TO_VALUE;

    private final byte code;

    static {
        CODE_TO_VALUE = new HashMap<>();
        for (AclPermissionType permissionType : values())
            CODE_TO_VALUE.put(Byte.valueOf(permissionType.code), permissionType);
    }

    public static AclPermissionType fromString(String str) {
        try {
            return valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static AclPermissionType fromCode(byte code) {
        AclPermissionType permissionType = CODE_TO_VALUE.get(Byte.valueOf(code));
        if (permissionType == null)
            return UNKNOWN;
        return permissionType;
    }

    AclPermissionType(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public boolean isUnknown() {
        return (this == UNKNOWN);
    }
}
