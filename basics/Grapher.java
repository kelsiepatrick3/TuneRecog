//Grapher.java
// This class is a JPanel that graphs waves.


package basics;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public class Grapher extends JPanel implements MouseListener, MouseMotionListener
{
	
	double[] wave;  				// pointer to the data, note this is live
	
	Complex[] wavec; 				// if we are doing complex, THIS is really where the data is
	int N;
	double divisor = 0.0;  			// max of data
	boolean isComplex = false; 		// false means double, true=complex
	int width = 700; 				// width of the panel in pixels
	int sample;					    // index to take a sample (is set by user clicking on graph)
	int sampleSize=4096; 			// number of values in this sample
	double pixPerSample;
	
	public Grapher( double[] wave1, int N1, int w )
	{
      wave = wave1; // shallow copy
      isComplex = false;
      theRest( N1, w );
	}
	
	public Grapher( Complex[] wavec1, int N1, int w )
	{
	   wave = new double[N1];
	   wavec = wavec1; // shallow copy
	   isComplex = true;
	   theRest( N1, w );
	}
	
	public void recon(double[] wave1, int N1, int w)
	{
		wave = wave1; 
		isComplex = false;
		width = w; 
	    N = N1;
	    sample = 0;
	}
	
	public void theRest( int N1, int w )
	{
      width = w; 
      N = N1;
      
      sample = 0;
      
      pixPerSample = 1.0 * width / N;
     
      addMouseListener(this);
      addMouseMotionListener(this);
      
      setLayout( new FlowLayout());
      setPreferredSize( new Dimension(width,200) );

      setBackground( Color.black );
      setVisible(true);
	}
	
	// set divisor to the maximum value of wave[] (but not less than .1)
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
		
		if( divisor < 0.1 )
		{
			divisor = 1.0;
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
	
	// copy from this Grapher's wave[] to target, start at wave[sample]
	// and do howMuch values
	public void snip( double[] target, int howMuch )
	{
	   for ( int i=0; i<howMuch; i++ )
	   {
	      target[i] = wave[sample+i];
	   }
	}
	
	// if no size is given, use sampleSize
	public void snip( double[] target )
	{
	   snip( target, sampleSize );
	}
			
	public void paint( Graphics g )
	{
		super.paint(g);
		
		if (isComplex) { realify(); }
		
		computeDivisor();
		
		g.setColor(Color.white);
		g.drawLine(0, 100, width, 100);	
			
		Polygon p = new Polygon();
		
		double xstep = 1.0*width/N;

		//by multiplying by 50, makes the waves more noticeable
		for ( int t = 0; t<N; t++)
		{
			int xpix = (int)(t*xstep);
			p.addPoint(xpix, 100 - (int)((wave[t]/divisor)*100));
		}
		
		g.setColor(Color.red);
		//draws the function
		g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
		
		g.setColor(Color.cyan);
		
		int sx = (int)(sample*pixPerSample);
		int sxx = (int)((sample+sampleSize)*pixPerSample);
		g.drawLine(sx, 0, sx, 200);
		g.drawLine(sxx, 0, sxx, 200);
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
	 
	 public void mouseClicked( MouseEvent e ) 
	 {
			int x = e.getX();
		    sample = N * x / width ;
		    sampleSize = 4096;
		    repaint();
	}
	public void mouseEntered( MouseEvent e ) {}
	public void mouseExited( MouseEvent e ) {}
	public void mousePressed( MouseEvent e )
	{
			int x = e.getX();
		    sample = N * x / width ;
		    sampleSize = 200;
		    repaint();
	}
	public void mouseReleased( MouseEvent e ){}
	public void mouseDragged( MouseEvent e ) 
	{
			int x = e.getX();
			sampleSize = (N * x / width)-sample; 
			repaint();
	}
	public void mouseMoved( MouseEvent e ) {}
	


   public int getSample() { return sample; }
   public int getSampleSize() { return sampleSize;}
   
 
}
