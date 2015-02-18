//SoundWave.java
// 2014 Ila Torfin and Barrett Koster

package wavegraph5;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;
import javax.swing.*;

import sound.*;

public class SoundWave extends JFrame implements ActionListener
{
	double[] wave; // this is where the original sound byte goes
	double[] wave1;
	double[] transform;
	double[] transformi;
	Complex[] transformc;
	double[] frequency;
	Complex[] wavec;
	int variable;
	int N;
	Complex c;
	double complexNumber;
	
	int intCount=4996;
	int width=800; // width of window in pixels
	
	JButton recordButton; // press this to record 3 seconds of sound
	JButton playButton; // not sure this works
	JButton fftButton; // press this to run the fft / whatever analysis
	JPanel panel1; // where we put all the buttons
	JPanel panel2; // the graphs go here
	
	Grapher clippy;
	
	JButton wave1Button; // click to reset wave1 to part of wave
	
	protected Recorder recorder;
	protected Player playSound;
	//protected AudioFile afile;
	
	FFT fft;
	
   public static void main(String[] args)
	{
	   new SoundWave();
	}


	
   public SoundWave() 
	{
      setSize(width,700);
	   setTitle("Sound Wave");
	   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   setLocationRelativeTo(null);
		setLayout( new BorderLayout());
		
		// add play and record buttons to NORTH
      panel1 = new JPanel();
      add( panel1, BorderLayout.NORTH );
      panel1.setLayout( new FlowLayout() );
      recordButton = makeButton("Record", panel1 ); // new JButton("Record");
      playButton   = makeButton("Play"  , panel1 );  //      play = new JButton("Play");
      fftButton = makeButton("analyze", panel1 );
      wave1Button = makeButton("wave1", panel1 );
      
      // add panel2 to CENTER, put Graphers on it
		
		wave = new double[5000];
		wave1 = new double[1024];
		wavec = new Complex[5000]; for (int p=0; p<5000; p++ ) { wavec[p] = new Complex(); }
		transform = new double[5000];
		transformi = new double[5000]; //imaginary -isin(theta)
		transformc = new Complex[5000]; for (int p=0; p<5000; p++ ) { transformc[p] = new Complex(); }
		frequency = new double[5000];
		
		panel2 = new JPanel();
		panel2.setLayout( new FlowLayout() );
		add( panel2, BorderLayout.CENTER );
		
      panel2.add( clippy = new Grapher(wave, intCount, width) );      
      panel2.add( new Grapher(wave1, 512, width )  );
      //panel2.add( new Grapher(frequency, intCount, width) );
      panel2.add( new Grapher(transformc, intCount, width) );
      
      fft = new FFT(12);

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
	
	public void DFT1()
	{
		for( int n=0;n<N; n++)
		{
			transform[n] = 0;
			
		 	for(int k=0; k<N;k++)
			{
				transform[n] += (1.0/N)*wave[k]*Math.cos((2*Math.PI*n*k)/N);
				transformi[n] += (1.0/N)*wave[k]*(-Math.sin((2*Math.PI*n*k)/N));
			}
		}
	}
	
	public void DFT2( int N1 )
	{
		N = N1;
		for( int n=0; n<N; n++ )
		{
			transformc[n] = new Complex();
			for( int k=0; k<N; k++)
			{
				Complex exponent = new Complex( Math.cos(2*Math.PI*n*k)/N, -Math.sin((2*Math.PI*n*k)/N));
				exponent.multiply(1.0/N);
				exponent.multiply(wavec[k]);
				transformc[n].add(exponent);
			}
		}
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if      ( e.getSource() == recordButton ) { RecordThis(); }
		else if ( e.getSource() == playButton ){ playSound = new Player(); }
      else if ( e.getSource() == fftButton ) { doFFT(); }
      else if ( e.getSource() == wave1Button ) { alignWave1(); }
		repaint();
	}
	
	public void alignWave1()
	{       
	   int offset = clippy.selectedSample;
      System.out.println("SoundWave.alignWave1: offset="+offset);
	   
	   for( int i=0; i<512; i++ )
      {
         wave1[i] = wave[i+offset];
      }

	}
	
	// analysis
	public void doFFT()
	{
	   for ( int i=0; i<N; i++ ) { wavec[i] = new Complex( wave[i],0); }
	   
	   Complex[] te = fft.fftcore( 4096, wavec );

      for ( int i=0; i<N; i++ ) { transformc[i] = new Complex( te[i] ); }
	}
	
	public void RecordThis()
	{
		File  outputFile = new File("HollerHolder");
		/*For simplicity, the audio data format used for recording
	      is hard coded here. We use PCM 44.1 kHz, 16 bit signed,
	      stereo. */
	      AudioFormat audioFormat = new AudioFormat(
	         AudioFormat.Encoding.PCM_SIGNED,
	         44100.0F, 16, 2, 4, 44100.0F, false);

	      /* Now, we are trying to get a TargetDataLine. The
	         TargetDataLine is used later to read audio data from it.
	         If requesting the line was successful, we are opening
	         it (important!). */
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

	      /* Again for simplicity, we've hardcoded the audio file
	         type, too.
	      */
	      AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

	      /* Now, we are creating an SimpleAudioRecorder object. It
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
	      
	      byte[] dataMaybe = new byte[10000];
	      int numnum =  targetDataLine.read( dataMaybe, 0, 8192 );
	      System.out.println("numnum="+numnum);
	      intCount = numnum / 2;
	      N = numnum/2;
	      
	      for ( int i=0; i<intCount; i++ )
	      {
	          int val = 256*dataMaybe[2*i+1] + dataMaybe[2*i];
	          //System.out.print(", "+val);
	          //if ( i%30==0 ) { System.out.println();  }
	          wave[i] = val;
	      }
	      
	      for( int i=0; i<100; i++ )
	      {
	    	  wave1[i] = wave[i];
	      }
	      
	      recorder.stopRecording(); System.out.println(" \n Recording stopped.");
	      
	      for( int i=0; i<intCount; i++)
		  {
	    	  wavec[i] = new Complex(wave[i],0);
		  }

	}

}


