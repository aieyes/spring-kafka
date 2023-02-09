//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.kafka.core;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.core.log.LogAccessor;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

@SuppressWarnings({"rawtypes", "unchecked"})
public class KafkaTemplate<K, V> implements KafkaOperations<K, V> {
	protected final LogAccessor log = new LogAccessor(LogFactory.getLog(this.getClass())); //NOSONAR

	protected final Log logger;
	private final ProducerFactory<K, V> producerFactory;
	private final boolean autoFlush;
	private RecordMessageConverter messageConverter;
	private volatile String defaultTopic;
	private volatile ProducerListener<K, V> producerListener;

	public KafkaTemplate(ProducerFactory<K, V> producerFactory) {
		this(producerFactory, false);
	}

	public KafkaTemplate(ProducerFactory<K, V> producerFactory, boolean autoFlush) {
		this.logger = LogFactory.getLog(this.getClass());
		this.messageConverter = new MessagingMessageConverter();
		this.producerListener = new LoggingProducerListener();
		this.producerFactory = producerFactory;
		this.autoFlush = autoFlush;
	}

	public String getDefaultTopic() {
		return this.defaultTopic;
	}

	public void setDefaultTopic(String defaultTopic) {
		this.defaultTopic = defaultTopic;
	}

	public void setProducerListener(ProducerListener<K, V> producerListener) {
		this.producerListener = producerListener;
	}

	public RecordMessageConverter getMessageConverter() {
		return this.messageConverter;
	}

	public void setMessageConverter(RecordMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public ListenableFuture<SendResult<K, V>> sendDefault(V data) {
		return this.send(this.defaultTopic, data);
	}

	public ListenableFuture<SendResult<K, V>> sendDefault(K key, V data) {
		return this.send(this.defaultTopic, key, data);
	}

	@Override
	public ListenableFuture<SendResult<K, V>> sendDefault(Integer partition, K key, V data) {
		return send(this.defaultTopic, partition, key, data);
	}

	@Override
	public ListenableFuture<SendResult<K, V>> sendDefault(Integer partition, Long timestamp, K key, V data) {
		return send(this.defaultTopic, partition, key, data);
	}

	public ListenableFuture<SendResult<K, V>> sendDefault(int partition, K key, V data) {
		return this.send(this.defaultTopic, partition, key, data);
	}

	public ListenableFuture<SendResult<K, V>> send(String topic, V data) {
		ProducerRecord<K, V> producerRecord = new ProducerRecord(topic, data);
		return this.doSend(producerRecord);
	}

	public ListenableFuture<SendResult<K, V>> send(String topic, K key, V data) {
		ProducerRecord<K, V> producerRecord = new ProducerRecord(topic, key, data);
		return this.doSend(producerRecord);
	}

	@Override
	public ListenableFuture<SendResult<K, V>> send(String topic, Integer partition, K key, V data) {
		ProducerRecord<K, V> producerRecord = new ProducerRecord(topic, partition, key, data);
		return this.doSend(producerRecord);
	}

	@Override
	public ListenableFuture<SendResult<K, V>> send(String topic, Integer partition, Long timestamp, K key, V data) {
		return send(topic, partition, data);
	}

	public ListenableFuture<SendResult<K, V>> send(String topic, int partition, V data) {
		ProducerRecord<K, V> producerRecord = new ProducerRecord(topic, partition, null, data);
		return this.doSend(producerRecord);
	}

	public ListenableFuture<SendResult<K, V>> send(ProducerRecord<K, V> record) {
		return this.doSend(record);
	}

	public ListenableFuture<SendResult<K, V>> send(Message<?> message) {
		ProducerRecord producerRecord = this.messageConverter.fromMessage(message, this.defaultTopic);
		return this.doSend(producerRecord);
	}

	public List<PartitionInfo> partitionsFor(String topic) {
		Producer<K, V> producer = this.getTheProducer();

		List var3;
		try {
			var3 = producer.partitionsFor(topic);
		} finally {
			producer.close();
		}

		return var3;
	}

	public Map<MetricName, ? extends Metric> metrics() {
		Producer<K, V> producer = this.getTheProducer();

		Map var2;
		try {
			var2 = producer.metrics();
		} finally {
			producer.close();
		}

		return var2;
	}

	public <T> T execute(KafkaOperations.ProducerCallback<K, V, T> callback) {
		Producer<K, V> producer = this.getTheProducer();

		Object var3;
		try {
			var3 = callback.doInKafka(producer);
		} finally {
			producer.close();
		}

		return (T) var3;
	}

	@Override
	public <T> T executeInTransaction(OperationsCallback<K, V, T> callback) {
		return null;
	}

	public void flush() {
		Producer<K, V> producer = this.getTheProducer();

		try {
			producer.flush();
		} finally {
			producer.close();
		}

	}

	@Deprecated
	@Override
	public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets) {
	}

	@Deprecated
	@Override
	public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId) {
	}

	@Override
	public boolean isTransactional() {
		return false;
	}

	@Override
	public ConsumerRecord<K, V> receive(String topic, int partition, long offset, Duration pollTimeout) {
		return null;
	}

	@Override
	public ConsumerRecords<K, V> receive(Collection<TopicPartitionOffset> requested, Duration pollTimeout) {
		return null;
	}

	protected ListenableFuture<SendResult<K, V>> doSend(final ProducerRecord<K, V> producerRecord) {
		final Producer<K, V> producer = this.getTheProducer();
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Sending: " + producerRecord);
		}

		final SettableListenableFuture<SendResult<K, V>> future = new SettableListenableFuture();
		producer.send(producerRecord, (Callback) (metadata, exception) -> {
			try {
				if (exception == null) {
					future.set(new SendResult(producerRecord, metadata));
					if (KafkaTemplate.this.producerListener != null && KafkaTemplate.this.producerListener.isInterestedInSuccess()) {
						KafkaTemplate.this.producerListener.onSuccess(producerRecord.topic(), producerRecord.partition(), producerRecord.key(), producerRecord.value(), metadata);
					}
				} else {
					future.setException(new KafkaProducerException(producerRecord, "Failed to send", exception));
					if (KafkaTemplate.this.producerListener != null) {
						KafkaTemplate.this.producerListener.onError(producerRecord.topic(), producerRecord.partition(), producerRecord.key(), producerRecord.value(), exception);
					}
				}
			} finally {
				producer.close();
			}

		});
		if (this.autoFlush) {
			this.flush();
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Sent: " + producerRecord);
		}

		return future;
	}

	private Producer<K, V> getTheProducer() {
		return this.producerFactory.createProducer();
	}
}
