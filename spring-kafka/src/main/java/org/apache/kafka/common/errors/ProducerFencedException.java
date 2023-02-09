package org.apache.kafka.common.errors;

public class ProducerFencedException extends ApiException {
    private static final long serialVersionUID = -4496919719793201592L;

    public ProducerFencedException(String msg) {
        super(msg);
    }
}
