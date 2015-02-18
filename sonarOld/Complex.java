package sonarOld; 

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
	
	 // return sum of this plus a
	 public Complex plus( Complex a )
	 {
	    Complex z = new Complex();
	   
	    z.real = real + a.real;
	    z.imag = imag + a.imag;
	  
	    return z;
	 }
	 
	 public Complex addition( Complex a, Complex b )
	 {
		 Complex z = new Complex();
		 z.real = a.real+b.real;
		 z.imag = a.imag+b.imag;
		 return z;
	 }
	
	//Adds the real and imaginary parts of plex into this Complex number
	public void add( Complex plex )
	{
		real = real + plex.real;
		imag = imag + plex.imag;
	}
	
	public Complex minus( Complex a )
	{
		Complex z = new Complex();
	     
	    z.real = real - a.real;
	    z.imag = imag - a.imag;
	     
	    return z;
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
	public Complex multiply( double r )
	{
		real = real*r;
		imag = imag*r;
		
		Complex z = new Complex();
		
		z.real = real - imag;
		z.imag = real + imag;
		
		return z;
	}
	//Vector multiply
	public Complex multiply( Complex plex )
	{
		Complex z = new Complex();
		z.real = (plex.real*real - plex.imag*imag);
		z.imag = (real*plex.imag + imag*plex.real);
		return z;
	}
	
	// time.  return Complex which is this one times a (double) 
	public Complex times( double a )
	{
	   return new Complex( real*a, imag*a );
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
		
		return new Complex( real, -imag );
	}

}
