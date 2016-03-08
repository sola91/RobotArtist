package org.robotartist.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.opencv.core.Point3;
import org.robotartist.kinem.AL5D;
import org.robotartist.kinem.Trajectory;

public class ArduinoSerialIO2 implements SerialPortEventListener {
	
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
	private Thread thr;
	
	public ArduinoSerialIO2(String port){
		
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

    
	public Thread getThr() {
		return thr;
	}

	/**
	 * Handle an event on the serial port.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				/**segnale inviato da arduino per iniziare la trasmissione */
				int c=input.read();
				if((char)c=='O'){
						thr = new Thread() {
							List<Point3> path;
							//matrice parametri mq, modello lineare dei giunti
							double[][] mq = {
									{0.0983 , -59,76},{0.1136 , -74.95 },{0.105 , -69.6},{0.09315 , -49.96}
							} ;
							
								public void run() {
									for (int i = 0; i < paths.size(); i++) {
										path = paths.get(i);
										//passo di campionamento
										double delta = 20;
										double T = 0;
										String command;
										Trajectory t = new Trajectory(al5d, path);
										List<PolynomialSplineFunction> splines = t.generate();
										boolean end = false; //variabile tappo
										while(T<=t.getMaxT(1)*1000){
											double[] joints = new double[4];
											for (int k = 0; k < splines.size(); k++) {
												joints[k] = splines.get(k).value(T/1000.0);
											}
											double basePulse = (joints[0] -mq[0][1]) / mq[0][0];
											double shoulderPulse = (joints[1] -mq[1][1]) / mq[1][0];
											double elbowPulse = (joints[2] -mq[2][1]) / mq[2][0];
											double wristPulse = (joints[3] -mq[3][1]) / mq[3][0];
											if (i%2 == 0)
												command = "P C "+ Math.round(basePulse) + " " + Math.round(shoulderPulse) + " " + Math.round(elbowPulse) + " " + Math.round(wristPulse) + "\n";
											else
												command = "P C "+ Math.round(basePulse) + " " + Math.round(shoulderPulse+20) + " " + Math.round(elbowPulse-70) + " " + "0" + "\n";
											if ((T + delta > t.getMaxT(1)*1000)&& end==false) {
												T = t.getMaxT(1)*1000-0.001;
												end = true;
											}
											else
												T+=delta;
											
											try {
												output.write(command.getBytes());
												output.flush();
												sleep(20);
											} catch (IOException e) {
												e.printStackTrace();
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
									}
								}
							};
							thr.start();
					}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}


