package org.robotartist.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class ImgHlpr extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public static void imShow(Mat img) {
		Image tmp = matToImage(img);
		imShow(tmp);
	}
	
	public static void imShow(Image img) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(new ImageViewer(img));
		frame.setVisible(true);
	}
	
	public static BufferedImage matToImage(Mat img) {
		Mat image_tmp = img;

		MatOfByte matOfByte = new MatOfByte();

		Highgui.imencode(".png", image_tmp, matOfByte); 

		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;

		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufImage;
	}
	
	public static Mat imageToMat(Image img) {
		try {
			File tmp = new File("tmp");
			ImageIO.write((BufferedImage)img, "PNG", tmp);
			Mat converted = Highgui.imread("tmp");
			tmp.delete();
			return converted;
		}
		catch (ClassCastException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * Metodo che permette di scalare un'immagine vettoriale in modo da 
	 * poterla disegnare.
	 * @param vectors L'immagine vettoriale da riscalare
	 * @param width Larghezza originale dell'immagine
	 * @param height Altezza originale dell'immagine
	 * @return Immagine vettoriale riscalata
	 */
	public static List<List<Point>> scaleVectors(List<List<Point>> vectors, int width, int height) {
		ArrayList<List<Point>> scaled = new ArrayList<List<Point>>();
		for (List<Point> l : vectors) {
			List<Point> path = new ArrayList<Point>();
			for (Point p : l) {
				path.add(new Point((p.x/width)*10.0 - 9.0, (p.y/height)*10.0 + 15.0));
			}
			scaled.add(path);
		}
		System.out.println(scaled);
		return scaled;
	}
	
	/**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in down-scaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@codey BufferedImage}
     */
    public static BufferedImage getScaledInstance(BufferedImage img,
                                                  int targetWidth,
                                                  int targetHeight,
                                                  Object hint,
                                                  boolean higherQuality) {
    	int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
	
	public static void main(String args[]) throws IOException {
		Loader.loadOpenCV();
		//Image test = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Marco\\Desktop\\Ischia\\DSCN7304.JPG").
		//		getScaledInstance(800, 600, Image.SCALE_SMOOTH);
		BufferedImage test = ImageIO.read(new File("C:\\Users\\Marco\\Desktop\\Ischia\\DSCN7304.JPG"));
		test = getScaledInstance(test, 800, 600, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, true);
		Mat testMat = Highgui.imread("C:\\Users\\Marco\\Desktop\\Ischia\\DSCN7304.JPG");
		Imgproc.resize(testMat, testMat, new Size(800, 600), 0, 0, Imgproc.INTER_AREA);
		imShow(testMat);
		imShow(test);
		imShow(imageToMat(test));
	}
	
}
