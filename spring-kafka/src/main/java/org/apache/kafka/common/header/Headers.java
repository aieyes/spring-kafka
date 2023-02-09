package org.apache.kafka.common.header;

public interface Headers extends Iterable<Header> {
  Headers add(Header paramHeader) throws IllegalStateException;

  Headers add(String paramString, byte[] paramArrayOfbyte) throws IllegalStateException;

  Headers remove(String paramString) throws IllegalStateException;

  Header lastHeader(String paramString);

  Iterable<Header> headers(String paramString);

  Header[] toArray();
}
