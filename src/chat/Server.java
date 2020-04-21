// original code taken from: www.geeksforgeeks.org

package chat;

import java.io.*;
import java.time.format.SignStyle;
import java.util.*;
import java.net.*;

// Server class
public class Server
{

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    // counter for clients
    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        System.out.println("Initializing server...");

        // server is listening for a port, default backlog and InetAddress
        Scanner scn = new Scanner(System.in);
        System.out.print("Port: ");
        int port = Integer.parseInt(scn.nextLine());

        ServerSocket ss = new ServerSocket(port);

        Socket s;
        System.out.println("Server succesfully initialized");

        // running infinite loop for getting client request
        while (true)
        {
            // Accept the incoming request
            s = ss.accept();

            System.out.println("New client request received : " + s);

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            //Establishing parameters for client handler
            String receivedName = "name";

            try {
                // receive the string
                receivedName = dis.readUTF();
            }catch (EOFException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            System.out.println("Creating a new handler for " + receivedName);

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s, receivedName, dis, dos);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            System.out.println("Adding " + receivedName + " to active client list");

            // add this client to active clients list
            ar.add(mtch);

            // start the thread.
            t.start();

            // increment i for new client.
            // i is used for naming only, and can be replaced
            // by any naming scheme
            i++;

        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received = "";
        while (true)
        {
            try
            {
                // receive the string
                String message = "";
                String receiver = "";
                received = dis.readUTF();
                if(received.contains("#"))
                {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    message = st.nextToken();
                    receiver = st.nextToken();
                    System.out.println(name + " to " + receiver + ": " + message);
                }

                try{
                    if(received.equals("LOGOUT")){
                        System.out.println("Closing " + name + " connection...");
                        this.isloggedin=false;
                        this.s.close();
                        System.out.println("    Connection closed");
                        break;
                    }
                }catch (EOFException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

                String MsgToSend = message;
                String recipient = receiver;

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.ar)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(recipient) && mc.isloggedin==true)
                    {
                        mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        break;
                    }
                }
            }catch (EOFException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
            System.out.println("    Resources closed");
        }catch (EOFException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
