package com.nk.kafka.producer;

import com.nk.kafka.utils.CustomCallBack;
import com.nk.kafka.utils.KafkaProperties;
import com.nk.kafka.utils.StringUtilsExt;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by zoushaofei on 16-11-10.
 */
public class Producer {

    private final static Producer instance = new Producer();

    public final static Producer getInstance(){
        return instance;
    }

    private final Logger log = LoggerFactory.getLogger(Producer.class);

    private final Properties props;

    private KafkaProducer<Integer, String> producer = null;

    private Producer(){
        props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, StringUtilsExt.merge(KafkaProperties.KAFKA_SERVER_URL, KafkaProperties.COLON, String.valueOf(KafkaProperties.KAFKA_SERVER_PORT)));
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "0");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

        producer = new KafkaProducer<Integer, String>(props);
    }

    public void run(String topic, Boolean isAsync,String key , String msg) {
        long startTime = System.currentTimeMillis();
        if (isAsync) { // Send asynchronously
            producer.send(new ProducerRecord(topic, key, msg), new CustomCallBack(startTime, key, msg));
        } else { // Send synchronously
            try {
                producer.send(new ProducerRecord(topic, key, msg)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private void sendData(String topic, String key, String value) {

        org.apache.kafka.clients.producer.Producer<String, String> producer = new KafkaProducer<String, String>(props) ;

        try{
            ProducerRecord record = new ProducerRecord(topic, key, value);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if(null != exception){
                        log.error("kafka send data completion error!", exception);
                        return;
                    }
                    log.info("The offset of the record we just sent is: {}", metadata.offset());
                    log.info("The partition of the record we just sent is: {}", metadata.partition());
                }
            });

            log.info("send data over.");
        } catch (Exception e){
            log.error("kafka send data error!", e);
        } finally {
            if(null != producer){
                producer.close();
            }
        }
    }

    public final static void main(String[] args){
//        Producer.getInstance().sendData("test_topic", "test_key", "test_value");
//        Producer.getInstance().run("test_topic", true);
    }
}
