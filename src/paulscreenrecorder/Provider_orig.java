package paulscreenrecorder;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.media.MediaLocator;

public class Provider_orig
{
    /**
     * Screen Width.
     */
    public static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

    /**
     * Screen Height.
     */
    public static int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    /**
     * Interval between which the image needs to be captured.
     */
    public static int captureInterval = 30;

    /**
     * Temporary folder to store the screenshot.
     */
    public static String store = "tmp";

    /**
     * Status of the recorder.
     */
    public static boolean record = false;
    
    ServerSocket providerSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    Provider_orig()
    {

    }
    void run()
    {
        try
        {
            providerSocket = new ServerSocket(2004);
            do
            {
                try
                {
                    System.out.println("Waiting for connection...");
                    connection = providerSocket.accept();
                    System.out.println("Connection received from " + connection.getInetAddress().getHostName());
                    in = new ObjectInputStream(connection.getInputStream());
                    message = (String)in.readObject();

                    String[] params = message.split(",");
                    String recordTitle = params[1];

                    System.out.println("client>" + message);
                    if (params[0].equals("Start"))
                    {
                        File f = new File(store);
                        if(!f.exists())
                        {
                           f.mkdir();
                        }

                        startRecord();
                        System.out.println("\nNow Recording!!!");
                    }
                    else if (params[0].equals("Stop"))
                    {
                        record = false;
                        System.out.println("Recording has been stopped.");
                        makeVideo(recordTitle+".avi");

                        File f = new File(store);
                        removeDirectory(f);
                    }
                }
                catch(ClassNotFoundException classnot)
                {
                    System.err.println("Data received in unknown format");
                }
            }
            while(true);                    
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace();
        }
        finally
        {            
            try
            {
                in.close();
                providerSocket.close();
            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
    }
    void sendMessage(String msg)
    {
        try
        {
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace();
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

    public static boolean removeDirectory(File directory)
    {
        if (directory == null) return false;
        if (!directory.exists()) return true;
        if (!directory.isDirectory()) return false;

        String[] list = directory.list();
        int ifiles = list.length;

        if (list != null || ifiles == 1)
        {
            for (int i = 0; i < list.length; i++)
            {
                File entry = new File(directory, list[i]);

//                System.out.println(list[i]);
                if (entry.isDirectory())
                {
                    if (!removeDirectory(entry)) return false;
                }
                else
                {
                    if (!entry.delete()) return false;
                }
            }
        }
        return directory.delete();
    }

    public static void makeVideo(String movFile) throws MalformedURLException
    {
        System.out.println("#### Converting Images to Video, please wait!!! ####");
        JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
        Vector<String> imgLst = new Vector<String>();
        File f = new File(store);
        File[] fileLst = f.listFiles();
        for (int i = 0; i < fileLst.length; i++)
        {
            imgLst.add(fileLst[i].getAbsolutePath());
        }
        // Generate the output media locators.
        MediaLocator oml;
        if ((oml = imageToMovie.createMediaLocator(movFile)) == null)
        {
            System.err.println("Cannot build media locator from: " + movFile);
            System.exit(0);
        }
//        imageToMovie.doIt(screenWidth, screenHeight, (1000 / captureInterval),
//                        imgLst, oml);
        imageToMovie.doIt(screenWidth, screenHeight, (3),imgLst, oml);
    }

    public static void startRecord()
    {
        Thread recordThread = new Thread()
        {
            @Override
            public void run()
            {
                Robot rt;
                int cnt = 0;
                try
                {
                    rt = new Robot();

                    long timestampNow = System.currentTimeMillis();
                    while (cnt == 0 || record)
                    {
                        if(System.currentTimeMillis() - timestampNow >= captureInterval)
                        {
                            BufferedImage img = rt.createScreenCapture(new Rectangle(screenWidth,screenHeight));
                            ImageIO.write(img, "jpg", new File("./"+store+"/"
                                            + System.currentTimeMillis() + ".jpg"));
                            timestampNow = System.currentTimeMillis();
                        }

                        if (cnt == 0)
                        {
                            record = true;
                            cnt = 1;
                        }
                        // System.out.println(record);
                        Thread.sleep(5);
                    }
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                }
            }
        };
        recordThread.start();
    }
}