package com.opencvcamera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class Cmaera_class{
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Ouvrir la caméra
        VideoCapture capture = new VideoCapture(0); // Utilisez 0 pour la caméra par défaut, 1 pour la deuxième caméra, etc.

        if (!capture.isOpened()) {
            System.out.println("Impossible d'ouvrir la caméra.");
            return;
        }

        JFrame frame = new JFrame("Camera Capture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);

        JLabel label = new JLabel();
        frame.add(label);
        frame.setVisible(true);

        Mat mat = new Mat();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choisissez le dossier de destination");

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'e' || e.getKeyChar() == 'E') {
                    int result = fileChooser.showSaveDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();

                        // Prendre une capture d'écran et l'enregistrer
                        capture.read(mat);

                        if (!mat.empty()) {
                            ImageIcon image = new ImageIcon(Mat2BufferedImage(mat));
                            label.setIcon(image);
                            frame.repaint();

                            // Enregistrer l'image dans le dossier spécifié avec le nom choisi
                            File outputImage = new File(selectedFile.getAbsolutePath() + ".png");

                            try {
                                ImageIO.write(Mat2BufferedImage(mat), "png", outputImage);
                                System.out.println("Capture d'écran enregistrée : " + outputImage.getAbsolutePath());
                            } catch (IOException ex) {
                                System.err.println("Erreur lors de l'enregistrement de la capture d'écran.");
                                ex.printStackTrace();
                            }
                        } else {
                            System.out.println("Aucun cadre capturé.");
                        }
                    }
                }
            }
        });

        while (true) {
            capture.read(mat);

            if (!mat.empty()) {
                ImageIcon image = new ImageIcon(Mat2BufferedImage(mat));
                label.setIcon(image);
                frame.repaint();
            } else {
                System.out.println("Aucun cadre capturé.");
            }

            // Ajoutez un délai pour ajuster la fréquence d'images
            try {
                Thread.sleep(100); // 100 millisecondes entre chaque image
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static BufferedImage Mat2BufferedImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();
        byte[] source = new byte[width * height * channels];
        mat.get(0, 0, source);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] target = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(source, 0, target, 0, source.length);

        return image;
    }
}

