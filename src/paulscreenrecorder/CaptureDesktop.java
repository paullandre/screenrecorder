package paulscreenrecorder;

import com.teamdev.jxcapture.*;
import com.teamdev.jxcapture.image.ImageFormat;

import java.awt.*;
import java.io.File;

/**
 * The sample demonstrates capturing of a current desktop.
 * <pre>
 * Platforms:           All
 * Capture area:        Desktop
 * Output image format: PNG
 * Output file:         Desktop.png
 *
 * @author Ikryanov Vladimir
 * @author Sergei Piletsky
 */
public class CaptureDesktop
{
    public static void main(String[] args) throws Exception
    {
        int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int xxx = 0;
        ImageCapture imageCapture = ImageCapture.create(new CaptureArea.Desktop());
        long before = System.currentTimeMillis();
        while  (xxx < 100)
        {
            imageCapture.takeSnapshot()
                        .resize(new Dimension(screenWidth, screenHeight), InterpolationMode.HighQualityBicubic)
                        .save(new File("C:\\Users\\Xuan Xin Orthodox\\Desktop\\Images\\Imagescaptured" + xxx + ".png"), ImageFormat.PNG);
            xxx++;
            Thread.sleep(100);
        }
        long after = System.currentTimeMillis();
        imageCapture.release();
        System.out.println("Operation took " + (after - before) + " milliseconds.");

    }
}