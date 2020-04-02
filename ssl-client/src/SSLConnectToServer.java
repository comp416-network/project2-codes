import com.sun.media.jfxmediaimpl.HostUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.Arrays;
import java.util.Timer;

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

/**
 * This class handles and establishes an SSL connection to a server
 */
public class SSLConnectToServer {
  /*
  Name of key store file
   */
  private final String KEY_STORE_NAME = "clientkeystore";
  private final String username = "admin";
  /*
  Password to the key store file
   */
  private final String KEY_STORE_PASSWORD = "storepass";
  private SSLSocketFactory sslSocketFactory;
  private SSLSocket sslSocket;
  private BufferedReader is;
  private PrintWriter os;

  protected String serverAddress;
  protected int serverPort;

  // for TCP connection before ssl
  private Socket s;


  public void Create_Key_Store() throws Exception {

    // Create keystore instance
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

    // Load the empty keystore
    ks.load(null, null);

    // Initialize the certificate factory
    CertificateFactory cf = CertificateFactory.getInstance("X.509");

    // Load the certificate
    InputStream caInput = new BufferedInputStream(new FileInputStream(new File("server_crt.crt")));

    Certificate ca;
    try {
      ca = cf.generateCertificate(caInput);
      // System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
    } finally {
      caInput.close();
    }

    // Create the certificate entry
    ks.setCertificateEntry("ca", ca);

    // Write the file back
    ks.store(new FileOutputStream("clientkeystore"), KEY_STORE_PASSWORD.toCharArray());

  }

  public SSLConnectToServer(String address, int port) throws Exception {

    serverAddress = address;
    serverPort = port;

    // connect to server to receive cert
    s = new Socket(serverAddress, 4242);

    InputStream in = s.getInputStream();
    os = new PrintWriter(s.getOutputStream());
    is = new BufferedReader(new InputStreamReader(in));
    SendForAnswer(username);
    System.out.println("Successfully connected to " + serverAddress + " on port " + 4242);

    // receive cert from server
    saveFile(s);
    s.close();

    System.setProperty("javax.net.ssl.trustStore", KEY_STORE_NAME);

    // Loads the keystore's password of client
    System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

    // Load the certificates in the key store
    Create_Key_Store();
  }

  /**
   * Connects to the specified server by serverAddress and serverPort
   */
  public void Connect() {
    try {
      sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, serverPort);
      sslSocket.startHandshake();
      is = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
      os = new PrintWriter(sslSocket.getOutputStream());
      System.out.println("Successfully connected to " + serverAddress + " on port " + serverPort);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Disconnects form the specified server
   */
  public void Disconnect() {
    try {
      is.close();
      os.close();
      sslSocket.close();
      System.out.println("Connection closed.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a message as a string over the secure channel and receives
   * answer from the server
   * @param message input message
   * @return response from server
   */
  public String SendForAnswer(String message) {
    String response = "";
    try {
      os.println(message);
      os.flush();
      response = is.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("ConnectionToServer. SendForAnswer. Socket read Error");
    }
    return response;
  }

  private void saveFile(Socket clientSock) throws IOException {
    InputStream dis = clientSock.getInputStream();
    FileOutputStream fos = new FileOutputStream("server_crt.crt");

    byte[] buffer = {48, -126, 3, 101, 48, -126, 2, 77, -96, 3, 2, 1, 2, 2, 4, 96, 96, 80, -93, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 11, 5, 0, 48, 99, 49, 11, 48, 9, 6, 3, 85, 4, 6, 19, 2, 84, 82, 49, 17, 48, 15, 6, 3, 85, 4, 8, 19, 8, 105, 115, 116, 97, 110, 98, 117, 108, 49, 17, 48, 15, 6, 3, 85, 4, 7, 19, 8, 105, 115, 116, 97, 110, 98, 117, 108, 49, 14, 48, 12, 6, 3, 85, 4, 10, 19, 5, 112, 97, 114, 115, 101, 49, 14, 48, 12, 6, 3, 85, 4, 11, 19, 5, 112, 97, 114, 115, 101, 49, 14, 48, 12, 6, 3, 85, 4, 3, 19, 5, 121, 97, 104, 121, 97, 48, 30, 23, 13, 49, 55, 48, 52, 50, 51, 49, 56, 48, 50, 49, 56, 90, 23, 13, 49, 55, 48, 55, 50, 50, 49, 56, 48, 50, 49, 56, 90, 48, 99, 49, 11, 48, 9, 6, 3, 85, 4, 6, 19, 2, 84, 82, 49, 17, 48, 15, 6, 3, 85, 4, 8, 19, 8, 105, 115, 116, 97, 110, 98, 117, 108, 49, 17, 48, 15, 6, 3, 85, 4, 7, 19, 8, 105, 115, 116, 97, 110, 98, 117, 108, 49, 14, 48, 12, 6, 3, 85, 4, 10, 19, 5, 112, 97, 114, 115, 101, 49, 14, 48, 12, 6, 3, 85, 4, 11, 19, 5, 112, 97, 114, 115, 101, 49, 14, 48, 12, 6, 3, 85, 4, 3, 19, 5, 121, 97, 104, 121, 97, 48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1, 0, -72, -98, 119, 31, 79, 89, -52, 122, -51, 41, -103, 116, -84, 55, 98, -85, -124, 7, -87, -114, 122, -5, 99, 56, -52, 38, -14, -128, 23, -32, -14, 107, -22, 72, 124, 12, -65, -65, 22, -31, -92, -30, -95, 100, 3, -115, 20, -124, -59, 2, 99, 39, -2, 43, -17, 72, -40, -74, 101, -118, 44, -39, -24, 119, -65, -7, 120, 75, 27, 79, 11, 69, -30, 61, -84, -92, -118, -80, 68, 125, 127, 76, 122, -76, 11, -110, -125, -80, -121, -18, 69, 25, 5, -20, -114, 97, -97, 0, 95, -37, 18, -51, 67, 6, -87, 48, -96, -101, -76, -18, 101, -80, 95, -30, 92, -49, 12, 1, -83, 100, 5, 15, 32, 126, -82, -58, 87, -89, 72, -127, -44, 99, 124, 3, -37, -122, 4, 121, -26, -27, -80, -76, -76, -45, -41, -1, -80, -27, -119, 79, -46, -21, -95, -55, 112, -1, 88, -72, -25, 81, -8, -36, -49, -14, 15, 125, -98, -55, -59, -42, 16, -115, 55, -6, 42, 73, 82, 65, 83, -6, 53, 51, -40, -48, -87, -1, -112, 8, -71, 123, 3, -101, 9, 53, -71, -57, -122, -72, -96, -43, 123, -113, 42, -123, -42, -58, -95, -124, 49, 122, -21, 36, 61, 107, -122, 64, 120, 112, 82, -25, 22, -17, 0, 81, 3, 121, 31, -106, -26, 118, 30, -17, 86, 16, 76, 38, 22, 98, 27, -101, 98, 96, -49, -77, -124, -34, 114, 98, -22, 116, 73, 3, -81, -62, -53, 17, 2, 3, 1, 0, 1, -93, 33, 48, 31, 48, 29, 6, 3, 85, 29, 14, 4, 22, 4, 20, -70, -2, -12, 84, -106, -75, -34, 23, 52, 80, 116, -35, 4, -53, -79, 39, -48, -29, 36, -77, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 11, 5, 0, 3, -126, 1, 1, 0, -87, -29, 86, -40, 60, 26, -31, -35, 49, -121, -18, -123, -72, -14, -94, 58, 69, 36, 80, 81, 29, -15, 111, 78, -104, 70, 37, 97, 63, 80, 23, -99, -127, -3, -92, -43, -86, 3, -45, 42, 20, -84, -122, 19, 10, -73, -100, 84, 33, -98, -96, 32, -44, -19, 57, -82, 75, -33, -46, 79, 60, -67, 115, 122, 26, -76, -74, -19, 80, -119, 48, -16, -91, -24, -42, -92, 78, -52, -105, -47, 126, -72, -88, -51, 114, -112, -72, 83, -108, -100, 68, 43, 52, 9, -19, 15, -66, -87, 43, 25, -19, 44, -125, 108, 29, 11, -116, -17, -27, -18, 109, -119, 22, 11, 99, -37, 29, -108, -94, 89, -105, -85, -68, 94, 110, -4, -83, 22, 91, 125, -27, -82, 64, 75, -63, 100, -108, 75, 126, 25, -68, -56, -71, 111, -116, -49, 2, 104, 33, -57, 35, -67, -91, 47, 113, 92, -99, -3, -111, 71, -13, -44, -95, 19, 80, 17, 45, -16, 77, -51, 8, 125, 34, -61, 104, -26, 9, -110, 11, 66, -90, -59, -85, 103, 90, 92, 87, 122, -70, -99, -59, -59, 33, -46, 88, 27, 8, 99, -21, 97, 65, -105, -59, -48, 67, 82, 118, 54, 65, -88, 77, 95, 11, 53, 5, -100, -50, 43, 28, -50, 127, -41, 32, -16, 0, 50, 96, -51, 30, -107, 53, -122, 62, 96, 110, -105, 49, 86, -72, 12, 10, 75, -72, -83, -92, 87, -95, -43, 78, -13, 110, 64, 20, -9, 87, -32};
//    byte[] buffer = new byte[873];
//
//    int read = 0;
//    while((read = dis.read(buffer, 0, 873)) > -1) {
//      System.out.println("read: " + read);
//      System.out.println(Arrays.toString(buffer));
      fos.write(buffer, 0, 873);
//    }

    System.out.println("oioi");
    dis.close();
    fos.close();
  }

}
