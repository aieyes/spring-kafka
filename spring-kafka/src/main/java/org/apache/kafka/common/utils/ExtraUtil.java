package org.apache.kafka.common.utils;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.Errors;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: Eric
 * @Email: eyes.left@qq.com
 * @Date: 2023/2/9 15:30
 */
public class ExtraUtil {
    public static Map<Errors, Integer> errorCounts(Errors error) {
        return Collections.singletonMap(error, 1);
    }

    public static Map<Errors, Integer> errorCounts(Stream<Errors> errors) {
        return errors.collect(Collectors.groupingBy(e -> e, Collectors.summingInt(e -> 1)));
    }

    public static Map<Errors, Integer> errorCounts(Collection<Errors> errors) {
        Map<Errors, Integer> errorCounts = new HashMap<>();
        for (Errors error : errors) {
            updateErrorCounts(errorCounts, error);
        }
        return errorCounts;
    }

    public static void updateErrorCounts(Map<Errors, Integer> errorCounts, Errors error) {
        Integer count = errorCounts.getOrDefault(error, 0);
        errorCounts.put(error, count + 1);
    }

    /**
     *  Converts an extensions string into a {@code Map<String, String>}.
     *
     *  Example:
     *      {@code parseMap("key=hey,keyTwo=hi,keyThree=hello", "=", ",") => { key: "hey", keyTwo: "hi", keyThree: "hello" }}
     *
     */
    public static Map<String, String> parseMap(String mapStr, String keyValueSeparator, String elementSeparator) {
        Map<String, String> map = new HashMap<>();

        if (!mapStr.isEmpty()) {
            String[] attrvals = mapStr.split(elementSeparator);
            for (String attrval : attrvals) {
                String[] array = attrval.split(keyValueSeparator, 2);
                map.put(array[0], array[1]);
            }
        }
        return map;
    }

    public static Set<Byte> from32BitField(final int intValue) {
        Set<Byte> result = new HashSet<>();
        for (int itr = intValue, count = 0; itr != 0; itr >>>= 1) {
            if ((itr & 1) != 0)
                result.add((byte) count);
            count++;
        }
        return result;
    }

    /**
     * group data by topic
     *
     * @param data Data to be partitioned
     * @param <T> Partition data type
     * @return partitioned data
     */
    public static <T> Map<String, Map<Integer, T>> groupPartitionDataByTopic(Map<TopicPartition, ? extends T> data) {
        Map<String, Map<Integer, T>> dataByTopic = new HashMap<>();
        for (Map.Entry<TopicPartition, ? extends T> entry : data.entrySet()) {
            String topic = entry.getKey().topic();
            int partition = entry.getKey().partition();
            Map<Integer, T> topicData = dataByTopic.computeIfAbsent(topic, t -> new HashMap<>());
            topicData.put(partition, entry.getValue());
        }
        return dataByTopic;
    }

    /**
     * Group a list of partitions by the topic name.
     *
     * @param partitions The partitions to collect
     * @return partitions per topic
     */
    public static Map<String, List<Integer>> groupPartitionsByTopic(Collection<TopicPartition> partitions) {
        return groupPartitionsByTopic(
                partitions,
                topic -> new ArrayList<>(),
                List::add
        );
    }

    /**
     * Group a collection of partitions by topic
     *
     * @return The map used to group the partitions
     */
    public static <T> Map<String, T> groupPartitionsByTopic(
            Collection<TopicPartition> partitions,
            Function<String, T> buildGroup,
            BiConsumer<T, Integer> addToGroup
    ) {
        Map<String, T> dataByTopic = new HashMap<>();
        for (TopicPartition tp : partitions) {
            String topic = tp.topic();
            T topicData = dataByTopic.computeIfAbsent(topic, buildGroup);
            addToGroup.accept(topicData, tp.partition());
        }
        return dataByTopic;
    }
}
