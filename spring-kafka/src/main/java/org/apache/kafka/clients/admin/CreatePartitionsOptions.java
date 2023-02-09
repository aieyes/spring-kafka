package org.apache.kafka.clients.admin;

import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class CreatePartitionsOptions extends AbstractOptions<CreatePartitionsOptions> {
    private boolean validateOnly = false;

    public boolean validateOnly() {
        return this.validateOnly;
    }

    public CreatePartitionsOptions validateOnly(boolean validateOnly) {
        this.validateOnly = validateOnly;
        return this;
    }
}
