package paulscreenrecorder;

import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

public class Recorder extends Thread
{

    private volatile boolean running;
    private volatile boolean requestStop;
    private volatile File video;
    private Logger logger;

    private String fileName;
    private int captureInterval;
    private BufferedImage pointerImg;

    public String users;
    public String ip;
    public String login;
    public String[] recipients;
    public String subject;

    public Recorder(String fileName, int captureInterval)
    {
        super("recorder_"+fileName);
        this.fileName = fileName;

        this.running = false;
        this.requestStop = false;

        this.captureInterval = captureInterval;

        logger = Logger.getLogger("ScreenRec");
        
        try
        {
            this.pointerImg = ImageIO.read(Provider.class.getResource("resources/mouse_pointer.png"));
        }
        catch(Exception ex)
        {
            logger.error(ex.getMessage(), ex.getCause());
        }
    }

    public void startRecorder()
    {
        this.start();
    }

    public void stopRecorder()
    {
        this.requestStop = true;
    }

    public boolean isRunning()
    {
        return this.running;
    }

    @Override public void run()
    {
        int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        
        boolean isTimeout = false;

        Robot rt;
        running = true;
        try
        {
            java.util.Date today = new java.util.Date();

            File f = new File("C:\\ScreenRec\\"+fileName);
            if(!f.exists())
            {
                try
                {
                    f.mkdir();
                    logger.info("Folder " + fileName + " is created...");
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage() + " \n The creation of file folder " + fileName + " failed");
                }
            }

            rt = new Robot();

            ArrayList<String[]> imageList = new ArrayList<String[]>();

            long timestampNow = System.currentTimeMillis();
            int filenamed = 0;

            logger.info("\n" + new java.sql.Timestamp(today.getTime()) + "  Now Recording!!!");

            long lastCaptureTimeStamp = System.currentTimeMillis();

            while (!requestStop)
            {
                if((System.currentTimeMillis() - timestampNow) > 7200000)
                {
                    isTimeout = true;
                    break;
                }

                if((System.currentTimeMillis() - lastCaptureTimeStamp) >= captureInterval)
                {
                    filenamed++;

                    String[] imageMap = new String[2];
                    lastCaptureTimeStamp = System.currentTimeMillis();
                    imageMap[0] = String.valueOf(lastCaptureTimeStamp);
                    imageMap[1] = "image" + filenamed + ".jpg";

                    imageList.add(imageMap);

                    BufferedImage img = rt.createScreenCapture(new Rectangle(screenWidth,screenHeight));                    

                    //For Mouse
                    Point pointer = null;
                    int pointX = 0;
                    int pointY = 0;

                    pointer = MouseInfo.getPointerInfo().getLocation();
                    pointX = (int)pointer.getX();
                    pointY = (int)pointer.getY();                                        
                    Graphics2D g = img.createGraphics();
                    try
                    {
                        g.drawImage(pointerImg, pointX, pointY, null);
                    }
                    catch(Exception ex)
                    {
                        logger.error(ex.getMessage(), ex.getCause());
                    }

                    ImageIO.write(img, "jpg", new File("C:\\ScreenRec\\"+fileName+"\\" + "image" + filenamed + ".jpg"));
                    //ImageIO.write(img, "jpg", new File("./"+fileName+"/" + "image" + filenamed + ".jpg"));
//                    lastCaptureTimeStamp = System.currentTimeMillis();
                }
                Thread.sleep(1);
            }

//            long recordTime = (System.currentTimeMillis() - timestampNow) / 1000;

            if(isTimeout)
            {
                removeDirectory(new File("C:\\ScreenRec\\"+fileName));
            }
            else
            {   
                video = new File("C:\\ScreenRec\\"+fileName+"\\Imagemap.txt");
                if(!video.exists())
                {
                    video.createNewFile();
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(video));
                for(String[] imageMap : imageList)
                {
                    String line = imageMap[0] + "=" + imageMap[1];                   
                    writer.write(line);
                    writer.newLine();
                    logger.info(line);
                }                
                writer.close();
               
                fixImageFrames(imageList);

                // Check if creation of screen shots and filename of folder is existing
                String folder = "C:\\ScreenRec\\"+fileName;
                try
                {
                    GetFiles size = new GetFiles();
                    long fileSizeByte = size.getFileSize(new File(folder));
                    long numFiles = size.getTotalFile();
                    long fileSize = fileSizeByte/1000000;
                    logger.info("Folder Name: "+ fileName);
                    logger.info("Folder Size: "+fileSizeByte/1000000+" MegaBytes" );
                    logger.info("Total Number of Folders: "+size.getTotalFolder());
                    logger.info("Total Number of Files: "+size.getTotalFile());
                    
                    subject = "";

                    if(fileSizeByte == 0)
                    {
                        subject = "No images saved";
                    }
                    if(fileName.equals(""))
                    {
                        subject = "Folder " + fileName + " not existing...";
                    }

                    logger.info("SUBJECT IS "+subject);
                    if(fileSizeByte == 0 || fileName.equals(""))
                    {
                        Mailer email_me = new Mailer();
                        try
                        {
                            InetAddress i = java.net.InetAddress.getLocalHost();
                            users = System.getProperty("user.name");
                            ip = i.getHostAddress();
                            login = i.getHostName();

                            java.util.Date dateToday = new java.util.Date();
                            String message = new java.sql.Timestamp(today.getTime()).toString() + " Connection received from 10.100.8.50";
                            //String[] recipient = {"paul.francisco@manila.concentrix.com", "antonio.andes@manila.concentrix.com","helpdesk_ph@concentrix.com"};
                            String[] recipient = {"paul.francisco@manila.concentrix.com"};
                            //String[] recipient = {"paul.francisco@manila.concentrix.com","paul.francisco@manila.concentrix.com"};
                            recipients = recipient;
                            //email.postMail(recipeints, videoPath, videoPath, videoPath, videoPath);
                            email_me.SendEmail(recipients,
                                               "Screen Recorder Error Message",
                                               subject, "paul.francisco@manila.link2support.com","damang08");
                            
                            Screen_Rec_Logs screen_rec_log  = new Screen_Rec_Logs();
                            screen_rec_log.imageCreationFailed(fileName);
                        }
                        catch (Exception email)
                        {
                            logger.error(email.getMessage());
                            logger.error("EMAIL Sending");
                        }
                        logger.error("FAILED CREATION: " + subject);
                    }
                    else
                    {
                        try
                        {
                            Screen_Rec_Logs screen_rec_log  = new Screen_Rec_Logs();
                            screen_rec_log.imageCreationSuccess(fileName, numFiles, fileSize);
                        } catch (Exception e)
                        {
                            logger.error(e.getMessage());
                            e.printStackTrace();
                            logger.error("Failed to insert in database");
                        }
                        logger.info("SUCCESSFUL CREATION OF FOLDER AND IMAGES:" + fileName);
                    }                   
                    //System.exit(0);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                
//            double fps = (double)filenamed / (double)recordTime;
                VideoCreator videoCreator = new VideoCreator(4, fileName, "", "");
                videoCreator.run();

                logger.info(new java.sql.Timestamp(today.getTime()) + "  Recording has been stopped.");
            }            
        }
        catch (Exception e)
        {
             logger.error(e.getMessage(), e.getCause());
        }

        running = false;
    }

    public boolean removeDirectory(File directory)
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

    public void fixImageFrames(ArrayList<String[]> imageList) {
        int ctr = 1;

        long firstSec = (Long.parseLong(imageList.get(0)[0])/1000)*1000;
        long prevSec = firstSec;

        int frameNum = 0;

        for(String[] file : imageList)
        {
            long newTimestamp = (Long.parseLong(file[0])/1000)*1000;

            File myFile = new File("C:\\ScreenRec\\"+fileName + "\\" + file[1]);

//            System.out.println(file[0] + " => " + file[1]);

            if(firstSec == newTimestamp)
            {
                try{
                    myFile.renameTo(new File("C:\\ScreenRec\\"+fileName +"\\img" + ctr + ".jpg"));

//                    System.out.println(file[1] + " => " + "img" + ctr + ".jpg");

                    ctr++;
                    frameNum++;
                }catch(Exception e){logger.error(e.getMessage(), e.getCause());}
            } else {

                if(prevSec != newTimestamp)
                {
                    long fillFrame = ((newTimestamp - prevSec - 1000) * 4) / 1000;
                        
                    if(frameNum < 4)
                    {
                        fillFrame += 4 - frameNum;
                    }

                    for(int i = 0; i < fillFrame; i++)
                    {
                        try{

                            BufferedImage img = ImageIO.read(myFile);
                            ImageIO.write(img, "jpg", new File("C:\\ScreenRec\\"+fileName + "\\img" + ctr + ".jpg"));

                            logger.info("Filler : " + file[1] + " => img" + ctr + ".jpg");

                            ctr++;
                        }catch(Exception e){logger.error(e.getMessage(), e.getCause());}
                    }

                    try{
                        myFile.renameTo(new File("C:\\ScreenRec\\"+fileName + "\\img" + ctr + ".jpg"));

//                        System.out.println(file[1] + " => " + "img" + ctr + ".jpg");

                        ctr++;
                    }catch(Exception e){logger.error(e.getMessage(), e.getCause());}

                    prevSec = newTimestamp;
                    frameNum = 1;
                } else {
                    if(frameNum > 4)
                    {
                        try{
                            myFile.delete();

                            logger.info("Excess : " + file[1]);
                        }catch(Exception e){logger.error(e.getMessage(), e.getCause());}
                    } else {
                        try{
                            myFile.renameTo(new File("C:\\ScreenRec\\"+fileName + "\\img" + ctr + ".jpg"));

//                            System.out.println(file[1] + " => " + "img" + ctr + ".jpg");

                            ctr++;
                            frameNum++;
                        }catch(Exception e){logger.error(e.getMessage(), e.getCause());}
                    }
                }
            }
        }
    }
}