package com.hungum.mail.kafka;

import com.hungum.common.event.SendMailEvent;
import com.hungum.mail.service.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final MailService mailService;

    public KafkaConsumerService(MailService mailService) {
        this.mailService = mailService;
    }

         @KafkaListener(topics = "send.mail", groupId = "mail-service-group")
    public void listenSendMail(SendMailEvent event) {
                 mailService.sendMail(event.getTo(), event.getContent());
        System.out.println("Mail sent to " + event.getTo());
    }
}
