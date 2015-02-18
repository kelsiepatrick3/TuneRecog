//FFT3.java
// 2014 refrsh of the FFT stuff, working from Owie version 2004

package wave3;

//cpp typedef double Sample;
//cpp extern FFT FFT1;

public class FFT3
{
 Complex[][] e2the; // e[c][k] is e ^ -i(2 pi k / (2^c) )
                    // e[c] is table of 2^c numbers that are the 
                    // roots of 1 around the circle.
                    // cpp Replaces the e[][] array, smaller tables used
                    // to wrap, so watch it.
 int cmax=9; // the largest value of c above for which e2the is defined
 static final double pi=3.14159265358979323846264338327950288419716939937510;

 
 // FFT constructor.  fill in the e2the tables.
 // note: k wraps; i.e., columns before the last repeat
 public FFT()
 {
     e2the = new Complex[cmax+1][];
     int c; // which sin cos table to fill
     for ( c=0; c<=cmax; c++ )
     {
         int klimit = (int) Math.pow(2,c); // size of the cth table
         e2the[c] = new Complex[klimit];
         double a = 2 * pi / klimit; // angular step in radians
    double x=0; // angle
         int k; // step through the table
         for ( k=0; k<klimit; k++ )
         {
             e2the[c][k] = new Complex( Math.cos(x), -Math.sin(x) );
             x += a;
         }
     }
 }
 
 // po2(N) returns smallest power of 2 at least as big as N
 // i.e., we round N up to a power of 2
 public int po2( int N )
 {
int P=1;
while ( P < N ) { P *= 2; }
return P;
 }

 // returns log base 2 of po2(N)
 public int log2( int N )
 {
int P=1; int k=0;
while ( P < N ) { P *= 2; k++; }
return k;
 }
 
 // maxofrange.  compute the maximum magsq from fromhere to tohere-1
 public double maxofrange( Complex[] S, int fromhere, int tohere )
 {
int i = fromhere;
double v;
double maxv = 0;
for ( ; i<tohere; i++ )
{ v = S[i].magsq(); if (v>maxv) { maxv = v; } }
return maxv;
 }



 // transform.  Returns the FFT of S (realN points).  Rounds realN up to next 
 // power of 2 as necessary to compute FFT.  
 // (Note, increasing realN makes result longer 
 // than original data, but harmonic spacing is still the same.)
 // Also note, we scale the data back by 1/N to keep the numbers 
 // approximately the same size as the original sound samples.
 // Also, N<=512.  (If you need bigger, jack the e2the table.)
 // cpp replaces fft
 public Complex[] transform( double realN, Complex[] S )
 {
     if ( realN > 512 )
     {  
        System.out.println("FFT.transform: error! realN="+realN);
        realN = 512;
     }
     if ( realN>S.length )
     { System.out.println("FFT.transform:error- realN="+realN
          +" data only ="+S.length ); 
     }
     
     Complex[] D;
     int N = (int) realN;
     
     if ( (N == po2(N)) && ((double)N==realN) ) 
     {
         double oneoverN = 1.0/N;
         D = fftcore( N, S ); 
         for ( int i=0; i<N; i++ ) { D[i] = D[i].times(5*oneoverN); }
     }
     else
     {
         int po2N = po2(N+1); // must sample up to this power of 2
         double oneoverpo2n = 1.0/po2N;
         Complex[] G = interpolate( realN, S, po2N );
         D = fftcore( po2N, G );
         
         for ( int i=0; i<po2N; i++ ) { D[i] = D[i].times(5*oneoverpo2n); }
     }
     return D;
 }
 
 // interpolate.  
 // resample N points of S to make P points of D, can do more
 // or fewer points, real N.
 // if P is bigger, do linear interpolate between points of S (sampleup)
 // if P is smaller, sample up to even multiple of P, then compress
 // by that multiple (sampledown)
 public Complex[] interpolate( double N, Complex[] S, int P )
 {
     Complex D[];
     
if ( N <= P ) { D = sampleup( N, S, P ); }
else
{
    int intN = (int) N;
    int T = ( (intN-1)/P + 1 ) * P ;  // lowest multiple of P >= N
    Complex[] Q = sampleup( N, S, T );
         D = sampledown( T, Q, P );
}
     return D;
 }

 // sampleup resamples N points of S to make P points of D (returned).  
 // sampleup should have P>=N.   (Don't use this to sampledown or
 // you'll get nasty aliasing.)
 // Also, N< size of S is needed, since we access N+1 points.
 private Complex[] sampleup( double N, Complex[] S, int P )
 {
     // if ( P < N ) { throw Exception; } // does this work?
     
     Complex [] D = new Complex[P];
double ratio = N / P  ;

double OldEquiv ; // S-space equivelent of ith destination point
int u ;
double v ;

for (int i = 0; i < P; i++ )
{
         
    OldEquiv = i * ratio;

    u = (int) OldEquiv ; // u is integer part
    v = OldEquiv - u ;   // v is fractional part

         D[i] = S[u].times(1-v) .plus( S[u+1].times(v) );                     
}
     return D;
 }

 // sampledown compresses S into D by averaging points in groups of N/P.
 // N/P must be an integer. i.e., N is multiple of P.
 private Complex[] sampledown( int N, Complex[] S, int P )
 {
     Complex D[] = new Complex[P];
int group = N / P; double rat = 1.0 / group;

int n=0; // counts source points
int p; // counts destination points
int g; // counts groups
for ( p=0; p<P; p++ )
{
   D[p] = new Complex();
   for ( g=0; g<group; g++ ) { D[p] = D[p].plus(S[n]); n++; }
   D[p] = D[p].times(rat);
}
     return D;
 }

 // fftcore.  This is a raw fft.  It takes N points of S and returns
 // the spectrum (N points are valid, but vector may be longer).
 // N must be a power of 2.
 // It does not alter S.
 private Complex[] fftcore( int N, Complex[] S )
 {
     Complex D[];
     
     if (N==1)
     {
         D = new Complex[1];
         D[0] = new Complex(S[0]);  
     }
     else // N>1, divide and recurse
     {
         D = new Complex[N];
         int Nover2 = N/2;
         int c = log2(N);

         Complex[] evens = new Complex[Nover2];
         Complex[] odds  = new Complex[Nover2];
         
         int m;
         for ( m=0; m<Nover2; m++ )
         {
             evens[m] = S[2*m];
             odds [m] = S[2*m+1];
         }
         
         Complex[] a = fftcore( Nover2, evens );
         Complex[] b = fftcore( Nover2, odds  );
         
         for ( m=0; m<Nover2; m++ )
         {
             D[m]        = a[m].plus( b[m].times(e2the[c][m]) );
             D[m+Nover2] = a[m].plus( b[m].times(e2the[c][m+Nover2]) );
         }
     }
     
     return D;
 }
 
  
 // findGPLength.  Given the sound data (array of samples) we
 // determine the best length of the GP.  It has to be between 50 and 500
 // (inclusive).
 // S should have at least 2600 samples defined.
 public int findGPLength( Complex[] S )
 {
// bugger <<"GPfindlength: entering...\n";
int i=50; // candidate length of GP 

double eval; // evaluation of candidate GP length
double maxeval = lengthEval( S, i ) ; // best eval so far
int maxi=i;  // i of best eval so far

for ( i = 51 ; i <= 500 ; i ++ )
{  
    eval = lengthEval ( S, i ) ;
    if ( eval >= maxeval ) { maxeval = eval; maxi = i; }
}
return maxi;
 }

 // lengthEval.  Returns a number which says how good i is as guess at the
 // length of the GP embedded in the data S.
 // lenghtEval is >= 0.  S should be defined to at lest 2600 samples.
 public double lengthEval( Complex[] S, int i )
 {      
     // grab data for 4 GPs of length i
     // downsample, we only need lower freqs
     Complex[] shrunk = interpolate( 4*i, S, 64 ); 
     
     // compute spectrum
     Complex[] spect = transform( 64, shrunk ); 
     
     double eval =
          spect[4].mag() 
       / 
          (    spect[1].mag() + spect[2].mag() 
   + spect[3].mag() + spect[5].mag() 
   + spect[6].mag() + spect[7].mag() 
     );
     
     return eval;
 }
 
 // findSync.  takes the data in S and a period (GP length) and
 // finds the start of the next GP in S.
 /*
    This method works by having a window of length period that is cut in
    half.  We are looking for a placement of this window where the left
    side ("Little") has minimal energy compared to the right side ("Big"). 
 */
 public int findSync( Complex[] S, int period )
 {
     int k;
     int c; // the middle of our split window, slides along 
            // from 5. period t o1.5
int lower = (int) (.5 * period); int upper = (int) (1.5 * period);
     
     int bestc; // the c with the best rat value
     double rat; // the ratio of Big/Little
     double bestrat=0; // best ratio seen so far
     
     // precompute squared magnitudes
     double[] mags = new double[1001];
for ( k=0; k<1001; k++ ) { mags[k] = S[k].magsq(); }

     double Big=0.0, Little=0.0;

     int gplo2 = period/2;  int gplt2 = period * 2;


c=lower; 
// Little from c-gplo2 to c-1, Big from c to c+gplo2-1
for ( k=c-gplo2; k<c; k++ ) { Little += mags[k]; }
for ( k=c; k<c+gplo2; k++ ) { Big    += mags[k]; }
bestrat = Big/Little; bestc = c; 

for ( c=lower+1; c<upper; c++ )
{
   // slide over one
   Little -= mags[c-gplo2-1]; // left most drops from Little
   Little += mags[c-1]; // old center moves from Big to Little
   Big    -= mags[c-1];
   Big    += mags[c+gplo2-1]; // Big adds on right end

   // evaluate
   rat = Big / Little;
   if ( rat > bestrat ) { bestrat = rat; bestc = c; }
   // rats[c] = rat;
}

     return bestc;
 }
 
 /*
The general process we use is dithering.  In dithering,
one computes the same function in different places and
analyzes the results, sorting out what is position-dependant
and what is position-independant.

In our particular case,
we divide the spectrum into several small sections,
which we will call "bites".  ("Divide" is in general
too strong, as the bites COULD be overlapping, but ours 
actually are not.)  
We compute a spectrum on each bite, and for each harmonic 
we average the spectrum values
two different ways, vector, and scaler (on magnitudes).
Noise is the ratio of the vector average
over the magnitude average (on the assumption
that these will be the same for periodic sound, but that
on random sound the vectors will add destructively and
there will be a big difference).  

compute ratio of vector to scaler average of dithered spectra
use N sample points from S, use bite/dither length of len,
save P points of spectra-ratio-vector into D.
We are rather expecting len=20 (which rounds to 32 in the FFT)
and P=14 (i.e., 0 to 13), giving
effective band width of (samplerate * 13/32) = 9khz approx.
 */
 public Complex[] noise( int N, Complex[] S, int P, int len )
 {
     Complex D[] = new Complex[513];
int dith=len; // distance between the starts of the bites
// note: dith=len avoids rotation vectors to get vector sum to align
int num=N/len; // number of bites, fit len into N, no fraction
int d; // counts bites
int ad; // the starting sample number of this bite
int n; // counts harmonics
Complex[] ft = new Complex[33]; // FFT raw (20 rounded up to power of 2 is 32)
Complex[] ftabs = new Complex[20]; // sums the absolute values
Complex[] ftvec = new Complex[20]; // sums as Complex numbers
     
for ( n=0; n<P; n++ )
{
         ftabs[n] = new Complex(); 
         ftvec[n] = new Complex(); 
         D[n] = new Complex();
     }
      
for ( d=0; d<num; d++ )
{
         ad = d*dith;
         
         // copy data for this dither to its own array
         Complex[] Q = new Complex[len+1];
         for ( int k=0; k<len+1; k++ )
         { Q[k] = new Complex(S[k+ad]); }
         
    ft = transform( len, Q ); 
    for ( n=0; n<P; n++ )
    {
   ftabs[n].real += ft[n].mag();
   ftvec[n] = ftvec[n].plus(ft[n]);
    }
}
Complex C;
for ( n=0; n<P; n++ )
{
   C = ftvec[n].times( 1 / ftabs[n].real );
   D[n].real = C.mag();
}
     
     return D;
 }


}

