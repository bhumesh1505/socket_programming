/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket_programming;
import java.net.*; 
import java.io.*; 
import java.util.Date;

/**
 *
 * @author BHUMESH
 */
// Java implementation for a client 
// Save file as Client.java 

import java.util.Scanner; 

// Client class 
public class Client 
{ 
    public static void main(String[] args) throws IOException 
    { 
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + "\\clientLogs\\log.txt";
        
        try
        { 
            Scanner scn = new Scanner(System.in); 
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost"); 

            // establish the connection with server port 5056 
            Socket s = new Socket(ip, 5056); 

            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

            System.out.println("New Client : " + s);            
            System.out.println(dis.readUTF());
            while (true) 
            { 
                String tosend = scn.nextLine(); // read from user terminal
                dos.writeUTF(tosend);   // send msg to server 

                if(tosend.equalsIgnoreCase("Exit")) 
                { 
                    System.out.println("Closing this connection : " + s); 
                    s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                } 
                else if(tosend.equalsIgnoreCase("log"))
                {
                    System.out.println("Getting file from server... : ");
                    int FILE_SIZE = 6022386;
                    byte[] myByteArray = new byte[FILE_SIZE];

                    filePath = projectPath + "\\clientLogs\\log" + s.getLocalPort() + ".txt";
                    int bytesRead = dis.read(myByteArray, 0, myByteArray.length);
                    
                    BufferedOutputStream bufferedOutputStream;
                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    bufferedOutputStream.write(myByteArray, 0, bytesRead);
                    bufferedOutputStream.flush();
                    System.out.println("Get file success...");
                    bufferedOutputStream.close();
                }
                else if(tosend.equalsIgnoreCase("send"))
                {
                    System.out.println("Sending file to Server...");
                    File myFile = new File(filePath);
                    byte[] myByteArray = new byte[(int) myFile.length()];
                    BufferedInputStream bufferedInputStream;
                    try (FileInputStream fileInputStream = new FileInputStream(myFile)) {
                        bufferedInputStream = new BufferedInputStream(fileInputStream);
                        bufferedInputStream.read(myByteArray, 0, myByteArray.length);
                        dos.write(myByteArray, 0, myByteArray.length);
                        dos.flush();
                        System.out.println("Send file success...");
                    }
                    bufferedInputStream.close();
                    String received = dis.readUTF(); 
                    if(received.equalsIgnoreCase("daemon"))
                    {
                        s.close();
                        System.out.println("You are blocked " + s); 
                        break;
                    }
                }
                else
                {
                    String received = dis.readUTF(); 
                    System.out.println(received);
                }	 
            } 

            // closing resources 
            scn.close(); 
            dis.close(); 
            dos.close(); 
        }catch(Exception e){
            System.out.println(e);
        } 
    } 
} 
