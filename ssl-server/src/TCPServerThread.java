import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class TCPServerThread extends Thread {

  private static BufferedReader in;
  private static OutputStream out;
  private static Socket s;

  public TCPServerThread(Socket socket) {
    s = socket;
  }

  @Override
  public void run() {
    try {
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

  public static void sendFile(String file) throws IOException {
    int count;
    File myFile = new File(file);
    OutputStream dos = s.getOutputStream();
    FileInputStream fis = new FileInputStream(myFile);
    byte[] buffer = new byte[873];

    while ((count = fis.read(buffer)) > 0) {
      dos.write(buffer);
      System.out.println(Arrays.toString(buffer));
    }

    dos.close();
    fis.close();
  }

}
