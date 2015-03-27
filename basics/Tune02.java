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
	Grapher graph; 						// panel to display wave

	
	double[] wave1;						//holds the raw recorded sound wave (another?)
	double[] sum;						//holds the sum of the absolute value of 10 values of the wave array
	int N;								//the length of the array according to how long the recording goes
	int intCount;						//holds half the amount of the length of the recording
	int width = 800;					// width of window in pixels
	
	JButton record, play, graphMe;		//buttons to start the function of the program
	JPanel panel1, panel2;				//holds the buttons and the graphs
	
	FFT trans;							// fourier transform variable
	Complex[] wave1c;
	
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
		//setLocationRelativeTo(null);
		setLayout( new BorderLayout());
			
		// add play, record, and graph buttons to NORTH panel
	    panel1 = new JPanel();
		panel1.setBackground(Color.gray);
	    add( panel1, BorderLayout.NORTH );
	    panel1.setLayout( new FlowLayout() );
	    record = makeButton( "Record", panel1 ); // new JButton("Record");
	    play = makeButton( "Play", panel1 );
	    graphMe = makeButton( "Graph", panel1 );

	    count =100000;
	    
		wave = new double[count];
		wave1 = new double[count];
		
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
	    panel2.add( new Grapher(wave1, 8000, width) );
	    //panel2.add( new Grapher(sum, 8000, width) );
	    
	  //  wave1c = Complex.real2complex(wave1, 4096);
	  //  trans = new FFT(); 
	  //  double[] wave1d = Complex.complex2real(trans.fft(wave1c), 4096);
	  //  panel2.add(new Grapher(wave1d, 4096, width) );
		
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
		else if ( e.getSource() == play    ) { playSound = new Player(); }
		else if ( e.getSource() == graphMe ) { alignWave(); }
		
		
		validate();
		repaint();
	}
	
	//gets the sample that is clicked on from the wave graph
	public void alignWave()
	{       
		int offset = graph.getSample();
		System.out.println("alignWave: offset="+offset);
	   
		for( int i=0; i<8000; i++ )
		{
			wave1[i] = wave[i+offset];
		}
		doSum( wave1 );
		
		  wave1c = Complex.real2complex(wave1, 4096);
		  trans = new FFT(); 
		  double[] wave1d = Complex.complex2real(trans.fft(wave1c), 250);
		  panel2.add(new Grapher(wave1d, 250, width) );
		
		validate(); repaint();

	}
	
	//recorder method to set up and start the recording of sound
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
	
	public void findKey(double tune)
	{
		if (tune < 230 && tune > 210) 						// A = 220
		{
			System.out.println("The note you sang was A");
		}
		else if (tune < 243 && tune > 230)					// A# = 233
		{
			System.out.println("The note you sang was A#");
		}
		else if (tune < 256 && tune > 243)					// B = 246
		{
			System.out.println("The note you sang was B");
		}
		else if (tune < 271 && tune > 256)					// C = 261
		{
			System.out.println("The note you sang was C");
		}
		else if (tune < 287 && tune > 271)					// C# = 277
		{
			System.out.println("The note you sang was C#");
		}
		else if (tune < 303 && tune > 287)					// D = 293
		{
			System.out.println("The note you sang was D");
		}
		else if (tune < 321 && tune > 303)					// D# = 311
		{
			System.out.println("The note you sang was D#");
		}
		else if (tune < 339 && tune > 321)					// E = 329
		{
			System.out.println("The note you sang was E");
		}
		else if (tune < 359 && tune > 339)					// F = 349
		{
			System.out.println("The note you sang was F");
		}
		else if (tune < 379 && tune > 359)					// F# = 369
		{
			System.out.println("The note you sang was F#");
		}
		else if (tune < 401 && tune > 379)					// G = 391
		{
			System.out.println("The note you sang was G");
		}
		else if (tune < 425 && tune > 401)					// G# = 415
		{
			System.out.println("The note you sang was G#");
		}
		else if (tune < 450 && tune > 425)					// A = 440
		{
			System.out.println("The note you sang was A");
		}
			
	}

}