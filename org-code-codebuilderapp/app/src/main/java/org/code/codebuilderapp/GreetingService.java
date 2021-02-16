package org.code.codebuilderapp;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private static final String WS_MESSAGE_TRANSFER_DESTINATION =
    "/topic/greetings";

  // private List<String> userNames = new ArrayList<>();

  GreetingService(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  public void sendMessages(String userName, String message) {
    simpMessagingTemplate.convertAndSendToUser(
      userName,
      WS_MESSAGE_TRANSFER_DESTINATION,
      new Greeting(message)
    );
  }
  //   public void addUserName(String username) {
  //     userNames.add(username);
  // }
}
