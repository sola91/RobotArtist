package org.robotartist.kinem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.opencv.core.Point3;

public class Trajectory {
	private List<Point3> path = null;
	private List<PolynomialSplineFunction> trajectories = new ArrayList<PolynomialSplineFunction>();
	private List<List<Double>> Q = new ArrayList<List<Double>>();
	private List<List<Double>> T = new ArrayList<List<Double>>();
	private List<List<Double>> V = new ArrayList<List<Double>>();
	private double vMax = 10.0; // gradi al secondo
	private int nJoints = 4;
	
	/**
	 * Inizializza un oggetto Trajectory 
	 * 
	 * @param k Oggetto per i calcoli di cinematica
	 * @param path Percorso nel piano cartesiano da trasformare in traiettoria
	 */
	public Trajectory(Kinematics k, List<Point3> path) {
		this.path = path;
		for (int j = 0; j < nJoints; j++) {
			List<Double> t = new ArrayList<Double>();
			t.add(0.0);
			List<Double> v = new ArrayList<Double>();
			v.add(0.0);
			List<Double> pathJ = new ArrayList<Double>(); //path di un singolo giunto
			for (int i = 0; i < path.size(); i++) {
				pathJ.add(Math.toDegrees(k.inverse(path.get(i))[j]));
			}
			Q.add(pathJ);
			T.add(t);
			V.add(v);
		}
	}
	
	/**
	 * Genera le traiettorie dei giunti relative a uno specifico path
	 * da settare al momento dell'inizializzazione della classe
	 * @return Le traiettorie continue nelle velocita'
	 */
	public List<PolynomialSplineFunction> generate() {
		List<PolynomialFunction[]> pfs = new ArrayList<PolynomialFunction[]>();
		for (int n = 0; n < nJoints; n++)
			pfs.add(new PolynomialFunction[this.path.size()-1]);
		/*----init V and T----*/
		for (int i = 0; i < this.path.size() - 1; i++) {
			double dist = maxDist(i);
			double T = dist / vMax;
			for (int j = 0; j < nJoints; j++) {
				if (i == this.path.size() - 2) {
						this.V.get(j).add(0.0);
						this.T.get(j).add(T + this.T.get(j).get(i));
				}
				else {
						this.T.get(j).add(T + this.T.get(j).get(i));
						this.V.get(j).add((Q.get(j).get(i+1) - Q.get(j).get(i)) / (this.T.get(j).get(i+1) - this.T.get(j).get(i)));
				}
			}	
		}
		/*--------------*/
		for (int i = 0; i < this.path.size() - 1; i++) {
			for (int j = 0; j < nJoints; j++) {
				double deltaT = this.T.get(j).get(i+1) - this.T.get(j).get(i);
				double[] coeff = new double[4];
				coeff[0] = Q.get(j).get(i);
				double qstartPrime = ( /*i+1 == Q.size()-1 ||*/ Math.signum(this.V.get(j).get(i)) != Math.signum(this.V.get(j).get(i+1)) ) ? 0 
						: (this.V.get(j).get(i) + this.V.get(j).get(i+1)) / 2.0;
				coeff[1] = qstartPrime;
				double qendprime = ( i == this.path.size()-2 || Math.signum(this.V.get(j).get(i+1)) != Math.signum(this.V.get(j).get(i+2)) ) ? 0 
						: (this.V.get(j).get(i) + this.V.get(j).get(i+1)) / 2.0;
				coeff[2] = (-3*(Q.get(j).get(i) - Q.get(j).get(i+1)) - (2*qstartPrime + qendprime)*deltaT) / (deltaT*deltaT);
				coeff[3] = (2*(Q.get(j).get(i) - Q.get(j).get(i+1)) + (qstartPrime + qendprime)*deltaT) / (deltaT*deltaT*deltaT);
				PolynomialFunction f = new PolynomialFunction(coeff);
				pfs.get(j)[i] = f;
			}
		}
		for (int i = 0; i < nJoints; i++) {
			double knots[] = new double[this.path.size()];
			for (int j = 0; j < path.size(); j++)
				knots[j] = T.get(i).get(j);
			PolynomialSplineFunction psf = new PolynomialSplineFunction(knots, pfs.get(i));
			trajectories.add(psf);
		}
		
		return trajectories;
	}
	
	/*
	 * Ritorna la distanza massima che deve compiere uno dei 
	 * giunti del manipolatore
	 */
	private double maxDist(int index) {
		List<Double> tmp = new ArrayList<Double>();
		for (int i = 0; i < nJoints; i++) {
			tmp.add(Math.abs(Q.get(i).get(index) - Q.get(i).get(index+1)));
		}
		return Collections.max(tmp);
	}
	
	public double getMaxT(int joint) {
		return T.get(joint).get(T.get(joint).size()-1);
	}
	
	public List<List<Double>> getT() {
		return T;
	}

	public static void main(String[] args) {
		List<Point3> path = new ArrayList<Point3>();
		Point3 p1 = new Point3(7, 7, 0);
		Point3 p2 = new Point3(10, 11, 0);
		Point3 p3 = new Point3(8, 5, 0);
		path.add(p1);
		path.add(p2);
		path.add(p3);
		Trajectory t = new Trajectory(new AL5D(), path);
		List<PolynomialSplineFunction> p = t.generate();
		System.out.println(p.get(0).value(0.47));
	}
}
