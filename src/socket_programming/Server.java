/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket_programming;

/**
 *
 * @author BHUMESH
 */

import java.net.*;
import java.io.*;
// Java implementation of Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 

import java.text.*; 
import java.util.*; 

// Server class 
public class Server 
{ 
    public final static int SOCKET_PORT = 5056;  // you may change this
    
    public static void main(String[] args) throws IOException 
    { 
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(SOCKET_PORT); 
        
        System.out.println("Server running ... ");
        // running infinite loop for getting 
        // client request 
        while (true) 
        { 
            Socket s = null; 

            try
            {
                // socket object to receive incoming client requests
                s = ss.accept(); 

                System.out.println("A new client is connected : " + s); 
                
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

                System.out.println("Assigning new thread for this client"); 

                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos); 

                // Invoking the start() method 
                t.start(); 
            } 
            catch (Exception e){
                s.close(); 
                e.printStackTrace(); 
            }
        } 
    }
} 

// ClientHandler class 
class ClientHandler extends Thread 
{ 
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 

    String projectPath;
    String filePath;
    String logFile;
    File myFile;
    //FileInputStream fileInputStream = null;
    //BufferedInputStream bufferedInputStream = null;

    private void appendStrToFile(String fileName, String str) 
    { 
        try { 
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true)); 
            out.write("\n"+str); 
            out.close(); 
        }
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        } 
    }
    
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) 
    {
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        projectPath = System.getProperty("user.dir");
        logFile = "log" + s.getPort() + ".txt";
        filePath = projectPath + "\\serverLogs\\" + logFile;
        appendStrToFile(filePath, "Logs for " + logFile + " ... ");
        appendStrToFile(filePath, "Client Information : " + s );
        appendStrToFile(filePath, "New Client connected at : " + new Date() );
    } 

    @Override
    public void run() 
    { 
        try 
        { 
            String received; 
            String toreturn;
            byte[] myByteArray = new byte[1] ;
            
            int count = 0;
            dos.writeUTF("What do you want?[Date | Time | log | send ]..\n"+ 
                                            "Type Exit to terminate connection."); 
            boolean stop = false;
            while (!stop) 
            {
                // receive the answer from client 
                received = dis.readUTF();
                appendStrToFile(filePath, "Client request (" + new Date() + ") : " + received );
                
                switch (received.toLowerCase()) 
                { 
                    case "date" : 
                        {
                            Date date = new Date(); 
                            toreturn = fordate.format(date); 
                            dos.writeUTF(toreturn);
                            appendStrToFile(filePath, "Client request status (" + new Date() + ") : " + received + " => " + "Success" );
                            break; 
                        }
                    case "time" : 
                        {
                            Date date = new Date(); 
                            toreturn = fortime.format(date); 
                            dos.writeUTF(toreturn);
                            appendStrToFile(filePath, "Client request status (" + new Date() + ") : " + received + " => " + "Success" ); 
                            break;
                        }
                    case "log":
                        {
                            System.out.println("Sending file to client...");
                            myFile = new File(filePath);
                            myByteArray = new byte[(int) myFile.length()];
                            BufferedInputStream bufferedInputStream;
                            try (FileInputStream fileInputStream = new FileInputStream(myFile)) {
                                bufferedInputStream = new BufferedInputStream(fileInputStream);
                                bufferedInputStream.read(myByteArray, 0, myByteArray.length);
                                dos.write(myByteArray, 0, myByteArray.length);
                                dos.flush();
                                System.out.println("Send file success...");
                            }
                            bufferedInputStream.close();
                            appendStrToFile(filePath, "Client request status (" + new Date() + ") : " + received + " => " + "Success" );
                            break;
                        }
                    case "send":
                        {
                            System.out.println("Getting file from Client... : ");
                            
                            int FILE_SIZE = 6022386;
                            byte[] myByteArrayReceived = new byte[FILE_SIZE];

                            //filePath = projectPath + "\\clientLogs\\log" + s.getLocalPort() + ".txt";
                            int bytesRead = dis.read(myByteArrayReceived, 0, myByteArrayReceived.length);
                            
                            
                            System.out.println("bytes read : " + bytesRead);
                            System.out.println(bytesRead + " " + myByteArray.length );
                            
                            boolean status = true;
                            int i=0;
                            if(myByteArray.length != bytesRead)
                            {
                                status = false;
                            }
                            else
                            {
                                while(i < bytesRead && status)
                                {
                                    if(myByteArrayReceived[i] != myByteArray[i] )
                                    {
                                        status = false;
                                    }
                                    i++;
                                }
                            }
                            System.out.println("Compare log files before and after :  " + status );
                            if(status == false)
                            {
                                // notify
                            }
                            appendStrToFile(filePath, "Client request status (" + new Date() + ") : " + received + " => " + "Success" );
                            break;
                        }
                    case "exit":
                        {
                            System.out.println("Client " + this.s + " sends exit..."); 
                            System.out.println("Closing this connection."); 
                            s.close(); 
                            System.out.println("Connection closed !"); 
                            stop = true;
                            appendStrToFile(filePath, "Client request status (" + new Date() + ") : " + received + " => " + "Success" );
                            break;
                        }
                    default:
                        {
                            dos.writeUTF("Invalid ");
                            appendStrToFile(filePath, "Client request status (" + new Date() + ") : " + received + " => " + "Failed" ); 
                            break;
                        }
                }
            } 
            this.dis.close(); 
            this.dos.close();
            appendStrToFile(filePath, "Client connection closed (" + new Date() + ") : " + s );
        }catch(Exception e){ 
            System.out.println(e);
        } 
    } 
} 
