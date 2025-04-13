package com.hungum.product.kafka;

import com.hungum.common.event.ProductRequestEvent;
import com.hungum.common.event.ProductResponseEvent;
import com.hungum.product.model.Product;
import com.hungum.product.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaConsumerService(ProductRepository productRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "product.request", groupId = "product-service-group")
    public void listenProductRequest(ProductRequestEvent event) {
        Product product = productRepository.findBySku(event.getSku())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductResponseEvent responseEvent = new ProductResponseEvent(product.getSku(), product.getName(), product.getPrice());

        kafkaTemplate.send("product.response", responseEvent);       }
}
