package paulscreenrecorder;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.sql.*;
import java.net.InetAddress;

public class Provider
{
    public volatile boolean record = false;
    public volatile String logTime;

    public static String fileNameAvi;
    public static String fileNameMpeg;
    public static String videoPath;
    public static String[] params;

    //public volatile String logTime;
    
    ServerSocket providerSocket;
    Socket connection = null;
    ObjectOutputStream out;

    String message;

    private Hashtable recorderHash;

    Provider()
    {
        recorderHash = new Hashtable();
    }
    
    void run()
    {
        try
        {
            Screen_Rec_Logs screenRecLogs = new Screen_Rec_Logs();
            screenRecLogs.insertRecordingLogs();
            
            providerSocket = new ServerSocket(2011);
            while(true)
            {
                try
                {                                        
                    java.util.Date today = new java.util.Date();                     
                    System.out.println(new java.sql.Timestamp(today.getTime()) + "  Waiting for connection...");
                    connection = providerSocket.accept();
                    System.out.println(new java.sql.Timestamp(today.getTime()) +  " Connection received from " + connection.getInetAddress().getHostName());

                    DataInputStream din = new DataInputStream(connection.getInputStream());
                    int read;

                    message = "";

                    while((read = din.read()) != -1)
                    {
                        message += (char)read;
                    }

                    params = message.split(",");

                    System.out.println(new java.sql.Timestamp(today.getTime()) + "  client>" + message);

                    din.close();
                    if (params[0].equals("START"))
                    {
                        if(!recorderHash.containsKey(params[1]))
                        {
                            screenRecLogs.insertRecordLogin(params[1]);
                            Recorder recorder = new Recorder(params[1], 250);
                            recorder.startRecorder();

                            recorderHash.put(params[1], recorder);
                        }
                    }
                    else if (params[0].equals("STOP"))
                    {
                        if(recorderHash.containsKey(params[1]))
                        {
                            screenRecLogs.insertRecordLogout(params[1]);
                            Recorder recorder = (Recorder)recorderHash.remove(params[1]);
                            recorder.stopRecorder();
                        }
                    }
                }
                catch(Exception classnot)
                {
                    System.err.println("Data received in unknown format");
                }
            }              
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace();
        }
        finally
        {            
            try
            {
                providerSocket.close();
            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String args[])
    {        
        Provider server = new Provider();
        while(true)
        {
            server.run();
        }
    }      
}