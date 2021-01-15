require 'pty'

class JavaRunner
  attr_accessor :currently_running

  def initialize(user_id)
    @user_id = user_id
    @write = nil
    @currently_running = false
  end

  def addToStdin(data)
    if (@write)
      @write.puts(data)
    end
  end

  def run_java_program(filename, text)
    @currently_running = true
    if !(filename.end_with?(".java"))
      raise ArgumentError "filename must end with .java"
    end
    Dir.mktmpdir do |dir|
      filepath = "#{dir}/#{filename}"
      File.open(filepath, "w") do |f|
        f.write text
      end
      # compile
      compile_cmd = "javac #{filepath}"

      ActionCable.server.broadcast "java_runner_channel_#{@user_id}", stdout: "javac #{filename}"
      stdout, stderr, status = Open3.capture3(compile_cmd)
      ActionCable.server.broadcast "java_runner_channel_#{@user_id}", stdout: stdout, stderr: stderr, status: status

      java_classname = filename.delete_suffix('.java')
      directory_cmd = " -cp #{dir}:"
      ActionCable.server.broadcast "java_runner_channel_#{@user_id}", stdout: "java #{java_classname}"
      run_cmd = "java#{directory_cmd} #{java_classname}"
      read, @write, pid = PTY.spawn(run_cmd)
      while !Process.waitpid(pid, Process::WNOHANG)
        begin
          read.each{|line| ActionCable.server.broadcast "java_runner_channel_#{@user_id}", stdout: line }
        rescue => e
          puts "hit error #{e}"
        end
      end
      puts "exiting"
      @currently_running = false
      @write = nil
    end
  end
end