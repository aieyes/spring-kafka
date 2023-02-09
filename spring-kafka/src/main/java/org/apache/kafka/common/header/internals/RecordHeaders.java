package org.apache.kafka.common.header.internals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.utils.AbstractIterator;

public class RecordHeaders implements Headers {
  private final List<Header> headers;
  
  private volatile boolean isReadOnly;
  
  public RecordHeaders() {
    this((Iterable<Header>)null);
  }
  
  public RecordHeaders(Header[] headers) {
    this((headers == null) ? null : Arrays.<Header>asList(headers));
  }
  
  public RecordHeaders(Iterable<Header> headers) {
    if (headers == null) {
      this.headers = new ArrayList<>();
    } else if (headers instanceof RecordHeaders) {
      this.headers = new ArrayList<>(((RecordHeaders)headers).headers);
    } else {
      this.headers = new ArrayList<>();
      for (Header header : headers) {
        Objects.requireNonNull(header, "Header cannot be null.");
        this.headers.add(header);
      } 
    } 
  }
  
  public Headers add(Header header) throws IllegalStateException {
    Objects.requireNonNull(header, "Header cannot be null.");
    canWrite();
    this.headers.add(header);
    return this;
  }
  
  public Headers add(String key, byte[] value) throws IllegalStateException {
    return add(new RecordHeader(key, value));
  }
  
  public Headers remove(String key) throws IllegalStateException {
    canWrite();
    checkKey(key);
    Iterator<Header> iterator = iterator();
    while (iterator.hasNext()) {
      if (iterator.next().key().equals(key))
        iterator.remove(); 
    } 
    return this;
  }
  
  public Header lastHeader(String key) {
    checkKey(key);
    for (int i = this.headers.size() - 1; i >= 0; i--) {
      Header header = this.headers.get(i);
      if (header.key().equals(key))
        return header; 
    } 
    return null;
  }
  
  public Iterable<Header> headers(String key) {
    checkKey(key);
    return () -> new FilterByKeyIterator(this.headers.iterator(), key);
  }
  
  public Iterator<Header> iterator() {
    return closeAware(this.headers.iterator());
  }
  
  public void setReadOnly() {
    this.isReadOnly = true;
  }
  
  public Header[] toArray() {
    return this.headers.isEmpty() ? new Header[0] : this.headers.toArray(new Header[0]);
  }
  
  private void checkKey(String key) {
    if (key == null)
      throw new IllegalArgumentException("key cannot be null."); 
  }
  
  private void canWrite() {
    if (this.isReadOnly)
      throw new IllegalStateException("RecordHeaders has been closed."); 
  }
  
  private Iterator<Header> closeAware(final Iterator<Header> original) {
    return new Iterator<Header>() {
        public boolean hasNext() {
          return original.hasNext();
        }
        
        public Header next() {
          return original.next();
        }
        
        public void remove() {
          RecordHeaders.this.canWrite();
          original.remove();
        }
      };
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    RecordHeaders headers1 = (RecordHeaders)o;
    return Objects.equals(this.headers, headers1.headers);
  }
  
  public int hashCode() {
    return (this.headers != null) ? this.headers.hashCode() : 0;
  }
  
  public String toString() {
    return "RecordHeaders(headers = " + this.headers + ", isReadOnly = " + this.isReadOnly + ')';
  }
  
  private static final class FilterByKeyIterator extends AbstractIterator<Header> {
    private final Iterator<Header> original;
    
    private final String key;
    
    private FilterByKeyIterator(Iterator<Header> original, String key) {
      this.original = original;
      this.key = key;
    }
    
    protected Header makeNext() {
      while (this.original.hasNext()) {
        Header header = this.original.next();
        if (!header.key().equals(this.key))
          continue; 
        return header;
      } 
      return allDone();
    }
  }
}
