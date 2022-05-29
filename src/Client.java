import com.vveed.AESUtil;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1337;
    private boolean connected = true;

    public Client() throws IOException {
        Scanner scn = new Scanner(System.in);

       // InetAddress ip = InetAddress.getByName("localhost");

        Socket s = new Socket("192.168.0.26", ServerPort);

        BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter dos = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);

        String serverMessage = dis.readLine();

        if(serverMessage.equalsIgnoreCase("username")){
            System.out.println("[*] Enter your username: ");
            String user = scn.nextLine();
            dos.println(user);
        }

        serverMessage = dis.readLine();

        if(serverMessage.equalsIgnoreCase("error")){
            System.out.println("[!] Unsuccessfull login, try using different username");
            s.close();
            dis.close();
            dos.close();
            return;
        }

        System.out.println("[+] Successfull login!");

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (connected) {
                    String msg = scn.nextLine();
                    try {
                        String content = "";
                        String recipient = "";
                        String data[] = msg.split("@");

                        if(msg.equalsIgnoreCase("exit")) {
                            dos.println("exit");
                            cleanUp(s, dos, dis);
                            break;
                        }

                        if(data.length < 2)
                            continue;

                        if(data[0].isEmpty()){
                            continue;
                        }

                        if(data[1].isEmpty()){
                            continue;
                        }

                        content = data[0];
                        recipient = data[1];

                        String poruka = AESUtil.encrypt(content)+"@"+recipient;

                        dos.println(poruka);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "";
                while (connected) {
                    try {

                        msg = dis.readLine();

                        String messageData[] = msg.split(":");
                        String whoSent = "";
                        String content = "";

                        if(messageData.length >= 2) {
                            whoSent = messageData[0];
                            content = messageData[1];
                        }

                        System.out.println(whoSent+": " + AESUtil.decrypt(content));
                    } catch (IOException e) {
//                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }

    public static void main(String args[]) throws UnknownHostException, IOException {
        try{
            new Client();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void cleanUp(Socket socket, PrintWriter dos, BufferedReader dis)
            throws IOException {
        connected = false;
        if(socket != null)
            socket.close();
        if(dos != null)
            dos.close();
        if(dis != null)
            dis.close();

    }
}
