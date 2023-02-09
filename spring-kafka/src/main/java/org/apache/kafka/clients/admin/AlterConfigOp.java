package org.apache.kafka.clients.admin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class AlterConfigOp {
  private final ConfigEntry configEntry;
  
  private final OpType opType;
  
  public enum OpType {
    SET((byte)0),
    DELETE((byte)1),
    APPEND((byte)2),
    SUBTRACT((byte)3);
    
    private static final Map<Byte, OpType> OP_TYPES = Collections.unmodifiableMap(
        (Map<? extends Byte, ? extends OpType>)Arrays.<OpType>stream(values()).collect(Collectors.toMap(OpType::id, Function.identity())));
    
    private final byte id;
    
    static {
    
    }
    
    OpType(byte id) {
      this.id = id;
    }
    
    public byte id() {
      return this.id;
    }
    
    public static OpType forId(byte id) {
      return OP_TYPES.get(Byte.valueOf(id));
    }
  }
  
  public AlterConfigOp(ConfigEntry configEntry, OpType operationType) {
    this.configEntry = configEntry;
    this.opType = operationType;
  }
  
  public ConfigEntry configEntry() {
    return this.configEntry;
  }
  
  public OpType opType() {
    return this.opType;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    AlterConfigOp that = (AlterConfigOp)o;
    return (this.opType == that.opType && 
      Objects.equals(this.configEntry, that.configEntry));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.opType, this.configEntry });
  }
  
  public String toString() {
    return "AlterConfigOp{opType=" + this.opType + ", configEntry=" + this.configEntry + '}';
  }
}
