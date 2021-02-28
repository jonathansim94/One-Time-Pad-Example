import javax.swing.*;
import java.awt.image.BufferedImage;

public class JayOtp {
    /**
     * Where you want to save the generated key, where you want the final xor file and where the images are
     */
    public static final String workDir = "C:\\Users\\jonat\\Desktop\\";
    /**
     * First image's path
     */
    private static String firstImageFileName = "1.jpg";
    /**
     * Second image's path
     */
    private static String secondImageFileName = "2.jpg";
    /**
     * At first running, set this to "false" and run. On each image frame press "E" to encrypt with same key and generate result files.
     * If you want to encrypt with different keys, encrypt the first one with "E", then press "K" before pressing "E" on the second image frame.
     * At second running, set this to "true" and the xor between encrypted images will show up and the result file will be generated.
     * FFF (FOR FUN FUNCTIONS) = press "S" to shuffle image pixels, or "F" to apply a red filter (images files will not be created)
     */
    private static final boolean isXorExecution = true;

    public static void main(String[] args) {
        JFrame firstImageFrame = new JFrame();
        JFrame secondImageFrame = new JFrame();

        firstImageFrame.setResizable(false);
        firstImageFrame.setVisible(true);
        firstImageFrame.setTitle("First Image");
        firstImageFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        secondImageFrame.setResizable(false);
        secondImageFrame.setVisible(true);
        secondImageFrame.setTitle("Second Image");
        secondImageFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        if (isXorExecution) {
            firstImageFileName = firstImageFileName + "-cipher.jpg";
            secondImageFileName = secondImageFileName + "-cipher.jpg";
        }

        JayOtpCipher firstImageCypher = new JayOtpCipher(workDir + firstImageFileName);
        JayOtpCipher secondImageCypher = new JayOtpCipher(workDir + secondImageFileName);

        BufferedImage firstLoadedImage = firstImageCypher.getBufferedImage();
        firstImageFrame.setSize(firstLoadedImage.getWidth(), firstLoadedImage.getHeight());
        firstImageFrame.setLocationRelativeTo(null);
        firstImageFrame.getContentPane().add(firstImageCypher);
        firstImageCypher.requestFocusInWindow();
        firstImageCypher.repaint();

        BufferedImage secondLoadedImage = firstImageCypher.getBufferedImage();
        secondImageFrame.setSize(secondLoadedImage.getWidth(), secondLoadedImage.getHeight());
        secondImageFrame.setLocationRelativeTo(null);
        secondImageFrame.getContentPane().add(secondImageCypher);
        secondImageCypher.requestFocusInWindow();
        secondImageCypher.repaint();

        if (isXorExecution) {

            firstImageFrame.setVisible(false);
            secondImageFrame.setVisible(false);

            JFrame xorFrame = new JFrame();

            xorFrame.setResizable(false);
            xorFrame.setVisible(true);
            xorFrame.setTitle("XOR result");
            xorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JayOtpCipher xorResult = new JayOtpCipher(null);
            xorFrame.setSize(firstLoadedImage.getWidth(), firstLoadedImage.getHeight());
            xorFrame.setLocationRelativeTo(null);
            xorFrame.getContentPane().add(xorResult);

            BufferedImage xorResultImage = xorResult.xorImages(firstImageCypher.getImageMatrix(), secondImageCypher.getImageMatrix(), workDir);
            xorResult.setBufferedImage(xorResultImage);

            xorResult.requestFocusInWindow();
            xorResult.repaint();

        }


    }

}
