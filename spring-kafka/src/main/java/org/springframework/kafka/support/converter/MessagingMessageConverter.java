//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.kafka.support.converter;

import java.lang.reflect.Type;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MessagingMessageConverter implements RecordMessageConverter {
	private boolean generateMessageId = false;
	private boolean generateTimestamp = false;

	public MessagingMessageConverter() {
	}

	public void setGenerateMessageId(boolean generateMessageId) {
		this.generateMessageId = generateMessageId;
	}

	public void setGenerateTimestamp(boolean generateTimestamp) {
		this.generateTimestamp = generateTimestamp;
	}

	@Override
	public Message<?> toMessage(ConsumerRecord<?, ?> record, Acknowledgment acknowledgment, Type type) {
		KafkaMessageHeaders kafkaMessageHeaders = new KafkaMessageHeaders(this.generateMessageId, this.generateTimestamp);
		Map<String, Object> rawHeaders = kafkaMessageHeaders.getRawHeaders();
		rawHeaders.put("kafka_receivedMessageKey", record.key());
		rawHeaders.put("kafka_receivedTopic", record.topic());
		rawHeaders.put("kafka_receivedPartitionId", record.partition());
		rawHeaders.put("kafka_offset", record.offset());
		if (acknowledgment != null) {
			rawHeaders.put("kafka_acknowledgment", acknowledgment);
		}

		return MessageBuilder.createMessage(this.extractAndConvertValue(record, type), kafkaMessageHeaders);
	}

	@Override
	public Message<?> toMessage(ConsumerRecord<?, ?> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer, Type payloadType) {
		return toMessage(record, acknowledgment, payloadType);
	}

	public ProducerRecord<?, ?> fromMessage(Message<?> message, String defaultTopic) {
		MessageHeaders headers = message.getHeaders();
		String topic = headers.get("kafka_topic", String.class);
		Integer partition = headers.get("kafka_partitionId", Integer.class);
		Object key = headers.get("kafka_messageKey");
		Object payload = this.convertPayload(message);
		return new ProducerRecord(topic == null ? defaultTopic : topic, partition, key, payload);
	}

	protected Object convertPayload(Message<?> message) {
		Object payload = message.getPayload();
		return payload instanceof KafkaNull ? null : payload;
	}

	protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
		return record.value() == null ? KafkaNull.INSTANCE : record.value();
	}

	public void setMessagingConverter(MessageConverter messageConverter) {
	}
}
