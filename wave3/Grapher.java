//Grapher.java
// This class is a JPanel that graphs ONE wave.
// Barrett Koster 2014

package wave3;

import java.awt.*;

import javax.swing.*;

public class Grapher extends JPanel
{
	
	double[] wave;
	int N;
	double divisor;
	int width = 400;
	
	public Grapher( double[] wave1, int N1 )
	{
      System.out.println("Grapher: entering ... N="+N1);
	  wave = wave1; // shallow copy
	  N = N1;
	  	   
	  setLayout( new FlowLayout());
	  setPreferredSize( new Dimension(400,200) );
	  setBackground( randomPastel() );
	  setVisible(true);
	}
	
	public void computerDivisor()
	{
		divisor = wave[0];
		for( int i=0; i<N; i++)
		{
			if( wave[i] > divisor )
			{
				divisor = wave[i];
			}
		}
	}
		
	protected void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		
		computerDivisor();
		
		//x axis
		g.drawLine(0, 100, width, 100);	
			
		Polygon p = new Polygon();
		
		double xstep = 1.0*width/N;

		//by multiplying by 50, makes the waves more noticeable
		for ( int t = 0; t<N; t++)
		{
			int xpix = (int)(t*xstep);
			p.addPoint(xpix, 100 - (int)((wave[t]/divisor)*100));
			g.setColor(Color.red);
		}
			
		//draws the function
		g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
	 }
	   public Color randomPastel()
	   {
	      Color color = new Color(
	            (int)( 200 + Math.random()*50),
	            (int)( 200 + Math.random()*50),
	            (int)( 200 + Math.random()*50)
	                       );
	      return color;
	   }

}

/*
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
		for ( int x = 0; x<N; x++)
		{
			p.addPoint(x+10, 100 - (int)(50*wave[x]));
			
		}
		
		//draws the function
		g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
		
		Polygon tran = new Polygon();
		for ( int x=0; x<N; x++)
		{
			double z = transformc[x].mag();
			tran.addPoint(x+10, 450 - (int)(50*z));
		}
		g.drawPolyline(tran.xpoints, tran.ypoints, tran.npoints);
	}
}
*/
