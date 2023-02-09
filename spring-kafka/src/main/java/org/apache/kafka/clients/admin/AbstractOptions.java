package org.apache.kafka.clients.admin;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractOptions<T extends AbstractOptions> {
    protected Integer timeoutMs = null;

    public T timeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
        return (T) this;
    }

    public Integer timeoutMs() {
        return this.timeoutMs;
    }
}
