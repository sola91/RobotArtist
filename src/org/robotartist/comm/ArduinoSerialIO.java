package org.robotartist.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;

import org.opencv.core.Point3;
import org.robotartist.kinem.AL5D;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ArduinoSerialIO implements SerialPortEventListener {
	
	SerialPort serialPort;
	private String portName = null;
	private BufferedReader input;
	private OutputStream output;
	private AL5D al5d = new AL5D();
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	
	/** Save the taylor's paths*/
	private static List<List<Point3>> paths;
	private static int index_path = 0;
	private static int index_point = 0;
	
public ArduinoSerialIO(String port){
		
		portName=port;
		
	}

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
		
				if (currPortId.getName().equals(portName)) 
					portId = currPortId;
			
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
		
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
	
	public synchronized void setPaths(List<List<Point3>> paths){
		this.paths = paths;
	}

    
	/**
	 * Handle an event on the serial port.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		List<Point3> path;
		String command;
		//matrice parametri mq, modello lineare dei giunti
		double[][] mq = {
				{0.0983 , -59,76},{0.1136 , -74.95 },{0.105 , -69.6},{0.09315 , -49.96}
		} ;
		
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				/**leggo da seriale segnale inviato da arduino */
				int c=input.read();
				if((char)c=='O'){
					if(index_path<paths.size()){
						path = paths.get(index_path);
						if(!(index_point<path.size())){
							index_point=0;
							index_path++;
							path = paths.get(index_path);
						}
						double[] joints = al5d.inverse(new Point3(path.get(index_point).x, path.get(index_point).y, path.get(index_point).z));
						double basePulse = (Math.toDegrees(joints[0]) - mq[0][1]) / mq[0][0];
						double shoulderPulse = (Math.toDegrees(joints[1]) - mq[1][1]) / mq[1][0];
						double elbowPulse = (Math.toDegrees(joints[2]) - mq[2][1]) / mq[2][0];
						double wristPulse = (Math.toDegrees(joints[3]) - mq[3][1]) / mq[3][0];
						
						command = "P L "+ Math.round(basePulse) + " " + Math.round(shoulderPulse) + " " + Math.round(elbowPulse) + " " + Math.round(wristPulse) + "\n";
						
						output.write(command.getBytes());
						output.flush();
						System.out.println(command);
						index_point++;
					}
				}
			}  
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
