class JavaRunnerChannel < ApplicationCable::Channel
  @@java_runners = {}

  def subscribed
    # stream_from "some_channel"
    stream_from "java_runner_channel_#{params[:user_id]}"
  end

  def unsubscribed
    # Any cleanup needed when channel is unsubscribed
  end

  def run_program(data)
    user_id = data["user_id"]
    runner = @@java_runners[user_id]
    if (!runner)
      runner = JavaRunner.new(user_id)
      @@java_runners[user_id] = runner
    end
    if (runner.currently_running)

      ActionCable.server.broadcast "java_runner_channel_#{user_id}", "This user is already running a program"
    end
    runner.run_java_program(data["filename"], data["text"])
  end

  def send_input(data)
    user_id = data["user_id"]
    runner = @@java_runners[user_id]
    if !runner
      ActionCable.server.broadcast "java_runner_channel_#{user_id}", "This user is already running a program"
    end
    runner.addToStdin(data["data"])
  end
end
