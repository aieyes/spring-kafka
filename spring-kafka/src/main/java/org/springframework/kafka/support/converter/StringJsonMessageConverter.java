//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.kafka.support.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.reflect.Type;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

@SuppressWarnings("deprecation")
public class StringJsonMessageConverter extends MessagingMessageConverter {
	private final ObjectMapper objectMapper;

	public StringJsonMessageConverter() {
		this(new ObjectMapper());
		this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public StringJsonMessageConverter(ObjectMapper objectMapper) {
		Assert.notNull(objectMapper, "'objectMapper' must not be null.");
		this.objectMapper = objectMapper;
	}

	protected Object convertPayload(Message<?> message) {
		try {
			return this.objectMapper.writeValueAsString(message.getPayload());
		} catch (JsonProcessingException var3) {
			throw new ConversionException("Failed to convert to JSON", var3);
		}
	}

	protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
		Object value = record.value();
		if (record.value() == null) {
			return KafkaNull.INSTANCE;
		} else {
			JavaType javaType = TypeFactory.defaultInstance().constructType(type);
			if (value instanceof String) {
				try {
					return this.objectMapper.readValue((String)value, javaType);
				} catch (IOException var6) {
					throw new ConversionException("Failed to convert from JSON", var6);
				}
			} else if (value instanceof byte[]) {
				try {
					return this.objectMapper.readValue((byte[]) value, javaType);
				} catch (IOException var7) {
					throw new ConversionException("Failed to convert from JSON", var7);
				}
			} else {
				throw new IllegalStateException("Only String or byte[] supported");
			}
		}
	}
}
