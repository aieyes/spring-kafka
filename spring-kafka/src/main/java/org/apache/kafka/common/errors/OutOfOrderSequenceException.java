package org.apache.kafka.common.errors;

public class OutOfOrderSequenceException extends ApiException {
    private static final long serialVersionUID = -577551655774171486L;

    public OutOfOrderSequenceException(String msg) {
        super(msg);
    }
}
