package org.code.codebuilderapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public Greeting greeting(HelloMessage message) throws Exception {


    // TODO: CSA-48 Handle more than one file
    // if (userProject.size() > 1) {
    //   return "Error: Can only process one file at a time.";
    // }

    File compileRunScript = null;
    String tempDir = System.getProperty("java.io.tmpdir");

    String filename = message.getName();
    String className = FilenameUtils.removeExtension(filename);
    String userCode = message.getCode();
    try {
      // Build user's java file
      File userFile = new File(Paths.get(tempDir, filename).toString());
      FileWriter writer = new FileWriter(userFile);
      writer.write(userCode);
      writer.close();

      // Create the compile & run script
      compileRunScript = File.createTempFile("script", null);
      FileWriter scriptWriter = new FileWriter(compileRunScript);
      scriptWriter.write("javac " + userFile.getAbsolutePath());
      scriptWriter.write(System.getProperty("line.separator"));
      scriptWriter.write("java -cp " + tempDir + " " + className);
      scriptWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      // TODO: CSA-42 Improve error handling
      return new Greeting("An error occurred creating files.");
    }

    ProcessBuilder pb = new ProcessBuilder().command("bash", compileRunScript.toString());

    Process process = null;
    try {
      // Execute the user's code
      // TODO: CSA-42 Handle infinite loops, malicious code, etc.
      process = pb.start();
      process.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return new Greeting("An error occurred running the program.");
    } catch (IOException e) {
      e.printStackTrace();
      return new Greeting("An error occurred running the program.");
    }

    StringBuilder programOutput = new StringBuilder();
    try {
      // Get output from the user's program
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = null;
      while ((line = reader.readLine()) != null) {
        programOutput.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return new Greeting("An error occurred reading the program output.");
    }

    return new Greeting("> " + programOutput.toString());
  }
}