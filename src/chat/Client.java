// original code taken from: www.geeksforgeeks.org

package chat;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    public static void main(String args[]) throws UnknownHostException, IOException, EOFException
    {
        Scanner scn = new Scanner(System.in);

        // getting ip
        InetAddress ip = InetAddress.getByName("localhost");

        // setting port, nickname, receiver
        String port = "";
        String msgNick = "";
        System.out.print("Port: ");
        port = scn.nextLine();
        System.out.print("Nickname: ");
        msgNick = scn.nextLine();

        String socketInformation = msgNick;

        // establish the connection
        Socket s = new Socket(ip, Integer.parseInt(port));

        // obtaining input and out streams for a socket
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // 1st thread: sendMessage
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                try {
                    // write on the output stream
                    dos.writeUTF(socketInformation);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int choice = 0;
                int counter = 0;
                boolean condition;
                String msg = "MENU";
                String msgRec = "";
                int doubleMenu = 0;
                boolean logoutCond = true;
                do {

                    // read the message to deliver.
                    if(counter!=0 && doubleMenu!=1)
                    {
                        System.out.println(msg);
                        //System.out.print("MESSAGE_1: ");
                        msg = scn.nextLine();
                    }
                    doubleMenu = 0;

                    if(msg.equals("MENU"))
                    {
                        System.out.println("MENU ENABLED: ");
                        System.out.println("1: Chat");
                        System.out.println("2: Recipient");
                        System.out.println("3: Logout");
                        System.out.print("Choice [1-3]: ");

                        do{
                            //System.out.println("SWITCH");
                            choice = scn.nextInt();
                            scn.nextLine();
                            switch (choice)
                            {
                                case 1:
                                {
                                    break;
                                }
                                case 2:
                                {
                                    System.out.print("Receiver: ");
                                    msgRec = scn.nextLine();
                                    break;
                                }
                                case 3:
                                {
                                    msg = "LOGOUT";
                                    break;
                                }
                                default:
                                {
                                    break;
                                }
                            }
                            condition = (choice >= 1 && choice <= 3);
                            //System.out.println(condition);
                        }while(!condition);
                    }
                    try {
                        // write on the output stream
                        if((choice == 1 || choice == 2) && msg.equals("MENU"))
                        {
                            //System.out.print("MESSAGE_2: ");
                            msg = scn.nextLine();
                            if(msg.equals("MENU"))
                            {
                                doubleMenu = 1;
                            }
                        }
                        if(!msg.equals("LOGOUT") && !msg.equals("MENU") && !msg.equals(""))
                        {
                            msg = msg + "#" + msgRec;
                        }
                        if(choice == 3)
                        {
                            System.out.println("Closing the connection...");
                            dos.writeUTF(msg);
                            logoutCond = false;
                            scn.close();
                            System.out.println("Scanner closed");
                            break;
                        }
                        dos.writeUTF(msg);
                    }catch (EOFException e){
                        System.out.println("EOFException: " + e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    counter++;
                    choice = 1;
                } while (logoutCond);

                System.out.println("Connection closed.");
                System.exit(0);
            }
        });

        // 2nd thread: readMessage
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    }catch (EOFException e){
                        //e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}