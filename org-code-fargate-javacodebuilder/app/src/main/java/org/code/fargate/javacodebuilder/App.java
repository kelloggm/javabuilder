package org.code.fargate.javacodebuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class App {
  public String getGreeting() {
    return "Hello World!";
  }

  public static String buildUserProject(Map<String, String> userProject) {
    // TODO: CSA-48 Handle more than one file
    if (userProject.size() > 1) {
      return "Error: Can only process one file at a time.";
    }

    File compileRunScript = null;
    String tempDir = System.getProperty("java.io.tmpdir");
    for (Map.Entry<String, String> projectFile : userProject.entrySet()) {
      String className = FilenameUtils.removeExtension(projectFile.getKey());
      String userCode = projectFile.getValue();
      try {
        // Build user's java file
        File userFile = new File(Paths.get(tempDir, projectFile.getKey()).toString());
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
        return "An error occurred creating files.";
      }
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
      return "An error occurred running the program.";
    } catch (IOException e) {
      e.printStackTrace();
      return "An error occurred running the program.";
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
      return "An error occurred reading the program output.";
    }

    return programOutput.toString();
  }

  public static void main(String[] args) {
    JSONParser parser = new JSONParser();
    JSONObject projectData = null;
    try {
      projectData = (JSONObject) parser.parse(new FileReader("src/main/resources/sampleInput.json"));
    } catch (IOException | ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println("Error parsing json file");
    }

    var userProject = new HashMap<String, String>();


    userProject.put("Foo.java", "public class Foo {\n  public static void main(String[] args) {\n    System.out.println(\"Hello Molly!\");\n  }\n}\n");
    System.out.println(App.buildUserProject(projectData));
  }
}
