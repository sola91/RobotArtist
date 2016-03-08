package main;

import java.util.Enumeration;

import gnu.io.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.robotartist.util.OpencvNativeLibs;

public class EnumPorts {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        }

//		for (OpencvNativeLibs lib : OpencvNativeLibs.values()) {
//			try {
//				System.loadLibrary(lib.getName());
//			} catch (UnsatisfiedLinkError e) {
//				//e.printStackTrace();
//			}
//		}
//		
//	
//        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
//        System.out.println("m = " + m.dump());
	}
	
	static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
}
