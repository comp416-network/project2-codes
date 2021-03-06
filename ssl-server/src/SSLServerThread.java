import javax.net.ssl.SSLSocket;
import java.io.*;
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


public class SSLServerThread extends Thread {

  private final String SERVER_REPLY = "Hello Client";
  private SSLSocket sslSocket;
  private String line = new String();
  private BufferedReader is;
  private PrintWriter os;

  private ArrayList<String> messages;

  public SSLServerThread(SSLSocket s, ArrayList<String> messages) {
    this.messages = messages;
    sslSocket = s;
  }

  public void run() {
    try {
      is = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
      os = new PrintWriter(sslSocket.getOutputStream());
    } catch (IOException e) {
      System.out.println("Server Thread. Run. IO error in server thread");
    }

    try {
      line = is.readLine();
      System.out.println("Client sent: " + line);
      int index = Integer.parseInt(line);
      os.write(messages.get(index));
      os.flush();
      System.out.println("Sent: " + messages.get(index));

      // get index of last received message from client
      // send next message in list

    } catch (IOException e) {
      line = this.getClass().toString(); //reused String line for getting thread name
      System.out.println("Server Thread. Run. IO Error/ Client " + line + " terminated abruptly");
    } catch (NullPointerException e) {
      line = this.getClass().toString(); //reused String line for getting thread name
      System.out.println("Server Thread. Run.Client " + line + " Closed");
    } finally {
      try {
        System.out.println("Closing the connection");
        if (is != null) {
          is.close();
          System.out.println(" Socket Input Stream Closed");
        }

        if (os != null) {
          os.close();
          System.out.println("Socket Out Closed");
        }
        if (sslSocket != null) {
          sslSocket.close();
          System.out.println("Socket Closed");
        }

      } catch (IOException ie) {
        System.out.println("Socket Close Error");
      }
    }//end finally
  }

}
