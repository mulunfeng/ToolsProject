package com.nk.kafka.consumer;

import com.nk.kafka.utils.KafkaProperties;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleConsumerDemo {

    private static void printMessages(ByteBufferMessageSet messageSet) throws UnsupportedEncodingException {
        for (MessageAndOffset messageAndOffset : messageSet) {
            ByteBuffer payload = messageAndOffset.message().payload();
            byte[] bytes = new byte[payload.limit()];
            payload.get(bytes);
            System.out.println(new String(bytes, "UTF-8"));
        }
    }

    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition, long whichTime, String clientName) {

        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
        OffsetRequest request = new OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
        OffsetResponse response = consumer.getOffsetsBefore(request);

        if (response.hasError()) {
            System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition));
            return 0;
        }

        long[] offsets = response.offsets(topic, partition);
//      long[] offsets2 = response.offsets(topic, 3);
        return offsets[0];
    }

    public static void main(String[] args) throws Exception {

        SimpleConsumer simpleConsumer = new SimpleConsumer(KafkaProperties.KAFKA_SERVER_URL,
                KafkaProperties.KAFKA_SERVER_PORT,
                KafkaProperties.CONNECTION_TIMEOUT,
                KafkaProperties.KAFKA_PRODUCER_BUFFER_SIZE,
                KafkaProperties.CLIENT_ID);

        System.out.println("Testing single fetch");
        FetchRequest req = new FetchRequestBuilder()
                .clientId(KafkaProperties.CLIENT_ID)
                .addFetch(KafkaProperties.TOPIC2, 0, 0L, 100)
                .build();
        FetchResponse fetchResponse = simpleConsumer.fetch(req);
        printMessages(fetchResponse.messageSet(KafkaProperties.TOPIC2, 0));

        System.out.println("Testing single multi-fetch");
        Map<String, List<Integer>> topicMap = new HashMap<String, List<Integer>>();
        topicMap.put(KafkaProperties.TOPIC2, Collections.singletonList(0));
        topicMap.put(KafkaProperties.TOPIC3, Collections.singletonList(0));
        req = new FetchRequestBuilder()
                .clientId(KafkaProperties.CLIENT_ID)
                .addFetch(KafkaProperties.TOPIC2, 0, 0L, 100)
                .addFetch(KafkaProperties.TOPIC3, 0, 0L, 100)
                .build();

        while (true) {
            fetchResponse = simpleConsumer.fetch(req);


//            long readOffset = getLastOffset(simpleConsumer, KafkaProperties.TOPIC2, partition, OffsetRequest.LatestTime(), clientName);

            int fetchReq = 0;
            for (Map.Entry<String, List<Integer>> entry : topicMap.entrySet()) {
                String topic = entry.getKey();
                for (Integer offset : entry.getValue()) {
                    System.out.println("Response from fetch request no: " + ++fetchReq);
                    printMessages(fetchResponse.messageSet(topic, offset));
                }
            }

            Thread.sleep(1000L);
        }
    }
}