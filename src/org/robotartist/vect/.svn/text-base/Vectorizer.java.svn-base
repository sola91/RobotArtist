package org.robotartist.vect;

import java.awt.Image;
import java.io.IOException;

import org.opencv.highgui.Highgui;
import org.opencv.core.Mat;
import org.robotartist.util.ImgHlpr;


public abstract class Vectorizer {
	private Mat img;

	
	public Vectorizer(String path) {
		this.img = Highgui.imread(path);
	}
	
	public Vectorizer(Mat img) {
		this.img = img;
	}
	
	public Vectorizer(Image img) throws IOException {
		this.img = ImgHlpr.imageToMat(img);
	}
	
	public abstract void vectorize() throws IOException;
	
	/*getters and setters*/
	public Mat getImg() {
		return img;
	}

	public void setImg(Mat img) {
		this.img = img;
	}

}
