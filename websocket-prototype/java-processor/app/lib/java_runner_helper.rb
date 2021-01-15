require 'open3'

class JavaRunnerHelper
  def self.run_java_program(filename, text, current_user_id)
    if !(filename.end_with?(".java"))
      raise ArgumentError "filename must end with .java"
    end
    Dir.mktmpdir do |dir|
      filepath = "#{dir}/#{filename}"
      # create file
      File.open(filepath, "w") do |f|
        f.write text
      end
      # compile
      compile_cmd = "javac #{filepath}"
      ActionCable.server.broadcast "java_runner_channel_#{current_user_id}", stdout: "javac #{filename}"
      stdout, stderr, status = Open3.capture3(compile_cmd)

      ActionCable.server.broadcast "java_runner_channel_#{current_user_id}", stdout: stdout, stderr: stderr, status: status
      java_classname = filename.delete_suffix('.java')
      directory_cmd = " -cp #{dir}:"
      run_cmd = "java #{java_classname}"
      ActionCable.server.broadcast "java_runner_channel_#{current_user_id}", stdout: run_cmd
      stdout, stderr, status = Open3.capture3("java#{directory_cmd} #{java_classname}")
      ActionCable.server.broadcast "java_runner_channel_#{current_user_id}", stdout: stdout, stderr: stderr, status: status

    end
  end



  def self.test_java_runner
    scanner_text = "import java.util.Scanner;
public class ScannerSample {
	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		System.out.println(\"Enter your name: \");
		String name = console.next();
		System.out.println(\"Hello \" + name);
	}
}
"
    jr = JavaRunner.new(1)
    thr = Thread.new{ jr.run_java_program("ScannerSample.java", scanner_text) }
    sleep(3)
    puts "finished sleep"
    jr.addToStdin("Hello")
    puts "added to stdin?"
    thr.join
  end
end

