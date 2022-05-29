import java.io.*;
import java.util.*;
import java.net.*;

public class Server
{

    static Vector<ClientHandler> ar = new Vector<>();

    private static int i = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(31337);

        Socket s;

        while (true) {
            s = ss.accept();

            System.out.println("New client request received : " + s);

            BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter dos = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
            ClientHandler mtch = new ClientHandler(s,"", dis, dos);

            ar.add(mtch);

            Thread t = new Thread(mtch);
            t.start();

            i++;
        }
    }
}