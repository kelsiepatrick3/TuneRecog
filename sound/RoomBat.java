//RoomBat.java

package sound;

import java.awt.*;
import javax.swing.*;

public class RoomBat extends JFrame
{
	
	double[] wave;
	double[] transform;
	double[] transformi;
	double[] frequency;
	//double[] sum;
	int variable;
	int N = 300;
	
	JButton transformButton;
	
	public RoomBat()
	{
	   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   
		//sets the layout of the window
		//constructor
		setLayout( new FlowLayout() );
		//add( new DrawSine() );
		wave = new double[1025];
		transform = new double[1025];
		transformi = new double[1025]; //imaginary -isin(theta)
		

		
		for (int i=0; i<300; i++)
		{
			wave[i]=Math.cos((i/10.0)*2*Math.PI);
			wave[i] += (0.5)*Math.cos((i/21.0)*2*Math.PI);
		}
      add( new Grapher(wave,300) );

		for( int n=0;n<N; n++)
		{
			transform[n] = 0;
			
		 	for(int k=0; k<N;k++)
			{
				transform[n] += (1.0/N)*wave[k]*Math.cos((2*Math.PI*n*k)/N);
				transformi[n] += (1.0/N)*wave[k]*(-Math.sin((2*Math.PI*n*k)/N));
			}
		}
		
		add( new Grapher(transform,300) );
		//transform[47]=1;
		
		setSize( new Dimension(700,700) );
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		//constructor of window
		RoomBat frame = new RoomBat();
		//frame.setSize(400,700);
		frame.setTitle("RoomBat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
//do fourier transform math on transform
//program sound for wave[i]