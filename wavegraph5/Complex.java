// Complex.java
// 2014 grabbed from Owie

package wavegraph5;

public class Complex
{
   double real;
   double imag;
 
 public Complex()
 {
     real = 0; imag = 0;
 }
 
 public Complex( double r, double i )
 {
     real = r; imag = i;
 }
 
 public Complex( Complex a )
 {
     real = a.real; imag = a.imag;
 }
 
    // return sum of this plus a
   public Complex plus( Complex a )
   {
      Complex z = new Complex();
   
      z.real = real + a.real;
      z.imag = imag + a.imag;
   
      return z;
   }
 
   // add a into this
   public void add( Complex a )
   {
      real = real + a.real;
      imag = imag + a.imag;
   }
 
   // return difference, this minus a
   public Complex minus( Complex a )
   {
     Complex z = new Complex();
     
      z.real = real - a.real;
      z.imag = imag - a.imag;
     
      return z;
   }
 
   // subtract a from this
   public void subtract( Complex a )
   {
      real = real - a.real;
      imag = imag - a.imag;
   }
 
   // return this times a
   public Complex times( Complex a )
   {
      Complex z = new Complex();
     
      z.real = real*a.real - imag*a.imag;
      z.imag = real*a.imag + imag*a.real;
     
      return z;
   }
   
   // multiply a into this
   public void multiply( Complex a )
   {
      double temp = real*a.real - imag*a.imag;
      imag = real*a.imag + imag*a.real;
      real = temp;
   }
    
   // time.  return Complex which is this one times a (double) 
   public Complex times( double a )
   {
      return new Complex( real*a, imag*a );
   }
 
   //time.  return Complex which is this one times a (double) 
   public void multiply( double a )
   {
      real *= a;
      imag *= a;
   }


 
 public double magsq()
 {
     return real*real + imag*imag;
 }
 
 public double mag()
 {
     return Math.sqrt( magsq() );
 }
 
 public void report() {
     System.out.println("real= "+real+" imag="+imag);
 }
 
}