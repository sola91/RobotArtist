package org.robotartist.kinem;

import java.util.List;

import org.opencv.core.Point3;

public class Taylor {
	private static Kinematics al5d = null;
	
	public Taylor(Kinematics k) {
		al5d = k;
	}
	
	/** Compute the Bounded Deviation Joints Path (Taylor algorithm)
	 * @param path List to work on. It must contains the start and end points in cartesian space
	 * @param threshold Maximum accepatable error
	 * @return The approximated list of cartesian coordinates
	 */
	public List<Point3> bdjp(List<Point3> path, final double threshold) {
		if (path.size() == 2)
			return bdjp(path, threshold, path.get(0), path.get(1));
		else 
			return null;
	}
	
	/* Compute the Bounded Deviation Joints Path (Taylor algorithm)
	 * @param path List to work on. It must contains the start and end points in cartesian space
	 * @param threshold Maximum accepatable error
	 * @param start Should correspond to the first element in path
	 * @param end Should correspond to the last element in path
	 * @return The approximated list of cartesian coordinates
	 */
	private static List<Point3> bdjp(List<Point3> path, final double threshold, Point3 start, Point3 end) {
		double[] startJ = al5d.inverse(start);
		double[] endJ = al5d.inverse(end);
		double[] midPoint = midPoint(startJ, endJ);
		Point3 midXYZ = al5d.direct(midPoint);
		if (error(start, end, midXYZ) < threshold || start.equals(end))
			return path;
		else {
			Point3 addMid = new Point3((start.x + end.x)/2.0, (start.y + end.y)/2.0, (start.z + end.z)/2.0);
			path.add(path.indexOf(end), addMid);
			bdjp(path, threshold, start, addMid);
			bdjp(path, threshold, addMid, end);
		}
		return null;
	}
	
	
	/* Ritorna la norma di un vettore rappresentato da un Point3 */
	private static double norm(Point3 p) {
		return Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
	}
	
	/*Ritorna il punto medio tra due giunti*/
	private static double[] midPoint(double[] start, double[] end) {
		if (start.length == end.length) {
			double[] midPoint = new double[start.length];
			for (int j=0; j<start.length; j++) {
				midPoint[j] = (start[j] + end[j]) / 2;
			}
			return midPoint;
		}
		else
			return null;
	}
	
	/* Calcola la distanza tra la retta passante per p1-p2 e il punto mid
	 * @param p1 A 3D (opencv) point
	 * @param p2 A 3D (opencv) point
	 * @param mid A 3D (opencv) point
	 * @return Errore di approssimazione per l'algoritmo di Taylor
	 */
	private static double error(Point3 p1, Point3 p2, Point3 mid) {
		Point3 ab = new Point3(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z); //p2 - p1
		Point3 ap = new Point3(mid.x - p1.x, mid.y - p1.y, mid.z - p1.z); //mid - p1
		Point3 parall = ab.cross(ap); // prodotto vettoriale
		double res = norm(parall) / norm(ab); //formula inversa area parallelogramma (per trovare altezza)
		return res;
	}
}
