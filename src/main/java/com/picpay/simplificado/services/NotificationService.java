package com.picpay.simplificado.services;

import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;

    @Autowired
    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, Map.class);

        if (!(response.getStatusCode() == HttpStatus.OK)) {
            System.out.println("Notification Service is offline");
            throw new Exception("Notification Service is offline");
        }
    }
}
