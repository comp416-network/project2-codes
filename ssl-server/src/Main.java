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

    Runnable tcpTask = () -> {
      TCPServer tcpServer = new TCPServer(4242);
    };

    Runnable sslTask = () -> {
      SSLServer s = new SSLServer(sslPort);
    };

    Thread tcpThread = new Thread(tcpTask);
    Thread sslThread = new Thread(sslTask);
//    tcpThread.start();
    sslThread.start();
  }

}
