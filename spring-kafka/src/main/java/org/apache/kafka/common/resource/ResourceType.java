package org.apache.kafka.common.resource;

import java.util.HashMap;
import java.util.Locale;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public enum ResourceType {
    UNKNOWN((byte)0),
    ANY((byte)1),
    TOPIC((byte)2),
    GROUP((byte)3),
    CLUSTER((byte)4),
    TRANSACTIONAL_ID((byte)5),
    DELEGATION_TOKEN((byte)6);

    private static final HashMap<Byte, ResourceType> CODE_TO_VALUE;

    private final byte code;

    static {
        CODE_TO_VALUE = new HashMap<>();
        for (ResourceType resourceType : values())
            CODE_TO_VALUE.put(Byte.valueOf(resourceType.code), resourceType);
    }

    public static ResourceType fromString(String str) throws IllegalArgumentException {
        try {
            return valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static ResourceType fromCode(byte code) {
        ResourceType resourceType = CODE_TO_VALUE.get(Byte.valueOf(code));
        if (resourceType == null)
            return UNKNOWN;
        return resourceType;
    }

    ResourceType(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public boolean isUnknown() {
        return (this == UNKNOWN);
    }
}
