//Grapher.java
// This class is a JPanel that graphs ONE wave.

package sound;

import java.awt.*;

import javax.swing.*;

public class Grapher extends JPanel
{
	
	double[] wave;
	int N;
	
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
	
	public static void main( String[] args )
	{
	   new RoomBat();
	}
	
		
		protected void paintComponent( Graphics g )
		{
			super.paintComponent(g);
			
			//x axis
			g.drawLine(0, 100, 399, 100);	
			
			Polygon p = new Polygon();

			//by multiplying by 50, makes the waves more noticeable
			for ( int t = 0; t<N; t++)
			{
				p.addPoint(t, 100 - (int)(50*wave[t]));
				
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
