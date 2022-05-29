import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

class ClientHandler implements Runnable {
    private Scanner scn = new Scanner(System.in);
    private BufferedReader dis;
    private PrintWriter dos;
    private Socket s;

    private String name;
    private boolean isloggedin;

    public ClientHandler(Socket s, String name,
                         BufferedReader dis, PrintWriter dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {
        String received;

        dos.println("username");

        String username = "";

        try {
            username = dis.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int idx = 0;
        boolean found = false;

        for(int i=0;i<Server.ar.size();i++){
            if(username.equalsIgnoreCase(Server.ar.get(i).name)){
                idx = i;
                found = true;
                break;
            }
        }

        if(found) {
            Server.ar.remove(idx);
            this.isloggedin=false;
            dos.println("error");
            try {
                this.dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.dos.close();
            try {
                this.s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        dos.println("successfull");

        this.name = username;

        while (true) {
            try {
                received = dis.readLine();

                //System.out.println(received);

                if(received.equals("exit")){
                    this.isloggedin=false;
                    this.dis.close();
                    this.dos.close();
                    this.s.close();
                    break;
                }

                StringTokenizer st = new StringTokenizer(received, "@");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                for (ClientHandler mc : Server.ar) {
                    if (mc.name.equals(recipient) && mc.isloggedin==true && mc != this) {
                        mc.dos.println(this.name+":"+MsgToSend);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.dis.close();
            this.dos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}