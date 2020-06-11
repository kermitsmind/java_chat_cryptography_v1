// original code taken from: www.geeksforgeeks.org

package chat;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client
{
    public static void main(String args[]) throws UnknownHostException, IOException, EOFException
    {
        OneTimePad otp = new OneTimePad();
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
                        msg= scn.nextLine();



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

                            //!!!
                            //!!!!!!!!! Dodany kod: wysylaie plikow //////
                            System.out.println("Your next message will be:");
                            System.out.println("1: File path");
                            System.out.println("2: Text message");
                            int option = scn.nextInt();
                            scn.nextLine();
                            switch (option) {
                                case 1: {

                                    System.out.println("Input File Name: ");

                                    String inputFileName = scn.nextLine().trim();


                                    File input = new File(inputFileName);
                                    String contents = null;
                                    try {
                                        contents = new String(Files.readAllBytes(Paths.get(inputFileName)));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String key = otp.getKey(contents);
                                    contents = otp.getEncryptedText(contents,key);
                                    msg =inputFileName + "--f--" + contents+(char)167+key;

                                    System.out.println(msg);
                                    break;
                                }
                                case 2: {
                                    System.out.print("Your text message: ");
                                    msg = scn.nextLine();
                                    String key = otp.getKey(msg);
                                    msg = otp.getEncryptedText(msg,key);
                                    msg=msg+(char)167+key;
                                    break;
                                }
                            }
                            //msg = scn.nextLine();
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
                        //Sprawdzenie czy przesłana wiadomosć to plik
                        System.out.println("INPUT:"+ msg);
                        if (msg.indexOf("--f--")!=-1) {
                            int fileNameEnd = msg.indexOf("--f--");
                            int fileBegin = msg.indexOf("--f--") + 5;
                            String fileName = msg.substring(0,fileNameEnd);
                            String fileContents = msg.substring(fileBegin);

                            File receivedFile = new File("recived.txt");
                            Writer writer = new FileWriter(receivedFile);
                            int index = fileContents.indexOf((char)167);
                            String key = fileContents.substring(index+1);
                            fileContents.trim();
                            fileContents=otp.getDecryptedText(fileContents.substring(0,index),key);
                            writer.write(fileContents);
                            writer.close();
                            System.out.print("Received a file: " + fileName);
                        }

                        else {

                            int i = msg.indexOf(':');
                            System.out.println(i);
                            String from = msg.substring(0,i);
                            msg = msg.substring(i+1);
                            System.out.println(from+"send you encrypted message:"+ msg );
                            int index = msg.indexOf((char)167);
                            String key = msg.substring(index+1);
                            msg.trim();
                            System.out.println(key);
                            System.out.println("Recived decrypted message:"+otp.getDecryptedText(msg.substring(0,index),key));
                            System.out.println();
                        }
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