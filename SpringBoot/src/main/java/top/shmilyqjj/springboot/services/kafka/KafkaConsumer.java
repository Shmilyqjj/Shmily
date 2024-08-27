package top.shmilyqjj.springboot.services.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Shmily
 * @Description: Spring消费kafka
 * @CreateTime: 2024/8/27 下午2:51
 */

@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true", matchIfMissing = false)
@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaListener(id = "local_kafka_consumer",groupId = "${kafka.consumer.group}", topics = {"${kafka.consumer.topic}"})
    public void consume(String message) {
        logger.info("consume msg: {}", message);
    }
}
