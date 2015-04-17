// Tune02.java
// BEK  We are working from Ila's RoomBat thingy, just trying to get some control
// over this code, do some basic recording.

package basics;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import sonar.*; // Ila's old code

import javax.sound.sampled.*;
import javax.swing.*;

public class Tune02 extends JFrame implements ActionListener
{
	
	double[] wave; 						// raw recorded sound wave
	Grapher graph;						// panel to display wave
	Grapher graph2; 					// show subsample of the first wave						
	Grapher graph3;						// shows the FFT sample
	
	double[] wave1;						//holds the raw recorded sound wave (another?)
	double[] wave1d;
	double[] sum;						//holds the sum of the absolute value of 10 values of the wave array
	int N;								//the length of the array according to how long the recording goes
	int intCount;						//holds half the amount of the length of the recording
	int width = 800;					// width of window in pixels
	
	JButton record, play, graphMe,Many;		//buttons to start the function of the program
	JPanel panel1, panel2;				//holds the buttons and the graphs
	
	FFT trans;							// fourier transform variable
	Complex[] wave1c;
	
	AudioFormat audioFormat; 
	
	String[] noteName = new String[]{"A", "A#", "B", "C", "C#","D", "D#","E","F","F#","G", "G#","A"};		
	/*
	 * used to hold the position of the absoluteMax, minimum and 
	 * relative max
	 */
	int k = 0; 						
	int j = 0; 
	int m = 0;
	
	double tune = 0; 					// will be the note that the max will find
		
	int count;							//variable to set the length of the initiated length of array
	
	double absoluteMax, relativeMax;	//variables to hold the absolute max and relative max
										//absolute max is the initial sound and relative max
										//is the immediate echo
	
	protected Recorder recorder;		//Recorder variable
	protected Player playSound;			//Player variable
	
	public static void main(String[] args)
	{
		//constructor of window
		new Tune02();
	}
	
	public Tune02()
	{
		setSize(900,600);
		setTitle("Sound Wave");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout( new BorderLayout());
			
		// add play, record, and graph buttons to NORTH panel
	    panel1 = new JPanel();
		panel1.setBackground(Color.gray);
	    add( panel1, BorderLayout.NORTH );
	    panel1.setLayout( new FlowLayout() );
	    record = makeButton( "Record", panel1 ); // new JButton("Record");
	    play = makeButton( "Play", panel1 );
	    graphMe = makeButton( "Graph", panel1 );
	    Many = makeButton("Continuous", panel1);

	    count =100000;
	    
		wave = new double[count];
		wave1 = new double[count];
		wave1d = new double[count];
		
		//wavec = new Complex[65536];
		//transformc = new Complex[count]; 
		//frequency = new Complex[count];
		
		sum = new double[count];						//initiate sum array

		// add panel2 to CENTER, put Graphs on it
		panel2 = new JPanel();
		panel2.setLayout( new FlowLayout() );
		add( panel2, BorderLayout.CENTER );
		panel2.setBackground(Color.gray);
		
		panel2.add( graph = new Grapher(wave, 65536/2, width) );
	    panel2.add( graph2 = new Grapher(wave1, 8000, width) );
	    panel2.add(graph3 = new Grapher(wave1d, 250, width));
	    //panel2.add( new Grapher(sum, 8000, width) );
	    
	  //  wave1c = Complex.real2complex(wave1, 4096);
	  //  trans = new FFT(); 
	  //  double[] wave1d = Complex.complex2real(trans.fft(wave1c), 4096);
	  //  panel2.add(new Grapher(wave1d, 4096, width) );
	    
	    /*
		 * For simplicity, the audio data format used for recording
	      is hard coded here. We use PCM 44.1 kHz, 16 bit signed,
	      stereo. 
	    */    
	    audioFormat = new AudioFormat(
		         AudioFormat.Encoding.PCM_SIGNED,
		         44100.0F, 16, 2, 4, 44100.0F, false);
		
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
	
	//sets the action to be done when buttons are pushed
	public void actionPerformed(ActionEvent e) 
	{
		if      ( e.getSource() == record  ) { RecordThis(); }
		else if ( e.getSource() == play    ) { playSound = new Player(this); }
		else if ( e.getSource() == graphMe ) { alignWave(); }
		else if (e.getSource() == Many){redo();		}
		
		
		validate();
		repaint();
	}
	
	public void redo()
	{
		for( int i = 0; i < 50; i++)
		{
			RecordThis();
			alignWave();
		}
	}
	//gets the sample that is clicked on from the wave graph
	public void alignWave()
	{       
		int offset = graph.getSample();
		System.out.println("alignWave: offset="+offset);
		int sampleSize = graph.getSampleSize();
		graph2.theRest(sampleSize, width);
		
		for( int i=0; i<sampleSize; i++ )
		{
			wave1[i] = wave[i+offset];
		}
		doSum( wave1 );
		
		  wave1c = Complex.real2complex(wave1, 4096);
		  trans = new FFT(); 
		  wave1d = Complex.complex2real(trans.fft(wave1c), 250);
		  
		  graph3.recon( wave1d, 250, width);
		  int mi = maxIndex(wave1d,250);
		  System.out.println("Max index = " + mi);
		  double f = mi * 10.75;
		  System.out.println("The frequency is " + f);
		  findKey(f);
		  
		validate(); repaint();

	}
	
	//recorder method to set up and start the recording of sound
	public void RecordThis()
	{
		File  outputFile = new File("HollerHolder");
		
	     

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
	      recorder = new Recorder(
	         targetDataLine,
	         targetType,
	         outputFile); 
	      
	      recorder.startRecording();
	      try{ Thread.sleep(500); } catch ( Exception o ) { System.out.println("insomnia"); }
	      
	      byte[] dataMaybe = new byte[count];
	      int numnum =  targetDataLine.read( dataMaybe, 0, 65536 );
	      System.out.println("numnum="+numnum);
	      intCount = numnum/2;
	      N = numnum/2;
	      for ( int i=0; i<intCount; i++ )
	      {
	          int val = 256*dataMaybe[2*i+1] + dataMaybe[2*i];
	          wave[i] = val;
	      }
	      
	      recorder.stopRecording(); System.out.println(" \n Recording stopped.");
	      
	      // sets a new array filled with the first 8000 variables from wave[]
		 //graph.snip(wave1,8000);
		 graph.snip(wave1);
	}
	

	//analysis
	/*public void doFFT()
	{
		for ( int i=0; i<65536; i++ ) 
		{
			wavec[i] = new Complex( wave[i],0 ); 
		}

	   fft = new FFT(wavec);
	   transformc = FFT.fft(wavec);	   
	}*/
	
	//sums up all the variable of the wave array in order to 
	//smooth it out
	public void doSum( double w[] )
	{
		int var = 300;
		for( int i = 0; i < 8000 - 2*var; i++ )
		{
			sum[i] = 0;
			for( int n=0; n < var*2 ; n++ )
			{
				double a = Math.abs( w[i+n] );
				double d = Math.exp(-((n - var)*(n - var))/(2*var*var));
				sum[i] += a*d;
			}
		}
		
		doDistance(sum);
	}
	
	//calculates the max of the wave and the relative max of the
	//wave in order to calculate the distance between the two positions
	//tell how far away the echo is 
	public void doDistance( double[] s )
	{		
		int var = 200;
		for( int i = 0; i < 8000 - 2*var; i++ )
		{
			absoluteMax = s[0];
			
			if( s[i] > absoluteMax )
			{
				absoluteMax = s[i];
				k = i;
			}
		}
		
		for( int i = k+50; i < 8000 - 2*var; i++ )
		{
			double minimum = s[k+50];
			if( s[i] < minimum )
			{
				minimum = s[i];
				j = i;
				if( s[i+1] > minimum )
				{
					break;
				}
			}
		}
		
		for( int n=j; n < 8000 - 2*var; n++ )
		{
			relativeMax = s[j];
			if( s[n] > relativeMax )
			{
				relativeMax = s[n];
				m = n;
			}
		}
		
		System.out.println("K: " + k + ", J: " + j + ", M: " + m );
		
		double distance = m - k;
		System.out.println( "Distance: "+distance );

	}
	// array a is a sample and we are finding the largest value of a 
	// b is the number of elements in a and we return the index of the max
	public int maxIndex(double[] a, int b)
	{
		double max = 0; 
		int mi = 0;
		
		for (int i=4; i < b; i++)
		{
			double A = a[i]; 
			
			if (A > max )
			{
				max = A;
				mi = i; 
			}
			
		}
		
		return mi;
		
	}
	
	public void findKey(double tune)
	{
		double log220 = Math.log(220);
		double log2   = Math.log(2);
		
		if (tune > 440 )
		{
			while (tune > 440)
			{
				tune /= 2;
			}
		}
		if (tune <= 220 )
		{
			while (tune <= 220)
			{
				tune *= 2;
			}
		}
		int n = (int)((Math.log(tune)- log220)*12/log2);
		String theNote = noteName[n];
		System.out.print("The note is " + theNote);		
	
	}

}