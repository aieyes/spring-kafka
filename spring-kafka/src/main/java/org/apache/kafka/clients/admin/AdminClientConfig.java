/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.clients.admin;

import org.apache.kafka.clients.ClientDnsLookup;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.SecurityConfig;
import org.apache.kafka.common.metrics.Sensor;

import java.util.Map;
import java.util.Set;

import static org.apache.kafka.common.config.ConfigDef.Range.atLeast;
import static org.apache.kafka.common.config.ConfigDef.Range.between;
import static org.apache.kafka.common.config.ConfigDef.ValidString.in;

/**
 * The AdminClient configuration class, which also contains constants for configuration entry names.
 */
public class AdminClientConfig extends AbstractConfig {
    private static final ConfigDef CONFIG;

    /**
     * <code>bootstrap.servers</code>
     */
    public static final String BOOTSTRAP_SERVERS_CONFIG = CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
    private static final String BOOTSTRAP_SERVERS_DOC = CommonClientConfigs.BOOTSTRAP_SERVERS_DOC;

    /**
     * <code>client.dns.lookup</code>
     */
    public static final String CLIENT_DNS_LOOKUP_CONFIG = "client.dns.lookup";
    private static final String CLIENT_DNS_LOOKUP_DOC = "Controls how the client uses DNS lookups. "
            + "If set to <code>use_all_dns_ips</code>, connect to each returned IP "
            + "address in sequence until a successful connection is established. "
            + "After a disconnection, the next IP is used. Once all IPs have been "
            + "used once, the client resolves the IP(s) from the hostname again "
            + "(both the JVM and the OS cache DNS name lookups, however). "
            + "If set to <code>resolve_canonical_bootstrap_servers_only</code>, "
            + "resolve each bootstrap address into a list of canonical names. After "
            + "the bootstrap phase, this behaves the same as <code>use_all_dns_ips</code>.";

    /**
     * <code>reconnect.backoff.ms</code>
     */
    public static final String RECONNECT_BACKOFF_MS_CONFIG = CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG;
    private static final String RECONNECT_BACKOFF_MS_DOC = CommonClientConfigs.RECONNECT_BACKOFF_MS_DOC;

    /**
     * <code>reconnect.backoff.max.ms</code>
     */
    public static final String RECONNECT_BACKOFF_MAX_MS_CONFIG = "reconnect.backoff.max.ms";
    private static final String RECONNECT_BACKOFF_MAX_MS_DOC = "The maximum amount of time in milliseconds to wait when " +
            "reconnecting to a broker that has repeatedly failed to connect. If provided, the backoff per host will " +
            "increase exponentially for each consecutive connection failure, up to this maximum. After calculating the " +
            "backoff increase, 20% random jitter is added to avoid connection storms.";

    /**
     * <code>retry.backoff.ms</code>
     */
    public static final String RETRY_BACKOFF_MS_CONFIG = CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG;
    private static final String RETRY_BACKOFF_MS_DOC = "The amount of time to wait before attempting to " +
                "retry a failed request. This avoids repeatedly sending requests in a tight loop under " +
                "some failure scenarios.";

    /** <code>socket.connection.setup.timeout.ms</code> */
    public static final String SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG = "socket.connection.setup.timeout.ms";
    private static final String SOCKET_CONNECTION_SETUP_TIMEOUT_MS_DOC = "The amount of time the client will wait for " +
            "the socket connection to be established. If the connection is not built before the timeout elapses, " +
            "clients will close the socket channel.";
    public static final Long DEFAULT_SOCKET_CONNECTION_SETUP_TIMEOUT_MS = 10 * 1000L;
    private static final String SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_DOC = "The maximum amount of time the client will " +
            "wait for the socket connection to be established. The connection setup timeout will increase exponentially " +
            "for each consecutive connection failure up to this maximum. To avoid connection storms, a randomization " +
            "factor of 0.2 will be applied to the timeout resulting in a random range between 20% below and 20% above " +
            "the computed value.";

    /** <code>socket.connection.setup.timeout.max.ms</code> */
    public static final String SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_CONFIG = "socket.connection.setup.timeout.max.ms";
    public static final Long DEFAULT_SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS = 30 * 1000L;

    /** <code>connections.max.idle.ms</code> */
    public static final String CONNECTIONS_MAX_IDLE_MS_CONFIG = CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_CONFIG;
    private static final String CONNECTIONS_MAX_IDLE_MS_DOC = CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_DOC;

    /** <code>request.timeout.ms</code> */
    public static final String REQUEST_TIMEOUT_MS_CONFIG = CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG;
    private static final String REQUEST_TIMEOUT_MS_DOC = CommonClientConfigs.REQUEST_TIMEOUT_MS_DOC;

    public static final String CLIENT_ID_CONFIG = CommonClientConfigs.CLIENT_ID_CONFIG;
    private static final String CLIENT_ID_DOC = CommonClientConfigs.CLIENT_ID_DOC;

    public static final String METADATA_MAX_AGE_CONFIG = CommonClientConfigs.METADATA_MAX_AGE_CONFIG;
    private static final String METADATA_MAX_AGE_DOC = CommonClientConfigs.METADATA_MAX_AGE_DOC;

    public static final String SEND_BUFFER_CONFIG = CommonClientConfigs.SEND_BUFFER_CONFIG;
    private static final String SEND_BUFFER_DOC = CommonClientConfigs.SEND_BUFFER_DOC;

    public static final String RECEIVE_BUFFER_CONFIG = CommonClientConfigs.RECEIVE_BUFFER_CONFIG;
    private static final String RECEIVE_BUFFER_DOC = CommonClientConfigs.RECEIVE_BUFFER_DOC;

    public static final String METRIC_REPORTER_CLASSES_CONFIG = CommonClientConfigs.METRIC_REPORTER_CLASSES_CONFIG;
    private static final String METRIC_REPORTER_CLASSES_DOC = CommonClientConfigs.METRIC_REPORTER_CLASSES_DOC;

    public static final String METRICS_NUM_SAMPLES_CONFIG = CommonClientConfigs.METRICS_NUM_SAMPLES_CONFIG;
    private static final String METRICS_NUM_SAMPLES_DOC = CommonClientConfigs.METRICS_NUM_SAMPLES_DOC;

    public static final String METRICS_SAMPLE_WINDOW_MS_CONFIG = CommonClientConfigs.METRICS_SAMPLE_WINDOW_MS_CONFIG;
    private static final String METRICS_SAMPLE_WINDOW_MS_DOC = CommonClientConfigs.METRICS_SAMPLE_WINDOW_MS_DOC;

    public static final String METRICS_RECORDING_LEVEL_CONFIG = CommonClientConfigs.METRICS_RECORDING_LEVEL_CONFIG;

    public static final String SECURITY_PROTOCOL_CONFIG = CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
    public static final String DEFAULT_SECURITY_PROTOCOL = CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL;
    private static final String SECURITY_PROTOCOL_DOC = CommonClientConfigs.SECURITY_PROTOCOL_DOC;
    private static final String METRICS_RECORDING_LEVEL_DOC = CommonClientConfigs.METRICS_RECORDING_LEVEL_DOC;

    public static final String RETRIES_CONFIG = "retries";
    private static final String RETRIES_DOC = "Setting a value greater than zero will cause the client to resend any " +
            "request that fails with a potentially transient error. It is recommended to set the value to either zero " +
            "or `MAX_VALUE` and use corresponding timeout parameters to control how long a client should retry a request.";

    public static final String DEFAULT_API_TIMEOUT_MS_CONFIG = "default.api.timeout.ms";

    private static final String DEFAULT_API_TIMEOUT_MS_DOC = "Specifies the timeout (in milliseconds) for client APIs. " +
            "This configuration is used as the default timeout for all client operations that do not specify a " +
            "<code>timeout</code> parameter.";


    /**
     * <code>security.providers</code>
     */
    public static final String SECURITY_PROVIDERS_CONFIG = SecurityConfig.SECURITY_PROVIDERS_CONFIG;
    private static final String SECURITY_PROVIDERS_DOC = SecurityConfig.SECURITY_PROVIDERS_DOC;

    static {
        CONFIG = new ConfigDef().define(BOOTSTRAP_SERVERS_CONFIG,
                                        Type.LIST,
                                        Importance.HIGH,
                                        BOOTSTRAP_SERVERS_DOC)
                                .define(CLIENT_ID_CONFIG, Type.STRING, "", Importance.MEDIUM, CLIENT_ID_DOC)
                                .define(METADATA_MAX_AGE_CONFIG, Type.LONG, 5 * 60 * 1000, atLeast(0), Importance.LOW, METADATA_MAX_AGE_DOC)
                                .define(SEND_BUFFER_CONFIG, Type.INT, 128 * 1024, atLeast(-1), Importance.MEDIUM, SEND_BUFFER_DOC)
                                .define(RECEIVE_BUFFER_CONFIG, Type.INT, 64 * 1024, atLeast(-1), Importance.MEDIUM, RECEIVE_BUFFER_DOC)
                                .define(RECONNECT_BACKOFF_MS_CONFIG,
                                        Type.LONG,
                                        50L,
                                        atLeast(0L),
                                        Importance.LOW,
                                        RECONNECT_BACKOFF_MS_DOC)
                                .define(RECONNECT_BACKOFF_MAX_MS_CONFIG,
                                        Type.LONG,
                                        1000L,
                                        atLeast(0L),
                                        Importance.LOW,
                                        RECONNECT_BACKOFF_MAX_MS_DOC)
                                .define(RETRY_BACKOFF_MS_CONFIG,
                                        Type.LONG,
                                        100L,
                                        atLeast(0L),
                                        Importance.LOW,
                                        RETRY_BACKOFF_MS_DOC)
                                .define(REQUEST_TIMEOUT_MS_CONFIG,
                                        Type.INT,
                                        30000,
                                        atLeast(0),
                                        Importance.MEDIUM,
                                        REQUEST_TIMEOUT_MS_DOC)
                                .define(SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG,
                                        Type.LONG,
                                        DEFAULT_SOCKET_CONNECTION_SETUP_TIMEOUT_MS,
                                        Importance.MEDIUM,
                                        SOCKET_CONNECTION_SETUP_TIMEOUT_MS_DOC)
                                .define(SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_CONFIG,
                                        Type.LONG,
                                        DEFAULT_SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS,
                                        Importance.MEDIUM,
                                        SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS_DOC)
                                .define(CONNECTIONS_MAX_IDLE_MS_CONFIG,
                                        Type.LONG,
                                        5 * 60 * 1000,
                                        Importance.MEDIUM,
                                        CONNECTIONS_MAX_IDLE_MS_DOC)
                                .define(RETRIES_CONFIG,
                                        Type.INT,
                                        Integer.MAX_VALUE,
                                        between(0, Integer.MAX_VALUE),
                                        Importance.LOW,
                                        RETRIES_DOC)
                                .define(DEFAULT_API_TIMEOUT_MS_CONFIG,
                                        Type.INT,
                                        60000,
                                        atLeast(0),
                                        Importance.MEDIUM,
                                        DEFAULT_API_TIMEOUT_MS_DOC)
                                .define(METRICS_SAMPLE_WINDOW_MS_CONFIG,
                                        Type.LONG,
                                        30000,
                                        atLeast(0),
                                        Importance.LOW,
                                        METRICS_SAMPLE_WINDOW_MS_DOC)
                                .define(METRICS_NUM_SAMPLES_CONFIG, Type.INT, 2, atLeast(1), Importance.LOW, METRICS_NUM_SAMPLES_DOC)
                                .define(METRIC_REPORTER_CLASSES_CONFIG, Type.LIST, "", Importance.LOW, METRIC_REPORTER_CLASSES_DOC)
                                .define(METRICS_RECORDING_LEVEL_CONFIG,
                                        Type.STRING,
                                        Sensor.RecordingLevel.INFO.toString(),
                                        in(Sensor.RecordingLevel.INFO.toString(), Sensor.RecordingLevel.DEBUG.toString(),
                                                Sensor.RecordingLevel.INFO.toString()),
                                        Importance.LOW,
                                        METRICS_RECORDING_LEVEL_DOC)
                                .define(CLIENT_DNS_LOOKUP_CONFIG,
                                        Type.STRING,
                                        ClientDnsLookup.USE_ALL_DNS_IPS.toString(),
                                        in(ClientDnsLookup.USE_ALL_DNS_IPS.toString(),
                                           ClientDnsLookup.RESOLVE_CANONICAL_BOOTSTRAP_SERVERS_ONLY.toString()),
                                        Importance.MEDIUM,
                                        CLIENT_DNS_LOOKUP_DOC)
                                // security support
                                .define(SECURITY_PROVIDERS_CONFIG,
                                        Type.STRING,
                                        null,
                                        Importance.LOW,
                                        SECURITY_PROVIDERS_DOC)
                                .define(SECURITY_PROTOCOL_CONFIG,
                                        Type.STRING,
                                        DEFAULT_SECURITY_PROTOCOL,
                                        Importance.MEDIUM,
                                        SECURITY_PROTOCOL_DOC)
                                .withClientSslSupport()
                                .withClientSaslSupport();
    }

    protected Map<String, Object> postProcessParsedConfig(final Map<String, Object> parsedValues) {
        return null;
    }

    public AdminClientConfig(Map<?, ?> props) {
        this(props, false);
    }

    protected AdminClientConfig(Map<?, ?> props, boolean doLog) {
        super(CONFIG, props, doLog);
    }

    public static Set<String> configNames() {
        return CONFIG.names();
    }

    public static ConfigDef configDef() {
        return  new ConfigDef(CONFIG);
    }

    public static void main(String[] args) {
    }

}
