//SoundWave.java
//code from 
//http://stackoverflow.com/questions/19914476/plot-the-sine-and-cosine-functions
//this code actually graphs the function while PlotGraph.java does not
//plots the sine/cosine function, not an actual sound wave


package wave3;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;

import sound.*;

public class SoundWave extends JFrame implements ActionListener
{
	
	double[] wave, wave1;
	double[] transform;
	double[] transformi;
	Complex[] transformc;
	double[] frequency;
	Complex[] wavec;
	int variable;
	int N=4096;
	Complex c;
	double complexNumber;
	
	FFT fft;
	
	int intCount;
	
	JButton record;
	JButton play;
	JPanel panel1, panel2;
	
	protected Recorder recorder;
	protected Player playSound;
	//protected AudioFile afile;
	
	public SoundWave() 
	{
		setTitle("Sound Wave");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//sets the layout of the window
		//constructor
		setLayout( new BorderLayout());
		//add( new DrawSine(), BorderLayout.CENTER);
		wave = new double[5000];
		wave1 = new double[100];
		wavec = new Complex[5000];
		transform = new double[5000];
		transformi = new double[5000]; //imaginary -isin(theta)
		transformc = new Complex[5000];
		frequency = new double[5000];
		
		fft = new FFT();

		record = new JButton("Record");
		play = new JButton("Play");
		
		panel1 = new JPanel();
		add( panel1, BorderLayout.NORTH );
		panel1.setLayout( new FlowLayout() );
		panel1.add(record);
		panel1.add(play);
		
		panel2 = new JPanel();
		panel2.setLayout( new FlowLayout() );
		add( panel2, BorderLayout.CENTER );
		
		record.addActionListener(this);
		play.addActionListener(this);
		
		panel2.add( new Grapher(wave, intCount) );
	    panel2.add( new Grapher(wave1, 100) );
	    panel2.add( new Grapher(frequency, intCount) );
		
		setSize(400,700);
		setLocationRelativeTo(null);
		setVisible(true);
		
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
			/*
			for( int k=0; k<N; k++)
			{
				Complex exponent = new Complex( Math.cos(2*Math.PI*n*k)/N, -Math.sin((2*Math.PI*n*k)/N));
				exponent.multiply(1.0/N);
				exponent.multiply(wavec[k]);
				transformc[n].add(exponent);
			}
			*/
		}
	}
	
	public static void main(String[] args)
	{
		//constructor of window
		new SoundWave();
	}

	public void actionPerformed(ActionEvent e) 
	{
		if ( e.getSource() == record ){ RecordThis(); }
		if ( e.getSource() == play ){ playSound = new Player(); }
		repaint();
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

	      /* Again for simplicity, we've hard coded the audio file
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
	      int numnum =  targetDataLine.read( dataMaybe, 0, 9996 );
	      System.out.println("numnum="+numnum);
	      intCount = numnum / 2;
	      N = numnum/2;
	      for ( int i=0; i<intCount; i++ )
	      {
	          int val = 256*dataMaybe[2*i+1] + dataMaybe[2*i];
	          System.out.print(", "+val);
	          if ( i%30==0 ) { System.out.println();  }
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
	      
	      N=4096;
	      DFT2( N );
	      
	      //fft.transform( N, wavec, transformc );
	      fft.fftcore( N, wavec, transformc );
	      
	      for ( int i = 0; i < N; i++ ) // BEK N put in for intCount
	      {
	    	  double trans = transformc[i].mag();
	    	  frequency[i] = 200*trans;
	    	  if( i%30 == 0) { System.out.println(""+frequency[i]); }
	      }
	      
	      System.out.println("\n\n"+intCount);
	}

}


