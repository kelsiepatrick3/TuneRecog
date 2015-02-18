package wave3;

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
	
	public void substract( Complex plex )
	{
		real = real - plex.real;
		imag = imag - plex.imag;
	}
	
	// return this times a
	public Complex times( Complex a )
	{
	   Complex z = new Complex();
	     
	   z.real = real*a.real - imag*a.imag;
	   z.imag = real*a.imag + imag*a.real;
	   
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

}
