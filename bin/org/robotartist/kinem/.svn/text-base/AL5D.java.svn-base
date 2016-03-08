package org.robotartist.kinem;

import org.opencv.core.Point3;
 

public class AL5D implements Kinematics {

	private static final double a2=14.61; //distance shoulder to elbow
	private static final double a3=18.73; //distance elbow to wrist
	//private static final double a4=3.2; //distance wrist to ikea pencil
	private static final double a4=3.2;
  
	
	public static void main(String[] args) {
		AL5D a=new AL5D();
//		double[] array = {Math.PI/2.0, Math.PI/2.0, Math.PI/2.0, 0.0};
//		Point3 p = a.direct(array);
//		Point3 p2 = a.direct2(array);
		Point3 p=new Point3();
		p.x=0;
	    p.y=15.0;
		p.z=5.0;
		
		//System.out.println(degree2us(90.0, 550, 2400));
		
		System.out.println(p.x + " " + p.y + " " + p.z);
		double[] joints=a.inverse(p);
		//double[] joints2=a.inverse(p);
		
		double basePulse = (Math.toDegrees(joints[0]) + 57.53) / 0.0973;
		double shoulderPulse = (Math.toDegrees(joints[1]) + 74.95) / 0.1136;
		double elbowPulse = (Math.toDegrees(joints[2]) + 72.23) / 0.1072;
		double wristPulse = (Math.toDegrees(joints[3]) + 49.96) / 0.09315;
		
		System.out.println( Math.toDegrees(joints[0]) + " " + Math.toDegrees(joints[1]) + " " + Math.toDegrees(joints[2]) + " " + Math.toDegrees(joints[3]));
		//System.out.println( Math.toDegrees(joints2[0]) + " " + Math.toDegrees(joints2[1]) + " " + Math.toDegrees(joints2[2]) + " " + Math.toDegrees(joints2[3]));
		//System.out.println( degree2us(Math.toDegrees(joints[0]), 600, 2400) + " " + degree2us(Math.toDegrees(joints[1]), 650, 2260) + " " + degree2us(Math.toDegrees(joints[2]), 640, 2440) + " " + degree2us(Math.toDegrees(joints[3]), 600, 2400));
		System.out.println( "P C " + Math.round(basePulse) + " " + Math.round(shoulderPulse) + " " + Math.round(elbowPulse) + " " + Math.round(wristPulse) + " \\n");
		
	}
	
	/* Mappa gradi nell'intervallo 0-180 in microsecondi nell'intervallo 600-2400 */
	public static int degree2us(double a, int min, int max){
		double result = min+(max-min)/180.0*a;
		return (int)Math.round(result);
	}
	
	@Override
	public Point3 direct(double[] joints) {
		
		double c1=Math.cos(joints[0]);
		double s1=Math.sin(joints[0]);
		double c2=Math.cos(joints[1]);
		double s2=Math.sin(joints[1]);
		double c3=Math.cos(joints[2]);
		double s3=Math.sin(joints[2]);
		double c4=Math.cos(joints[3]);   //rotazione 90 gradi per rispettare verso del giunto
		double s4=Math.sin(joints[3]);   //rotazione 90 gradi per rispettare verso del giunto


		Point3 xyz= new Point3(   a2*c1*c2 - a4*c4*(c1*c2*s3 - c1*c3*s2) + a4*s4*(c1*c2*c3 + c1*s2*s3) + a3*c1*s2*s3 + a3*c1*c2*c3,
								  a2*c2*s1 - a4*c4*(c2*s1*s3 - c3*s1*s2) + a4*s4*(c2*c3*s1 + s1*s2*s3) + a3*s1*s2*s3 + a3*c2*c3*s1,
								  a2*s2 - a3*c2*s3 + a3*c3*s2 - a4*c4*(c2*c3 + s2*s3) - a4*s4*(c2*s3 - c3*s2));
	
		return xyz;
	}
	
	public Point3 direct2(double[] joints) {
		double d1 = a2*Math.cos(joints[1]);
		double d2 = a3*Math.cos(joints[1] - joints[2]);
		double h = a2*Math.sin(joints[1]) + a3*Math.sin(joints[1] - joints[2]) - a4;
		double r = d1 + d2;
		Point3 xyz = new Point3(r*Math.cos(joints[0]), r*Math.sin(joints[0]), h/*+H_BASE*/);
		
		return xyz;
	}

	@Override
	public double[] inverse(Point3 xyz) {
		
		double s234=0;
		double c234=1;
		//System.out.println(xyz.x + " " + xyz.y);
		double teta1=Math.atan2(xyz.y, xyz.x);
		double c1=Math.cos(teta1);
		double s1=Math.sin(teta1);

		double c3=(Math.pow((xyz.x*c1 + xyz.y*s1 - a4*s234) , 2) + Math.pow(xyz.z + c234*a4, 2) - Math.pow(a2, 2) - Math.pow(a3, 2))/ (2*a2*a3);
		  
		   if(c3>1.0)  c3=1.0;
		   if(c3<-1.0)  c3=-1.0;

		double s3=Math.sqrt(1-Math.pow(c3, 2));
		double teta3=Math.atan2(s3,c3);
		double c2=(a3*s3*(-xyz.z - a4*c234) + (a2+a3*c3)*(xyz.x*c1+xyz.y*s1 - a4*s234))/ (2*a2*c3*a3 + Math.pow(a2,2)+Math.pow(a3,2));
		double s2=(-a2*c2 - c2*a3*c3 + (xyz.x *c1 + xyz.y*s1-a4*s234)) / (a3*s3);
		double teta2=Math.atan2(s2,c2);
		
		double teta4=teta3-teta2;
        double[] joints={teta1,teta2,teta3,teta4};
		
        return joints;
	}
	
	
}
