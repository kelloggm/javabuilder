package org.code.codebuilderapp;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class CompileRunService {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private static final String WS_MESSAGE_TRANSFER_DESTINATION =
    "/topic/output";

  CompileRunService(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  public void sendMessages(String userName, String message) {
    simpMessagingTemplate.convertAndSendToUser(
      userName,
      WS_MESSAGE_TRANSFER_DESTINATION,
      new UserProgramOutput(message)
    );
  }
}
