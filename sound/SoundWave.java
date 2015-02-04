//SoundWave.java
//code from 
//http://stackoverflow.com/questions/19914476/plot-the-sine-and-cosine-functions
//this code actually graphs the function while PlotGraph.java does not
//plots the sine/cosine function, not an actual sound wave


package sound;

import java.awt.*;
import javax.swing.*;

public class SoundWave extends JFrame
{
	
	double[] wave;
	double[] transform;
	double[] transformi;
	double[] frequency;
	//double[] sum;
	int variable;
	int N = 300;
	
	JButton transformButton;
	
	public SoundWave()
	{
		//sets the layout of the window
		//constructor
		setLayout( new BorderLayout());
		add( new DrawSine(), BorderLayout.CENTER);
		wave = new double[1025];
		transform = new double[1025];
		transformi = new double[1025]; //imaginary -isin(theta)
		
		add( new Grapher(wave,300) );

		
		for (int i=0; i<300; i++)
		{
			wave[i]=Math.cos((i/10.0)*2*Math.PI);
			wave[i] += (0.5)*Math.cos((i/21.0)*2*Math.PI);
		}
		
		for( int n=0;n<N; n++)
		{
			transform[n] = 0;
			
		 	for(int k=0; k<N;k++)
			{
				transform[n] += (1.0/N)*wave[k]*Math.cos((2*Math.PI*n*k)/N);
				transformi[n] += (1.0/N)*wave[k]*(-Math.sin((2*Math.PI*n*k)/N));
			}
		}
		//transform[47]=1;
		
		setSize( new Dimension(700,700) );
	}
	
	public static void main(String[] args)
	{
		//constructor of window
		SoundWave frame = new SoundWave();
		//frame.setSize(400,700);
		frame.setTitle("RoomBat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	class DrawSine extends JPanel
	{
		double f(double x)
		{
			return Math.sin(2*x); //sine function
		}
		
		double g(double y)
		{
			return Math.cos(y); //cosine function
		}
		
		protected void paintComponent( Graphics g )
		{
			super.paintComponent(g);
			
			//x axis
			g.drawLine(10, 100, 380, 100);
			//y axis
			g.drawLine(10, 30, 10, 190);
			
			//Fourier Transform x axis
			g.drawLine(10, 450, 380, 450);
			//Fourier Transform y axis
			g.drawLine(10, 380, 10, 520);
					
			
			//makes the line function visible
			Polygon p = new Polygon();
			
			//by multiplying by 50, makes the waves more noticeable
			for ( int x = 0; x<=300; x++)
			{
				p.addPoint(x+10, 100 - (int)(50*wave[x]));
				
			}
			
			//draws the function
			g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
			
			//Polygon f = new Polygon();
			//for (int i=0; i<=300; i++)
			//{
				//int variable = 450-(int)(50*wave[i]);
			//creates the frequency display of the sin function
			for(int x=10; x<=380; x=x+10)
			{
				int variable = 450-(int)(50*wave[x]);
				//g.drawLine(x, variable, x, 450);
			}
			//}
			
			Polygon t = new Polygon();
			
			for (int x=0; x<=300; x++)
			{
				t.addPoint(x+10, 450 - (int)(50.0*transform[x]));
			}
			g.drawPolyline(t.xpoints, t.ypoints, t.npoints);
			
			Polygon s = new Polygon();
			
			for(int x=0;x<=300;x++)
			{
				s.addPoint(x+10, 600-(int)(50.0*transformi[x]));
			}
			g.drawPolyline(s.xpoints, s.ypoints, s.npoints);
		}
	}
}
//do fourier transform math on transform
//program sound for wave[i]