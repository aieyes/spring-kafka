package org.apache.kafka.common.acl;

import java.util.HashMap;
import java.util.Locale;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public enum AclOperation {
    UNKNOWN((byte)0),
    ANY((byte)1),
    ALL((byte)2),
    READ((byte)3),
    WRITE((byte)4),
    CREATE((byte)5),
    DELETE((byte)6),
    ALTER((byte)7),
    DESCRIBE((byte)8),
    CLUSTER_ACTION((byte)9),
    DESCRIBE_CONFIGS((byte)10),
    ALTER_CONFIGS((byte)11),
    IDEMPOTENT_WRITE((byte)12);

    private static final HashMap<Byte, AclOperation> CODE_TO_VALUE;

    private final byte code;

    static {
        CODE_TO_VALUE = new HashMap<>();
        for (AclOperation operation : values())
            CODE_TO_VALUE.put(Byte.valueOf(operation.code), operation);
    }

    public static AclOperation fromString(String str) throws IllegalArgumentException {
        try {
            return valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static AclOperation fromCode(byte code) {
        AclOperation operation = CODE_TO_VALUE.get(Byte.valueOf(code));
        if (operation == null)
            return UNKNOWN;
        return operation;
    }

    AclOperation(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public boolean isUnknown() {
        return (this == UNKNOWN);
    }
}
