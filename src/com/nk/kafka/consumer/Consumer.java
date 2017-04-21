package com.nk.kafka.consumer;

import com.nk.kafka.utils.KafkaProperties;
import com.nk.kafka.utils.StringUtilsExt;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class Consumer {

    private final static Consumer instance = new Consumer();

    public final static Consumer getInstance(){
        return instance;
    }

    private final KafkaConsumer<Integer, String> consumer;

    public Consumer() {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, StringUtilsExt.merge(KafkaProperties.KAFKA_SERVER_URL, KafkaProperties.COLON, String.valueOf(KafkaProperties.KAFKA_SERVER_PORT)));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ConsumerMysql");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        consumer = new KafkaConsumer<Integer, String>(props);
    }

    public final static void main(String[] args){
//        Consumer.getInstance().run("test_topic");
    }

    public void run(String topic){
        new Thread(new ConsumerWorker(topic)).start();
    }

    class ConsumerWorker implements Runnable {

        private String topic;
        public ConsumerWorker(String topic) {
            this.topic = topic;
        }

        @Override
        public void run() {
            consumer.subscribe(Collections.singletonList(topic));
            while (true){
                ConsumerRecords<Integer, String> records = consumer.poll(100);
//                try{
//                    Thread.sleep(1000);
//                } catch (Exception e){
//
//                }
                for (ConsumerRecord<Integer, String> record : records) {
                    System.out.println("Received message: (" + record.key() + ", "  + ") at offset " + record.offset());
//                    throw new RuntimeException("手动抛出");
//                    String value = record.value();
//                    List<FundProduct> productList = JSON.parseObject(value, new TypeToken<List<FundProduct>>() {}.getType());
//                    if (productList!= null && productList.size() >0 ) {
//                        try {
//                            DbToVoUtils.insertDb(productList);
//                        } catch (MySQLIntegrityConstraintViolationException e) {
//                            System.out.println();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
        }
    }
}