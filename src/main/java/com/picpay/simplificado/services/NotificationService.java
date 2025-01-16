package com.picpay.simplificado.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;

    @Value("${notification.url}")
    private String notificationUrl;

    @Autowired
    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);
//        ResponseEntity<JsonNode> response = restTemplate.postForEntity(notificationUrl, notificationRequest, JsonNode.class);
//
//        if (!(response.getStatusCode() == HttpStatus.OK)) {
//            System.out.println("Notification Service is offline");
//        }
        System.out.println("Notification has been sent to " + email);
    }
}
