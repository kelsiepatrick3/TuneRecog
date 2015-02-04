//Grapher.java
// This class is a JPanel that graphs ONE wave.

package sonar;

import java.awt.*;

import javax.swing.*;

public class Grapher extends JPanel
{
	
	double[] wave;  				// pointer to the data, note this is live
	Complex[] wavec; 				// if we are doing complex, THIS is really where the data is
	int N;
	double divisor = 0.0;  			// max of data
	boolean isComplex = false; 		// false means double, true=complex
	int width = 500;
	
	public Grapher( double[] wave1, int N1, int w )
	{
      wave = wave1; // shallow copy
      isComplex = false;
      theRest( N1, w );
	}
	
	public Grapher( Complex[] wavec1, int N1, int w )
	{
	   wavec = wavec1; // shallow copy
	   isComplex = true;
	   theRest( N1, w );
	}
	
	public void theRest( int N1, int w )
	{
      width = w; 
      N = N1;
     
      setLayout( new FlowLayout());
      setPreferredSize( new Dimension(width,150) );

      setBackground( Color.black );
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
	
	public void realify()
	{
	  wave = new double[N];
      for ( int i = 0; i < N; i++ )
      {
         wave[i] = wavec[i].mag();
      }
	}
		
	public void paint( Graphics g )
	{
		super.paint(g);
		
		if (isComplex) { realify(); }
		
		computerDivisor();
		
		//x axis
		g.setColor(Color.white);
		g.drawLine(0, 75, width, 75);	
			
		Polygon p = new Polygon();
		
		double xstep = 1.0*width/N;

		//by multiplying by 50, makes the waves more noticeable
		for ( int t = 0; t<N; t++)
		{
			int xpix = (int)(t*xstep);
			p.addPoint(xpix, 75 - (int)((wave[t]/divisor)*100));
		}
		
		g.setColor(Color.red);
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
