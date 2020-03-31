import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
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
      System.out.println("Authenticating connection from: " + s.getRemoteSocketAddress());

      in = new BufferedReader(new InputStreamReader(s.getInputStream()));
      out = s.getOutputStream();

      String username = in.readLine();
      if (authenticate(username)) {
        System.out.println("Authenticated " + username + ". Sending certificate...");

        // send certificate
        sendFile("server_crt.crt");
        s.close();

      } else {
        // close conn
        System.out.println("Wrong username " + username);
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static boolean authenticate(String username) {
    File file = new File("src/users.txt");
    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.equals(username)) {
          return true;
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void sendFile(String file) throws IOException {
    int count;
    File myFile = new File(file);
    System.out.println(myFile.length());
    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
    FileInputStream fis = new FileInputStream(myFile);
    byte[] buffer = new byte[873];

    while ((count = fis.read(buffer)) > 0) {
      System.out.println("oi");
      dos.write(buffer, 0, 873);
      dos.flush();
      System.out.println(Arrays.toString(buffer));
    }

    fis.close();
    dos.close();
  }

}
