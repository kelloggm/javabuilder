package org.code.lambda.javabuilder;

public class InputService {
  private final OutputService outputService;
  InputService(OutputService outputService) {
    this.outputService = outputService;
  }

  public void acceptInput(UserInput userInput) {
    this.outputService.acknowledgeInput(userInput);
  }
}
