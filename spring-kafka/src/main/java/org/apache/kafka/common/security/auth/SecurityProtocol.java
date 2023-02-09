package org.apache.kafka.common.security.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum SecurityProtocol {
    PLAINTEXT(0, "PLAINTEXT"),
    SSL(1, "SSL"),
    SASL_PLAINTEXT(2, "SASL_PLAINTEXT"),
    SASL_SSL(3, "SASL_SSL");

    private static final Map<Short, SecurityProtocol> CODE_TO_SECURITY_PROTOCOL;

    private static final List<String> NAMES;

    public final short id;

    public final String name;

    static {
        SecurityProtocol[] protocols = values();
        List<String> names = new ArrayList<>(protocols.length);
        Map<Short, SecurityProtocol> codeToSecurityProtocol = new HashMap<>(protocols.length);
        for (SecurityProtocol proto : protocols) {
            codeToSecurityProtocol.put(Short.valueOf(proto.id), proto);
            names.add(proto.name);
        }
        CODE_TO_SECURITY_PROTOCOL = Collections.unmodifiableMap(codeToSecurityProtocol);
        NAMES = Collections.unmodifiableList(names);
    }

    SecurityProtocol(int id, String name) {
        this.id = (short)id;
        this.name = name;
    }

    public static List<String> names() {
        return NAMES;
    }

    public static SecurityProtocol forId(short id) {
        return CODE_TO_SECURITY_PROTOCOL.get(Short.valueOf(id));
    }

    public static SecurityProtocol forName(String name) {
        return valueOf(name.toUpperCase(Locale.ROOT));
    }
}
