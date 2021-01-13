import consumer from "./consumer"

const helloWorld = "public class HelloWorld {public static void main(String[] args) {"
+
  "\t\tSystem.out.println(\"Hello World\");\n"
+
  "\t}\n"
+
  "}\n"

document.addEventListener('turbolinks:load', () => {

  const userId = document.getElementById('user-id').getAttribute('data-user-id');
  let connected = false;

  const connection = consumer.subscriptions.create({channel: "JavaRunnerChannel", user_id: userId}, {
    connected() {
      console.log("connected to channel " + userId);
      connected = true;
    },

    disconnected() {
      // Called when the subscription has been terminated by the server
    },

    received(data) {
      console.log(data);
      const outputDiv = document.getElementById('program-output')
      console.log(outputDiv);
      const stdout = data.stdout
      const stderr = data.stderr
      const status = data.status
      const html = "stdout: " + stdout + " stderr: " + stderr + " status: " + status + "\n"
      outputDiv.innerHtml = outputDiv.innerHtml + html
    },

    run_program(filename, text) {
      console.log("calling run program");
      this.perform("run_program", {filename: filename, text: text, user_id: userId})
    }
  });
  console.log(connection);
  connection.run_program("HelloWorld.java", helloWorld);
})

const sleep = (milliseconds) => {
  return new Promise(resolve => setTimeout(resolve, milliseconds))
}
