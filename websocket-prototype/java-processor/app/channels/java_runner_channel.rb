class JavaRunnerChannel < ApplicationCable::Channel
  def subscribed
    # stream_from "some_channel"
    stream_from "java_runner_channel_#{params[:user_id]}"
  end

  def unsubscribed
    # Any cleanup needed when channel is unsubscribed
  end

  def run_program(data)
    JavaRunnerHelper.run_java_program(data["filename"], data["text"], data["user_id"])
  end
end
