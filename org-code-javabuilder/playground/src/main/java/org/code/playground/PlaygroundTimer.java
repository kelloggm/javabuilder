package org.code.playground;

public class PlaygroundTimer {
  private boolean messageReceived;
  private long messageReceivedTimeMs;

  private boolean drawStarted;
  private long drawStartedTimeMs;

  private boolean drawOccurred;
  private long drawEndedTimeMs;

  private boolean updateDispatched;
  private long updateDispatchTimeMs;

  public PlaygroundTimer() {
    this.messageReceived = false;
    this.messageReceivedTimeMs = 0;
  }

  public void onMessageReceived() {
    this.messageReceived = true;
    this.messageReceivedTimeMs = System.currentTimeMillis();
  }

  public void onUpdateDispatched() {
    if (this.messageReceived) {
      this.updateDispatchTimeMs = System.currentTimeMillis();
      this.publishLatency();
      this.messageReceived = false;
    }
  }

  private void publishLatency() {
    System.out.println("------ [PLAYGROUND] ------");
    System.out.printf(
        "Javabuilder message to update latency: %.4f\n",
        ((this.updateDispatchTimeMs - this.messageReceivedTimeMs) / 1000.0));
  }
}
