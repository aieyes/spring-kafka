//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.kafka.support.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.core.log.LogAccessor;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.JacksonPresent;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SuppressWarnings({"rawtypes"})
public class BatchMessagingMessageConverter implements BatchMessageConverter {
	protected final LogAccessor logger;
	private final RecordMessageConverter recordConverter;
	private boolean generateMessageId;
	private boolean generateTimestamp;
	private KafkaHeaderMapper headerMapper;

	public BatchMessagingMessageConverter() {
		this((RecordMessageConverter)null);
	}

	public BatchMessagingMessageConverter(RecordMessageConverter recordConverter) {
		this.logger = new LogAccessor(LogFactory.getLog(this.getClass()));
		this.generateMessageId = false;
		this.generateTimestamp = false;
		this.recordConverter = recordConverter;
		if (JacksonPresent.isJackson2Present()) {
			this.headerMapper = new DefaultKafkaHeaderMapper();
		}

	}

	public void setGenerateMessageId(boolean generateMessageId) {
		this.generateMessageId = generateMessageId;
	}

	public void setGenerateTimestamp(boolean generateTimestamp) {
		this.generateTimestamp = generateTimestamp;
	}

	public void setHeaderMapper(KafkaHeaderMapper headerMapper) {
		this.headerMapper = headerMapper;
	}

	public RecordMessageConverter getRecordMessageConverter() {
		return this.recordConverter;
	}

	@Override
	public Message<?> toMessage(List<ConsumerRecord<?, ?>> records, Acknowledgment acknowledgment, Type type) {
		KafkaMessageHeaders kafkaMessageHeaders = new KafkaMessageHeaders(this.generateMessageId, this.generateTimestamp);
		Map<String, Object> rawHeaders = kafkaMessageHeaders.getRawHeaders();
		List<Object> payloads = new ArrayList<>();
		List<Object> keys = new ArrayList<>();
		List<String> topics = new ArrayList<>();
		List<Integer> partitions = new ArrayList<>();
		List<Long> offsets = new ArrayList<>();
		List<String> timestampTypes = new ArrayList<>();
		List<Long> timestamps = new ArrayList<>();
		List<Map<String, Object>> convertedHeaders = new ArrayList<>();
		List<Headers> natives = new ArrayList<>();
		if (this.headerMapper != null) {
			rawHeaders.put("kafka_batchConvertedHeaders", convertedHeaders);
		} else {
			rawHeaders.put("kafka_nativeHeaders", natives);
		}

		this.commonHeaders(acknowledgment, null, rawHeaders, keys, topics, partitions, offsets, timestampTypes, timestamps);
		boolean logged = false;

		ConsumerRecord record;
		for(Iterator var16 = records.iterator(); var16.hasNext(); timestamps.add(record.timestamp())) {
			record = (ConsumerRecord)var16.next();
			payloads.add(this.recordConverter != null && this.containerType(type) ? this.convert(record, type) : this.extractAndConvertValue(record, type));
			keys.add(record.key());
			topics.add(record.topic());
			partitions.add(record.partition());
			offsets.add(record.offset());
			if (record.timestampType() != null) {
				timestampTypes.add(record.timestampType().name());
			}
		}

		return MessageBuilder.createMessage(payloads, kafkaMessageHeaders);
	}

	@Override
	public Message<?> toMessage(List<ConsumerRecord<?, ?>> records, Acknowledgment acknowledgment, Consumer<?, ?> consumer, Type payloadType) {
		return toMessage(records, acknowledgment, payloadType);
	}

	public List<ProducerRecord<?, ?>> fromMessage(Message<?> message, String defaultTopic) {
		throw new UnsupportedOperationException();
	}

	protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
		return record.value() == null ? KafkaNull.INSTANCE : record.value();
	}

	protected Object convert(ConsumerRecord<?, ?> record, Type type) {
		return this.recordConverter.toMessage(record, (Acknowledgment)null, ((ParameterizedType)type).getActualTypeArguments()[0]).getPayload();
	}

	private boolean containerType(Type type) {
		return type instanceof ParameterizedType && ((ParameterizedType)type).getActualTypeArguments().length == 1;
	}
}
