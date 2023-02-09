package org.apache.kafka.common.header;

public interface Header {
  String key();

  byte[] value();
}
