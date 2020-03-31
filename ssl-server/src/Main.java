import java.util.ArrayList;
import java.util.Arrays;

/**
 * Copyright [2017] [Yahya Hassanzadeh-Nazarabadi]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Main {
  public static void main(String args[]) {
    int sslPort = (64004 + 59860 + 60346) % 65535;

    ArrayList<String> addresses = new ArrayList<>(Arrays.asList("eerdogan17@ku.edu.tr",
            "etekalp16@ku.edu.tr",
            "okolukisa16@ku.edu.tr"));

    // generate messages to be sent to a single client
    ArrayList<String> messages = generateMessages(addresses);
    System.out.println(messages);

    Runnable tcpTask = () -> {
      TCPServer tcpServer = new TCPServer(4242);
    };

    Runnable sslTask = () -> {
      SSLServer s = new SSLServer(sslPort, messages);
    };

    Thread tcpThread = new Thread(tcpTask);
    Thread sslThread = new Thread(sslTask);
    tcpThread.start();
    sslThread.start();
  }

  private static ArrayList<String> generateMessages(ArrayList<String> list) {
    ArrayList<String> result = new ArrayList<>();
    int maxLength = 0;
    for (String str : list) {
      if (str.length() > maxLength) {
        maxLength = str.length();
      }
    }

    for (int i = 0; i <= maxLength; i++) {
      StringBuilder builder = new StringBuilder();
      for (String str : list) {
        if (str.length() > i) {
          builder.append(str.charAt(i));
        } else {
          builder.append("$");
        }
      }
      result.add(builder.toString());
    }

    return result;
  }

}
