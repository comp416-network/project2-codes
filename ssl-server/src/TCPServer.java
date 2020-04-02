import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class TCPServer {

  private int port;
  private ServerSocket serverSocket;
  private Socket s;

  private OutputStream out;
  private BufferedReader in;

  public TCPServer(int port) {
    this.port = port;

    try {
      serverSocket = new ServerSocket(port);
      System.out.println("TCP Server up and running on port " + port);

      while (true) {
        listenAndAccept();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void listenAndAccept() {
    try {
      s = serverSocket.accept();
      TCPServerThread thread = new TCPServerThread(s);
      thread.start();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }



}
