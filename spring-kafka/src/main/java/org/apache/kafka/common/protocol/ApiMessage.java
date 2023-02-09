package org.apache.kafka.common.protocol;

public interface ApiMessage extends Message {
  short apiKey();
}
