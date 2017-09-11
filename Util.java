// Title       : Util.java
// Description : This class contains useful static methods
//               regarding discretisations of affine functions.
// Author      : Léo Martire.
// Date        : 2016.
// Notes       : None.

package BrownianGenerator;

import java.util.Arrays; // useful ?
import java.util.Locale; // for double formatting

public class Util{
  // Attributes //---------------------------------------------
  //-----------------------------------------------------------
  // Constructors //-------------------------------------------
  //-----------------------------------------------------------
  // Methods //------------------------------------------------
  public static double[] concat(double[] a, double[] b){
    // Concatenate two double arrays.
    // @param a the first array
    // @param b the second array
    // @return an array containing a and b at the end of a
    double[] c= new double[a.length+b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }
  
  public static double[] fillWithStep(int size, double startPoint, double stp){
    // Fills an array of given size with values starting at a certain one and incrementing by a fixed step.
    // @param size the wanted size
    // @param startPoint the first value of the array
    // @param stp the step by which to increment
    // @return an array of wanted size with values starting at the wanted one and incrementing by the given step
    double[] arr=new double[size];
    arr[0]=startPoint;
    for(int i=1; i<size; i++){
      arr[i]=arr[i-1]+stp;
    }
    return(arr);
  }
  
  public static double[] getTriangleCoefs(int j, int k){
    // Get the two coefficients encoding the two affine lines encoding the (j, k) non-normalised Faber-Schauder function.
    // @param j j index of the non-normalised Faber-Schauder function
    // @param k k index of the non-normalised Faber-Schauder function
    // @return [a1, b1, a2, b2] where the first slope of the (j, k) non-normalised Faber-Schauder function is given by (a1 * x + b1) and the second one by (a2 * x + b2)
    double[] r={Math.pow(2, (double)(j)/2),
                -k*Math.pow(2, -(double)(j)/2),
                -Math.pow(2, (double)(j)/2),
                (k+1)*Math.pow(2, -(double)(j)/2)};
    return(r);
  }
  
  public static double[] sample(double[] abscissas, double a, double b){
    // Samples over a given array of abcsissas an affine function given by its characteristic coefficients.
    // @param abscissas table containing the wanted abscissas
    // @param a slope of the affine function
    // @param b offset at x=0 of the affine function
    // @return a table containing (a * x + b) evaluated for all x in the abscissas table
    double[] r=new double[abscissas.length];
    for(int i=0; i<abscissas.length; i++){
      r[i]=a*abscissas[i]+b;
    }
    return(r);
  }
  
  public static String formatDouble(double d){
    // Formats a double into a #,#
    String str=(d<0?"":" ")+String.format(Locale.US, "%.16e", d);
    return(str);
  }
  //-----------------------------------------------------------
}