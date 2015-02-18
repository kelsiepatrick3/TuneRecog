//Grapher.java
// This class is a JPanel that graphs ONE wave.

package wavegraph5;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Grapher extends JPanel implements MouseListener
{
	
	double[] wave; // pointer to the data, note this is live
	Complex[] wavec; // if we are doing complex, THIS is really where the data is
	boolean isComplex = false; // false means double, true=complex
	int N;
	double divisor; // max of data
	int width = 400;
	int selectedSample;
	
	
	public Grapher( double[] wave1, int N1, int w )
	{
      wave = wave1; // shallow copy
      isComplex = false;
      theRest( N1, w );
      addMouseListener(this);
	}
	
	public Grapher( Complex[] wavec1, int N1, int w )
	{
	   wavec = wavec1; // shallow copy
	   isComplex = true;
      theRest( N1, w );
      addMouseListener(this);
	}

	// constructor stuff that is the same for both double and complex
	public void theRest( int N1, int w )
	{
      width = w; 
      N = N1;
     
      setLayout( new FlowLayout());
      setPreferredSize( new Dimension(width,200) );
      setBackground( randomPastel() );
      setVisible(true);
	}
	
	// find the maximum value of the data, used to scale graph
	public void computeDivisor()
	{
	     divisor = wave[0];
	     for( int i=1; i<N; i++)
	     {
	        if( wave[i] > divisor )
	        {
	           divisor = wave[i];
	        }
	     }
	}
	
	// put the magnitudes of the complex array into a double array (ready to be graphed)
	public void realify()
	{
	   wave = new double[N];
      for ( int i = 0; i < N; i++ )
      {
         wave[i] = wavec[i].mag();
      }
	}
	
   public void mouseEntered( MouseEvent e ) {}
   public void mouseExited( MouseEvent e ) {}
   public void mousePressed( MouseEvent e ) {}
   public void mouseReleased( MouseEvent e ) {}
   public void mouseClicked( MouseEvent e )
   {
      int x = e.getX();
      selectedSample = N * x / width ;
      System.out.println("Grapher.mouseClicked: selectedSample="+selectedSample);
   }
		
	protected void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		
		if (isComplex) { realify(); }
		
		computeDivisor();
		//x axis
		g.drawLine(0, 100, width, 100);	
			
		Polygon p = new Polygon();
		
		double xstep = 1.0*width/N;

		//by multiplying by 50, makes the waves more noticeable
		for ( int t = 0; t<N; t++)
		{
		   int xpix = (int) (t * xstep);
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


