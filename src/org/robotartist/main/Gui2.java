package org.robotartist.main;

import gnu.io.CommPortIdentifier;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.RenderingHints;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.border.EtchedBorder;
import javax.swing.JSlider;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.robotartist.comm.ArduinoSerialIO2;
import org.robotartist.kinem.AL5D;
import org.robotartist.kinem.Taylor;
import org.robotartist.util.ExtensionFileFilter;
import org.robotartist.util.ImgHlpr;
import org.robotartist.util.LayoutFileFilter;
import org.robotartist.util.Loader;
import org.robotartist.vect.CustomVectorizer;
import javax.swing.JComboBox;

public class Gui2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JSlider slider;
	private JSlider slider_1;
	private JSlider slider_2;
	private JSlider slider_3;
	private JSlider slider_4;
	private JSlider slider_5;
	final JFileChooser fc = new JFileChooser();
	private BufferedImage myPicture=null;
	private BufferedImage myPicture1=null;
	private BufferedImage myPicture2=null;
    private CustomVectorizer custom = null;
    private String nameImage=null;
    private int pictureWidth = 0;
    private int pictureHeight = 0;
	private String PORT_NAMES[] = new String[20];
    private String selectedPortName = null;
    private ArduinoSerialIO2 as;

    protected static final String EXTENSION = ".png";

    protected static final String FORMAT_NAME = "png";

    protected static final LayoutFileFilter SAVE_AS_IMAGE = 
            new LayoutFileFilter("PNG Image Format", EXTENSION, true);

    protected int chooseSaveFile(BufferedImage image) {
        JFileChooser fileChooser = new JFileChooser();
        ExtensionFileFilter pFilter = new ExtensionFileFilter(SAVE_AS_IMAGE);
        fileChooser.setFileFilter(pFilter);
        int status = fileChooser.showSaveDialog(this);

        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                String fileName = selectedFile.getCanonicalPath();
                if (!fileName.endsWith(EXTENSION)) {
                    selectedFile = new File(fileName + EXTENSION);
                }
                ImageIO.write(image, FORMAT_NAME, selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return status;
    }
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Loader.loadOpenCV();

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					Gui2 frame = new Gui2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void openFile(){
	
       JFileChooser chooser = new JFileChooser();
       int r=chooser.showOpenDialog(this);
       if(r!= chooser.APPROVE_OPTION)
    	   return;
       
   		try {
   		    nameImage=chooser.getSelectedFile().toString();
			myPicture = ImageIO.read(chooser.getSelectedFile());
			pictureWidth = myPicture.getWidth();
			pictureHeight = myPicture.getHeight();

			custom = new CustomVectorizer(myPicture);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
   		repaint();
    }
	
 private void openBorderedImage(){
	 
	 if(myPicture!=null){
		 
			custom.setHighTresh(slider.getValue());
			custom.setKsize(slider_1.getValue());
			custom.setSigma(slider_2.getValue());
			custom.preprocess();
			Mat a = custom.getTmpImg();
			myPicture1=ImgHlpr.matToImage(a);
			
		
		 
			repaint();

	 }
	 
 }
 
   private void openVectorizedImage(){
	   
if(myPicture1!=null){			
	   custom.setDpThresh(slider_3.getValue());
	   custom.setMinPathSize(slider_5.getValue());
       try {
		custom.vectorize();
	} catch (IOException e) {
		e.printStackTrace();
	}
	Size size=new Size(custom.getEdges().height(), custom.getEdges().width());
    List<List<Point>> vectors = custom.getVectors(); 
   	Mat show = new Mat(size, custom.getEdges().type(), new Scalar(255.0));
	for (int i = 0; i < vectors.size(); i++) {
		for (int k=0; k<vectors.get(i).size()-1; k++) {
			Point p1 = vectors.get(i).get(k);
			Point p2 = vectors.get(i).get(k+1);
			Core.line(show, p1, p2, new Scalar(0), 1);
		}
	}
	Core.flip(show.t(), show, 1);
    Core.flip(show, show, 1);
	myPicture2=ImgHlpr.matToImage(show);

     
		 repaint();  
    }	   
}
		   	   
   private void plotImage(){
	   
	   double height = -4.1;
		List<List<Point>> vect = ImgHlpr.scaleVectors(custom.getVectors(), pictureWidth, pictureHeight);
		Taylor t = new Taylor(new AL5D());
		List<List<Point3>> paths = new ArrayList<List<Point3>>();
		for(int i=0; i<vect.size(); i++) {
			List<Point3> path = new ArrayList<Point3>();
			path.add(new Point3(vect.get(i).get(0).x, vect.get(i).get(0).y, height+7.0));
			for(Point p : vect.get(i)) {
				path.add(new Point3(p.x, p.y, height));
			}
			paths.add(path);
			List<Point3> pickAndPlace = new ArrayList<Point3>();
			pickAndPlace.add(new Point3(vect.get(i).get(vect.get(i).size()-1).x, vect.get(i).get(vect.get(i).size()-1).y, height));
			pickAndPlace.add(new Point3(vect.get(i).get(vect.get(i).size()-1).x, vect.get(i).get(vect.get(i).size()-1).y, height+5.0));
			if (i != vect.size()-1)
				pickAndPlace.add(new Point3(vect.get(i+1).get(0).x, vect.get(i+1).get(0).y, height+5.0));
			else
				pickAndPlace.add(new Point3(10.0, 0.0, -3.0));
			paths.add(pickAndPlace);
		}
		

		for(List<Point3> l : paths) {
			for (int i=0; i<l.size()-1; i++) {
				t.bdjp(l.subList(i, i+2), (double)slider_4.getValue()/1000);
			}
		}
		System.out.println(paths);
		//ArduinoSerialIO as = new ArduinoSerialIO(CommPortIdentifier.getPortIdentifier("COM11"), 9600, path);
		as = new ArduinoSerialIO2(selectedPortName);
		as.setPaths(paths);
		as.initialize();
		System.out.println("Started");
	
	
	   
   }
   
    // common listener for all sliders
    ChangeListener listener = new ChangeListener()
       {
          public void stateChanged(ChangeEvent event)
          {
             // update text field when the slider value changes
             JSlider source = (JSlider) event.getSource();
             
              if(source.equals(slider))
               textField.setText("" + source.getValue());
              if(source.equals(slider_1))
                  textField_1.setText("" + source.getValue());
              if(source.equals(slider_2))
                  textField_2.setText("" + source.getValue());
              if(source.equals(slider_3))
                  textField_3.setText("" + source.getValue());
              if(source.equals(slider_4))
                  textField_4.setText("" + (double)source.getValue()/1000);
              if(source.equals(slider_5))
                  textField_5.setText("" + source.getValue());
              if(myPicture1!=null)
            	  openBorderedImage();
              if(myPicture2!=null)
            	  openVectorizedImage();
          }


       };
    private JTextField textField_5;

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public Gui2() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 930, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		
		JPanel panel = new JPanel(){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				if(myPicture!=null) 
					g.drawImage(myPicture.getScaledInstance(285, 330, RenderingHints.KEY_RENDERING.hashCode() ), 0, 0, null);
			}
		};
		
		
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel.setBackground(Color.WHITE);
		
		
		
		JPanel panel_1 = new JPanel(){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				if(myPicture1!=null) {
					g.drawImage(myPicture1.getScaledInstance(290, 330, RenderingHints.KEY_RENDERING.hashCode() ), 0, 0, null);
				}
			}
		};
		
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_1.setBackground(Color.WHITE);
		
		 JPanel panel_2 = new JPanel(){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				 if(myPicture2!=null){
						g.drawImage(myPicture2.getScaledInstance(290, 330, RenderingHints.KEY_RENDERING.hashCode() ), 0, 0, null);

				 }
			}
		};
		
		 
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_2.setBackground(Color.WHITE);
		
		JLabel lblPhoto = new JLabel("Photo");
		
		JLabel lblNewLabel = new JLabel("Preview");
		
		JLabel lblVectorizedImage = new JLabel("Vectorized Image");
		
		JButton btnS = new JButton("Sketch");
		btnS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openBorderedImage();
			}
		});
		
		JButton btnVectorize = new JButton("Vectorize");
		btnVectorize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openVectorizedImage();
			}
		});
		
		
		JPanel panel_3 = new JPanel();
	    panel_3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));
		
		JButton btnOpenImage = new JButton("Open Image");
		btnOpenImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				openFile();		           		             				
			}
		});
		
	
		JButton btnSave = new JButton("Save Portrait");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseSaveFile(myPicture2);
			}
		});
		
		JButton btnPlot = new JButton("Plot");
		btnPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotImage();
			}
		});

		//restituisce il nome delle porte
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		
		//First, Find an instance of serial port as set in PORT_NAMES.
		int i=0;
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            PORT_NAMES[i]=currPortId.getName();
			i++;
			
		}
		
	   final JComboBox comboBox = new JComboBox(PORT_NAMES);
		comboBox.addItemListener( 
			
			 new ItemListener(){
			
				@Override
				public void itemStateChanged(ItemEvent e) {

					if(e.getStateChange()==ItemEvent.SELECTED){
						selectedPortName=PORT_NAMES[comboBox.getSelectedIndex()];
					}
				}
				 
			 }
			);
		
		JLabel lblPort = new JLabel("Port");
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				as.close();
			}
		});
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(138)
							.addComponent(lblPhoto)
							.addGap(250)
							.addComponent(lblNewLabel)
							.addGap(224)
							.addComponent(lblVectorizedImage))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(24)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 729, GroupLayout.PREFERRED_SIZE)
									.addGap(12)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addComponent(lblPort)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox, 0, 91, Short.MAX_VALUE))
										.addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_contentPane.createSequentialGroup()
													.addGap(6)
													.addComponent(btnPlot, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
												.addComponent(btnStop, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)))))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(panel, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)))))
					.addGap(20))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(107)
					.addComponent(btnOpenImage)
					.addGap(189)
					.addComponent(btnS)
					.addPreferredGap(ComponentPlacement.RELATED, 211, Short.MAX_VALUE)
					.addComponent(btnVectorize)
					.addGap(106))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(16)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel)
							.addComponent(lblPhoto))
						.addComponent(lblVectorizedImage))
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 322, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 322, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnVectorize)
								.addComponent(btnS)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 322, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnOpenImage)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(18)
							.addComponent(btnSave)
							.addGap(11)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPort))
							.addGap(47)
							.addComponent(btnStop)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPlot, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(13)
							.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(307, Short.MAX_VALUE))
		);
		
		JLabel lblSigma = new JLabel("Sigma");
		
		JLabel lblTaylorThreshold = new JLabel("Taylor threshold");
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Vectorization", TitledBorder.LEADING, TitledBorder.TOP, null, Color.DARK_GRAY));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Preprocessing", TitledBorder.LEADING, TitledBorder.TOP, null, Color.DARK_GRAY));
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 356, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTaylorThreshold)
						.addComponent(lblSigma)
						.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 346, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 210, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(lblSigma)
					.addGap(110)
					.addComponent(lblTaylorThreshold)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		JLabel lblDpThreshold = new JLabel("D-P threshold");
		
		slider_3 = new JSlider();
		slider_3.setMaximum(10);
		slider_3.setValue(3);
		slider_3.setMinimum(1);
		slider_3.setFont(new Font("Letter Gothic Std", Font.PLAIN, 11));
	    slider_3.addChangeListener(listener);

		textField_3 = new JTextField();
		textField_3.setColumns(10);
        textField_3.setText("" + slider_3.getValue());
		JLabel lblTaylor = new JLabel("Taylor");
		
		slider_4 = new JSlider();
		slider_4.setMaximum(100);
		slider_4.setValue(50);
		slider_4.setMinimum(1);
		slider_4.setFont(new Font("Letter Gothic Std", Font.PLAIN, 11));
	    slider_4.addChangeListener(listener);

		textField_4 = new JTextField();
		textField_4.setColumns(10);
        textField_4.setText("" + (double)slider_4.getValue()/1000);
		
		JLabel lblCleanTreshold = new JLabel("Clean treshold");
		
		slider_5 = new JSlider();
		slider_5.setMaximum(50);
		slider_5.setValue(10);
		slider_5.setMinimum(1);
		slider_5.setFont(new Font("Letter Gothic Std", Font.PLAIN, 11));
	    slider_5.addChangeListener(listener);
	    
		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setText("" + slider_5.getValue());


		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_4.createSequentialGroup()
							.addComponent(lblDpThreshold, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(slider_3, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_4.createSequentialGroup()
							.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
								.addComponent(lblTaylor, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCleanTreshold))
							.addGap(18)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING, false)
								.addComponent(slider_5, 0, 0, Short.MAX_VALUE)
								.addComponent(slider_4, GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING, false)
								.addComponent(textField_5, 0, 0, Short.MAX_VALUE)
								.addComponent(textField_4, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))))
					.addGap(69))
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_4.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblDpThreshold))
						.addComponent(slider_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_4.createSequentialGroup()
							.addGap(24)
							.addComponent(lblTaylor))
						.addGroup(gl_panel_4.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
								.addComponent(textField_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(slider_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_4.createSequentialGroup()
							.addGap(23)
							.addComponent(lblCleanTreshold))
						.addGroup(gl_panel_4.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
								.addComponent(textField_5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(slider_5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(59, Short.MAX_VALUE))
		);
		panel_4.setLayout(gl_panel_4);
		
		JLabel lblEdgeThreshold = new JLabel("Edge threshold");
		
		slider = new JSlider();
		slider.setValue(70);
		slider.setMinimum(40);
		slider.setFont(new Font("Letter Gothic Std", Font.PLAIN, 11));
	    slider.addChangeListener(listener);
		textField = new JTextField();
		textField.setColumns(10);
        textField.setText("" + slider.getValue());

		JLabel lblKernelSize = new JLabel("Kernel size");
		
		slider_1 = new JSlider();
		slider_1.setMaximum(9);
		slider_1.setValue(3);
		slider_1.setMinimum(1);
		slider_1.setFont(new Font("Letter Gothic Std", Font.PLAIN, 11));
	    slider_1.addChangeListener(listener);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
        textField_1.setText("" + slider_1.getValue());

		slider_2 = new JSlider();
		slider_2.setValue(2);
		slider_2.setMinimum(1);
		slider_2.setFont(new Font("Letter Gothic Std", Font.PLAIN, 11));
	    slider_2.addChangeListener(listener);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
        textField_2.setText("" + slider_2.getValue());

		JLabel lblSigma_1 = new JLabel("Sigma");
		GroupLayout gl_panel_5 = new GroupLayout(panel_5);
		gl_panel_5.setHorizontalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
						.addComponent(lblEdgeThreshold)
						.addComponent(lblKernelSize)
						.addComponent(lblSigma_1))
					.addGap(18)
					.addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_5.createSequentialGroup()
							.addComponent(slider_2, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_5.createSequentialGroup()
							.addComponent(slider, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_5.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(slider_1, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(13, Short.MAX_VALUE))
		);
		gl_panel_5.setVerticalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_5.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblEdgeThreshold))
						.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblKernelSize)
						.addComponent(slider_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
						.addComponent(slider_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSigma_1))
					.addContainerGap(60, Short.MAX_VALUE))
		);
		panel_5.setLayout(gl_panel_5);
		panel_3.setLayout(gl_panel_3);
		
		contentPane.setLayout(gl_contentPane);
	}
}
