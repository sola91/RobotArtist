package org.robotartist.vect;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core;
import org.robotartist.comm.ArduinoSerialIO;
import org.robotartist.comm.ArduinoSerialIO2;
import org.robotartist.kinem.AL5D;
import org.robotartist.kinem.Taylor;
import org.robotartist.util.ImgHlpr;
import org.robotartist.util.Loader;

public class CustomVectorizer extends Vectorizer {

	private Mat tmpImg = null;  
	private Mat edges = null;
	private List<List<Point>> vectors = null;

	private double highThresh = 74.0; //soglia alta per Canny
	private int ksize = 3; //dimensione del kernel per filtro gaussiano
	private double sigma = 2.0; //sigma per filtro gaussiano
	private int minPathSize = 10;

	private double dpThresh = 3.0; //soglia per algoritmo di Douglas-Peucker

	public CustomVectorizer(String path) {
		super(path);
	}

	public CustomVectorizer(Mat img) {
		super(img);
	}

	public CustomVectorizer(Image img) throws IOException {
		super(img);
	}
	
	/** 
	 * Vectorize the current image and put it in vectors
	 */
	@Override
	public void vectorize() throws IOException {
		//preprocess();
		List<Point> path = null;
		MatOfPoint2f approxPath = null;
		vectors = new ArrayList<List<Point>>();
		for (int i = 0; i < edges.rows(); i++) {
			for (int j = 0; j < edges.cols(); j++) {
				if (edges.get(i, j)[0] == 0.0) {
					path = new ArrayList<Point>();
					approxPath = new MatOfPoint2f();
					followPath(i, j, edges, path);
					if (path.size() >= minPathSize) {
					approxPath.fromList(path);
					Imgproc.approxPolyDP(approxPath, approxPath, dpThresh, false); //Douglas-Peucker
					vectors.add(new ArrayList<Point>(approxPath.toList()));
					}
				}
			}
		}
		Size s = new Size(tmpImg.height(), tmpImg.width());
		Mat show = new Mat(s, tmpImg.type(), new Scalar(255.0));
		for (int i = 0; i < vectors.size(); i++) {
			for (int k = 0; k < vectors.get(i).size() - 1; k++) {
				Point p1 = vectors.get(i).get(k);
				Point p2 = vectors.get(i).get(k+1);
				Core.line(show, p1, p2, new Scalar(0), 1);
				//show.put((int)p.x, (int)p.y, 255.0);
			}
		}
		Core.flip(show.t(), show, 1);
		//ImgHlpr.imShow(show);
	}
	
	/** 
	 * Metodo che filtra l'immagine originale per prepararla alla vettorizzazione:
	 * 1. conversione grayscale
	 * 2. filtraggio gaussiano
	 * 3. estrazione contorni con Canny
	 * 4. inversione dell'immagine
	 */
	public void preprocess() {
		tmpImg = super.getImg().clone();
		Imgproc.cvtColor(tmpImg, tmpImg, Imgproc.COLOR_RGB2GRAY);
		Imgproc.GaussianBlur(tmpImg, tmpImg, new Size(ksize, ksize), sigma);
		Imgproc.Canny(tmpImg, tmpImg, 0.4*highThresh,  highThresh); //bianco = 255.0, nero = 0.0
//		ImgHlpr.imShow(tmpImg);
		Core.subtract(new Mat(tmpImg.size(), tmpImg.type(), new Scalar(255)), tmpImg, tmpImg);
		setEdges(tmpImg.clone());
//		ImgHlpr.imShow(tmpImg);
	}
	
	/* Funzione ricorsiva che segue un percorso di pixel adiacenti
	 * @param x, y Il pixel di partenza
	 * @param bitmap L'immagine bitmap su cui lavorare
	 * @param list La lista di pixel adiacenti
	 */
	private void followPath(int x, int y, Mat bitmap, List<Point> list) {
		list.add(new Point(x, y));
		bitmap.put(x, y, 255.0);
		boolean found = false;
		for (int h = x-1; h <= x+1; h++) {
			for (int k = y-1; k <= y+1; k++) {
				if (h >= 0 && k >= 0 && h < bitmap.rows() && k < bitmap.cols() && bitmap.get(h, k)[0] == 0.0) {
					found = true;
					followPath(h, k, bitmap, list);
					break;
				}
			}
			if (found)
				break;
		}
	}
	
	public static void main(String args[]) throws Exception {
		double height = -3.4;
		Loader.loadOpenCV();
		CustomVectorizer v = new CustomVectorizer("img/omino-stilizzato1.jpg");
		v.preprocess();
		v.vectorize();
		List<List<Point>> vect = ImgHlpr.scaleVectors(v.getVectors(), 780, 581);
		Taylor t = new Taylor(new AL5D());
		List<List<Point3>> paths = new ArrayList<List<Point3>>();
//		for(List<Point> l : vect) {
//			List<Point3> path = new ArrayList<Point3>();
//			path.add(new Point3(l.get(0).x, l.get(0).y, height+7.0));
//			for(Point p : l) {
//				path.add(new Point3(p.x, p.y, height));
//			}
//			paths.add(path);
//			List<Point3> pickAndPlace = new ArrayList<Point3>();
//			pickAndPlace.add(new Point3(l.get(l.size()-1).x, l.get(l.size()-1).y, height));
//			pickAndPlace.add(new Point3(l.get(l.size()-1).x, l.get(l.size()-1).y, height+7.0));
//			paths.add(pickAndPlace);
//		}
		/*------Test--------*/
		double h = -3.9;
		for (int i = 0; i < 1; i++) {
			List<Point3> path = new ArrayList<Point3>();
			Point3 p1 = new Point3(5.0, 25.0, h);
			Point3 p2 = new Point3(5.0, 15.0, h);
			Point3 p3 = new Point3(-5.0, 15.0, h);
			Point3 p4 = new Point3(-5.0, 25.0, h);
			Point3 p5 = new Point3(5.0, 25.0, h);
			//Point3 p5 = new Point3(15, 5, 0);
			path.add(p1);path.add(p2);path.add(p3);path.add(p4);path.add(p5);
			paths.add(path);
		}
		/*--------------*/
		for(List<Point3> l : paths) {
			for (int i = 0; i < l.size() - 1; i++) {
				t.bdjp(l.subList(i, i+2), 0.01);
			}
		}
		System.out.println(paths);
		//ArduinoSerialIO as = new ArduinoSerialIO(CommPortIdentifier.getPortIdentifier("COM11"), 9600, path);
		ArduinoSerialIO2 as = new ArduinoSerialIO2("COM11");
		as.setPaths(paths);
		as.initialize();
		System.out.println("Started");
	}

	public Mat getEdges() {
		return edges;
	}
	
	public Mat getTmpImg(){
		
		return tmpImg;
	}

	public void setEdges(Mat edges) {
		this.edges = edges;
	}

	public List<List<Point>> getVectors() {
		return vectors;
	}

	public void setVectors(List<List<Point>> vectors) {
		this.vectors = vectors;
	}

	public void setHighTresh(double highThresh) {
		this.highThresh = highThresh;
	}

	public void setKsize(int ksize) {
		this.ksize = ksize;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	
	public void setDpThresh(double dpThresh) {
		this.dpThresh = dpThresh;
	}
	
	public void setMinPathSize(int minPathSize) {
		this.minPathSize = minPathSize;
	}

}
