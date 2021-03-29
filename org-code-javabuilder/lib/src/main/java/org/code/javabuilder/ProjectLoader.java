package org.code.javabuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProjectLoader {
  private final URL projectURL;
  public ProjectLoader(URL projectURL) {
    this.projectURL = projectURL;
  }

  public String getProject() {
    HttpURLConnection con = (HttpURLConnection) projectURL.openConnection();
    con.setRequestMethod("GET");
    con.setConnectTimeout(5000);
    con.setReadTimeout(5000);
    Reader streamReader = null;

    if (con.getResponseCode() > 299) {
      streamReader = new InputStreamReader(con.getErrorStream());
    } else {
      streamReader = new InputStreamReader(con.getInputStream());
    }
    BufferedReader in = new BufferedReader(streamReader);
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }

    in.close();
    con.disconnect();
    return content.toString();
  }
}
