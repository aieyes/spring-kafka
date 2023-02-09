package org.apache.kafka.clients.admin;

import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class DescribeTopicsOptions extends AbstractOptions<DescribeTopicsOptions> {
    private boolean includeAuthorizedOperations;

    public DescribeTopicsOptions timeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public DescribeTopicsOptions includeAuthorizedOperations(boolean includeAuthorizedOperations) {
        this.includeAuthorizedOperations = includeAuthorizedOperations;
        return this;
    }

    public boolean includeAuthorizedOperations() {
        return this.includeAuthorizedOperations;
    }
}
