// Complex.java 

package basics;

public class Complex {
	
	double real;
	double imag;
	
	public Complex()
	{
		real = 0;
		imag = 0;
	}
	//Complex constructor
	public Complex(double r, double i) 
	{
		real = r;
		imag = i;
	}
	//Copy constructor
	public Complex( Complex c )
	{
		real = c.real;
		imag = c.imag;
	}
	
	//Adds the real and imaginary parts of plex into this Complex number
	public void add( Complex plex )
	{
		real = real + plex.real;
		imag = imag + plex.imag;
	}
	
	public Complex plus (Complex plex)
	{
		Complex z = new Complex();
		
		z.real = real + plex.real;
		z.imag = imag + plex.imag;
		
		return z;
	}
	
	public void substract( Complex plex )
	{
		real = real - plex.real;
		imag = imag - plex.imag;
	}
	
	public Complex minus (Complex plex)
	{
		Complex z = new Complex();
		
		z.real = real - plex.real;
		z.imag = imag - plex.imag;
		
		return z;
	}
	
	// return this times a
	public Complex times( Complex a )
	{
	   Complex z = new Complex();
	     
	   z.real = real*a.real - imag*a.imag;
	   z.imag = real*a.imag + imag*a.real;
	   
	   return z;
	}
	
	public Complex times( double c)
	{
		Complex z = new Complex(this);
		z.real = z.real*c;
		z.imag = z.imag*c;
		return z;
	}
	
	//Scalar multiply
	public void multiply( double r )
	{
		real = real*r;
		imag = imag*r;
	}
	//Vector multiply
	public void multiply( Complex plex )
	{
		real = (plex.real*real - plex.imag*imag);
		imag = (plex.real*imag + plex.imag*real);
	}
	
	public double magsq()
	{
		double m2 = real*real + imag*imag;
		return m2;
	}
	
	public double mag()
	{
		double m = Math.sqrt(real*real + imag*imag);
		return m;
	}
	
	public Complex conjugate()
	{
		Complex z = new Complex(this);
		z.imag *= -1;
		return z;
	}
	
	public static Complex[] real2complex(double[] r2c,  int length)
	{
		Complex[] bob = new Complex[length];
		
		for ( int i = 0; i < length; i++)
		{
			bob[i] = new Complex();
			bob[i].real = r2c[i];
			
		}
		return bob;
	}
	
	public static double[] complex2real(Complex[] c2r, int length)
	{
		double[] bob2 = new double[length];
		
		for ( int i=0; i < length; i++)
		{
			bob2[i] = c2r[i].mag();
		}
		return bob2;
	}

}
