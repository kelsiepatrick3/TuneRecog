//SoundWave.java
//code from 
//http://stackoverflow.com/questions/19914476/plot-the-sine-and-cosine-functions
//this code actually graphs the function while PlotGraph.java does not
//plots the sine/cosine function, not an actual sound wave


package sonar; // blah

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;
import javax.swing.*;

public class SWave extends JFrame implements ActionListener
{
	
	double[] wave, wave1;
	double[] transform;
	double[] transformi;
	Complex[] frequency;
	Complex[] transformc;
	Complex[] wavec;
	int variable;
	int N;
	Complex c;
	double complexNumber;
	
	double[] waveReal, waveImag;
	
	FFT fft;
	
	int intCount;
	int width = 800;		// width of window in pixels
	
	JButton record;
	JPanel panel1, panel2;
	
	int count = 100000;
	
	protected Recorder recorder;
	protected Player playSound;
	
	public static void main(String[] args)
	{
		//constructor of window
		new SWave();
	}
	
	public SWave() 
	{
		setSize(900,700);
		setTitle("Sound Wave");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout( new BorderLayout());
			
		// add play and record buttons to NORTH
	    panel1 = new JPanel();
	    add( panel1, BorderLayout.NORTH );
	    panel1.setLayout( new FlowLayout() );
	    record = makeButton( "Record", panel1 ); // new JButton("Record");

		panel1.setBackground(Color.gray);
		wave = new double[count];
		wave1 = new double[count];
		transformc = new Complex[count]; 
		frequency = new Complex[count];
		
		// add panel2 to CENTER, put Graphers on it
		panel2 = new JPanel();
		panel2.setLayout( new FlowLayout() );
		add( panel2, BorderLayout.CENTER );
		panel2.setBackground(Color.gray);
		setVisible(true);
		
	}
	
	// make a button with this name, add it to this panel, and return the JButton, oh 
	// and send action listener to this
	public JButton makeButton( String name, JPanel jp )
	{
	   JButton b=null;
	   
	   b = new JButton(name);
	   jp.add(b);
	   b.addActionListener(this);
	   
	   return b;
	}

	public void actionPerformed(ActionEvent e) 
	{
		if ( e.getSource() == record )
		{ 
			RecordThis(); 
			doFFT();
			doGraph();
		}
		validate();
		repaint();
	}
	
	public void RecordThis()
	{
		File  outputFile = new File("HollerHolder");
		/*
		 * For simplicity, the audio data format used for recording
	      is hard coded here. We use PCM 44.1 kHz, 16 bit signed,
	      stereo. 
	    */
	     AudioFormat audioFormat = new AudioFormat(
	         AudioFormat.Encoding.PCM_SIGNED,
	         44100.0F, 16, 2, 4, 44100.0F, false);

	      /* 
	       * Now, we are trying to get a TargetDataLine. The
	         TargetDataLine is used later to read audio data from it.
	         If requesting the line was successful, we are opening
	         it (important!). 
	      */
	      DataLine.Info  info = new DataLine.Info(TargetDataLine.class, audioFormat);
	      TargetDataLine targetDataLine = null;
	      try
	      {
	         targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
	         targetDataLine.open(audioFormat);
	      }
	      catch (LineUnavailableException o)
	      {
	         System.out.println("unable to get a recording line");
	         o.printStackTrace();
	         System.exit(1);
	      }

	      /* 
	       * Again for simplicity, we've hard coded the audio file
	         type, too.
	      */
	      AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

	      /* 
	       * Now, we are creating an SimpleAudioRecorder object. It
	         contains the logic of starting and stopping the
	         recording, reading audio data from the TargetDataLine
	         and writing the data to a file.
	      */
	      Recorder  recorder = new Recorder(
	         targetDataLine,
	         targetType,
	         outputFile); 
	      
	      recorder.start();
	      try{ Thread.sleep(3000); } catch ( Exception o ) { System.out.println("insomnia"); }
	      
	      byte[] dataMaybe = new byte[count];
	      int numnum =  targetDataLine.read( dataMaybe, 0, 65536 );
	      System.out.println("numnum="+numnum);
	      intCount = numnum / 2;
	      N = numnum/2;
	      for ( int i=0; i<intCount; i++ )
	      {
	          int val = 256*dataMaybe[2*i+1] + dataMaybe[2*i];
	          wave[i] = val;
	      }
	      
	      recorder.stopRecording(); System.out.println(" \n Recording stopped.");
	      /*
	       * sets a new array filled with the first 100 variables from wave[]
	       */
	      for( int i=0; i<N; i++ )
	      {
	    	  wave1[i] = wave[i];
	      }
	      
	      wavec = new Complex[N];
	}
	
	//analysis
	public void doFFT()
	{
	   for ( int i=0; i<intCount; i++ ) 
	   {
		   wavec[i] = new Complex( wave[i],0 ); 
	   }

	   fft = new FFT(wavec);
	   transformc = FFT.fft(wavec);
	   
	   frequency = FFT.convolve( wavec, wavec );
	   
	}
	//Graphs the functions
	public void doGraph()
	{
		panel2.add( new Grapher(wave, N, width) );
	    panel2.add( new Grapher(wave1, 500, width) );
	    panel2.add( new Grapher(frequency, N, width) );
	    panel2.add( new Grapher(transformc, N, width) );
	}

}