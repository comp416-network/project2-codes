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
  public final static String TLS_SERVER_ADDRESS = "localhost";
  public final static String MESSAGE_TO_TLS_SERVER = "hello from client";
  public final static int TLS_SERVER_PORT = 53140;

  public static int nextMessageToRequest = 0;

  public static void main(String[] args) throws Exception {
    while (true) {
      SSLConnectToServer sslConnectToServer = new SSLConnectToServer(TLS_SERVER_ADDRESS, TLS_SERVER_PORT);
      sslConnectToServer.Connect();

      String receivedMessage = sslConnectToServer.SendForAnswer(Integer.toString(nextMessageToRequest));
      System.out.println(receivedMessage);

      if (receivedMessage.equals("$$$")) {
        sslConnectToServer.Disconnect();
        break;
      } else {
        nextMessageToRequest++;
      }

      sslConnectToServer.Disconnect();
    }


  }
}
