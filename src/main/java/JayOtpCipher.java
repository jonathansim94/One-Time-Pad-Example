import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class JayOtpCipher extends JPanel {

    private String imagePath;
    private BufferedImage bufferedImage;
    private int[][] imageMatrix;
    private int[][] keyMatrix;

    private static final int RED_FACTOR = 65536;
    private boolean filterOn;
    private boolean isShuffled;

    public JayOtpCipher(String imagePath) {
        try {
            this.imagePath = imagePath;
            if (imagePath != null) {
                this.bufferedImage = ImageIO.read(new File(imagePath));
                this.setImageMatrix();
                this.setSize(this.bufferedImage.getWidth(), this.bufferedImage.getHeight());
                this.addKeyListener(new JayKeyListener());

                this.generateOtpKey(true);
                this.filterOn = false;
                this.isShuffled = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getBufferedImage() {
        return this.bufferedImage;
    }

    public int[][] getImageMatrix() {
        return this.imageMatrix;
    }

    public void setBufferedImage(BufferedImage xorResultImage) {
        this.bufferedImage = xorResultImage;
    }

    private void generateOtpKey(boolean generateFile) {
        Random random = new Random();
        this.keyMatrix = new int[this.imageMatrix.length][this.imageMatrix[0].length];

        for (int row = 0; row < this.keyMatrix.length; row++) {
            for (int col = 0; col < this.keyMatrix[row].length; col++) {
                this.keyMatrix[row][col] = random.nextInt();
            }
        }

        if (generateFile)
            this.createKeyFile();
    }

    public void toggleOtpEncryption() {
        for (int row = 0; row < this.keyMatrix.length; row++) {
            for (int col = 0; col < this.keyMatrix[row].length; col++) {
                this.imageMatrix[row][col] = this.imageMatrix[row][col] ^ this.keyMatrix[row][col];
                Color newColor = new Color(this.imageMatrix[row][col], true);
                this.bufferedImage.setRGB(col, row, newColor.getRGB());
            }
        }
        this.createImageFile(this.bufferedImage);
    }

    public BufferedImage xorImages(int[][] firstImageMatrix, int[][] secondImageMatrix, String xorResultImageFilePath) {
        BufferedImage xorResultBufferedImage = new BufferedImage(firstImageMatrix.length, firstImageMatrix.length, 5);
        int[][] xorMatrixResult = new int[firstImageMatrix.length][firstImageMatrix.length];

        for (int row = 0; row < firstImageMatrix.length; row++) {
            for (int col = 0; col < firstImageMatrix[row].length; col++) {
                xorMatrixResult[row][col] = firstImageMatrix[row][col] ^ secondImageMatrix[row][col];
                Color newColor = new Color(xorMatrixResult[row][col], true);
                xorResultBufferedImage.setRGB(col, row, newColor.getRGB());
            }
        }
        this.generateXorFile(xorResultBufferedImage, xorResultImageFilePath);

        return xorResultBufferedImage;
    }

    public void toggleShuffleEffect() {
        int[][] imageMatrixCopy = Arrays.stream(this.imageMatrix).map(int[]::clone).toArray(int[][]::new);

        if (!this.isShuffled) {
            Random random = new Random();
            for (int i = imageMatrixCopy.length - 1; i > 0; i--) {
                for (int j = imageMatrixCopy[i].length - 1; j > 0; j--) {
                    int m = random.nextInt(i + 1);
                    int n = random.nextInt(j + 1);

                    int temp = imageMatrixCopy[i][j];
                    imageMatrixCopy[i][j] = imageMatrixCopy[m][n];
                    imageMatrixCopy[m][n] = temp;
                }
            }
            this.isShuffled = true;
        } else {
            this.isShuffled = false;
        }

        for (int i = 0; i < imageMatrixCopy.length; i++) {
            for (int j = 0; j < imageMatrixCopy.length; j++) {
                int a = imageMatrixCopy[i][j];
                Color newColor = new Color(a, true);
                this.bufferedImage.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public void toggleFilter(int filterCode) {
        for (int i = 0; i < this.imageMatrix.length; i++) {
            for (int j = 0; j < this.imageMatrix.length; j++) {

                int a;
                if (this.filterOn)
                    a = this.imageMatrix[i][j];
                else
                    a = this.imageMatrix[i][j] * filterCode;

                Color newColor = new Color(a, true);
                this.bufferedImage.setRGB(j, i, newColor.getRGB());
            }
        }
        this.filterOn = !this.filterOn;
    }

    private void setImageMatrix() {
        int originalImageWidth = this.bufferedImage.getWidth();
        int originalImageHeight = this.bufferedImage.getHeight();
        this.imageMatrix = new int[originalImageWidth][originalImageHeight];

        for (int x = 0; x < originalImageWidth; x++) {
            for (int y = 0; y < originalImageHeight; y++) {
                this.imageMatrix[y][x] = this.bufferedImage.getRGB(x, y);
            }
        }
    }

    private void createImageFile(BufferedImage image) {
        try {
            File output = new File(this.imagePath + "-cipher.jpg");
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateXorFile(BufferedImage image, String xorResultImageFilePath) {
        try {
            File output = new File(xorResultImageFilePath + "XOR.jpg");
            ImageIO.write(image, "jpg", output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createKeyFile() {
        try {
            File keyFile = new File(JayOtp.workDir + "key.txt");
            if (keyFile.createNewFile()) {
                try {
                    FileWriter myWriter = new FileWriter(JayOtp.workDir + "key.txt");

                    for (int row = 0; row < this.keyMatrix.length; row++) {
                        for (int col = 0; col < this.keyMatrix[row].length; col++) {
                            myWriter.write(this.keyMatrix[row][col] + "\n");
                        }
                    }
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Scanner myReader = new Scanner(keyFile);
                for (int row = 0; row < this.keyMatrix.length; row++) {
                    for (int col = 0; col < this.keyMatrix[row].length; col++) {
                        this.keyMatrix[row][col] = Integer.parseInt(myReader.nextLine());
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.bufferedImage, 0, 0, null);
    }

    private class JayKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_E:
                    toggleOtpEncryption();
                    break;
                case KeyEvent.VK_K:
                    generateOtpKey(false);
                    break;
                case KeyEvent.VK_F:
                    toggleFilter(RED_FACTOR);
                    break;
                case KeyEvent.VK_S:
                    toggleShuffleEffect();
                    break;
                default:
                    break;
            }
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}

