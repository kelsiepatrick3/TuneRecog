//Grapher.java
// This class is a JPanel that graphs ONE wave.

package sonar;

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
	int width = 700;
	int sample;
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
	
	public void computeDivisor()
	{
		divisor = wave[0];

		for( int i=0; i<N; i++)
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
			
	public void paint( Graphics g )
	{
		super.paint(g);
		
		if (isComplex) { realify(); }
		//System.out.println("check1");
		computeDivisor();
		//System.out.println("check2");
		//x axis
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
		
		g.drawLine((int)(sample*pixPerSample), 0, (int)(sample*pixPerSample), 200);
		g.drawLine((int)((sample+8000)*pixPerSample), 0, (int)((sample+8000)*pixPerSample), 200);
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
		    repaint();
	}
	public void mouseEntered( MouseEvent e ) {}
	public void mouseExited( MouseEvent e ) {}
	public void mousePressed( MouseEvent e ) {}
	public void mouseReleased( MouseEvent e ){}
	public void mouseDragged( MouseEvent e ) 
	{
			int x = e.getX();
			sample = N * x / width; 
			repaint();
	}
	public void mouseMoved( MouseEvent e ) {}

}
