package org.apache.kafka.clients;

import java.util.Locale;

public enum ClientDnsLookup {
    DEFAULT("default"),
    USE_ALL_DNS_IPS("use_all_dns_ips"),
    RESOLVE_CANONICAL_BOOTSTRAP_SERVERS_ONLY("resolve_canonical_bootstrap_servers_only");

    private String clientDnsLookup;

    ClientDnsLookup(String clientDnsLookup) {
        this.clientDnsLookup = clientDnsLookup;
    }

    public String toString() {
        return this.clientDnsLookup;
    }

    public static ClientDnsLookup forConfig(String config) {
        return valueOf(config.toUpperCase(Locale.ROOT));
    }
}
