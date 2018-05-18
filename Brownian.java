// Author:        Valentin Debarnot, Léo Martire.
// Mail:          leo.martire@outlook.com
// Description:   TODO.
// Last modified: See file metadata.
// Usage:         N/A.
// Notes:         This class implements the decomposition along
//                the non-normalised Faber-Schauder system
//                (often called the Lévy decomposition) of the
//                Brownian motion.

package BrownianGenerator;

import java.util.Arrays; // useful ?
import java.util.Random; // for gaussian vectors

public class Brownian{
  // Attributes //---------------------------------------------
  private int len; // number of time index discretisation points
  private int dim; // Brownian motion dimension
  private int n; // cut parameter (information about the generation)
  private double c; // scaling (information about the generation)
  private double[] start; // Brownian motion starting point
  private double[][] BrownianPath; // Brownian motion values
  //-----------------------------------------------------------
  
  // Constructors //-------------------------------------------
  public Brownian(int Kt){
    // Constructor : basic.
    this(Kt, 1, 1.0, 15);
  }
  public Brownian(int Kt, int dimension){
    // Constructor : default.
    this(Kt, dimension, 1.0, 15);
  }
  public Brownian(int Kt, int dimension, double c){
    // Constructor : scale.
    this(Kt, dimension, c, 15);
  }
  public Brownian(int Kt, int dimension, int N){
    // Constructor : cut.
    this(Kt, dimension, 1.0, N);
  }
  public Brownian(int Kt, int dimension, double c, int N){
    // Constructor : complete.
    // Errors :
    if(Kt<=0){
      System.err.println("Error : interval discretisation is impossible (Kt="+Kt+").");
      System.exit(-1);
    }
    if(c<=0){
      System.err.println("Error : dimension is incorrect (d="+dimension+").");
      System.exit(-1);
    }
    if(c<=0){
      System.err.println("Error : interval length is incorrect (c="+c+").");
      System.exit(-1);
    }
    if(c<=0){
      System.err.println("Error : cut index is incorrect (N="+N+").");
      System.exit(-1);
    }
    
    // Warnings :
    if(Kt<=10){
      System.err.println("Warning : interval discretisation is very small (Kt="+Kt+"), beware.");
    }
    if(c<=1e-12){
      System.err.println("Warning : interval length is very small (c="+c+"), numerical singularities may occur.");
    }
    if(N<=5){
      System.err.println("Warning : cut point is very small (N="+N+"), beware.");
    }
    
    // Construction :
    this.len=Kt;
    this.dim=dimension;
    this.c=c;
    this.n=N;
    this.start=new double[dimension];
    SchauderWavelet sw=new SchauderWavelet(this.len);
    this.BrownianPath=this.getBrownian(sw, this.dim, N);
    for(int i=0; i<this.dim; i++){ // ensure the starting point is O
      this.start[i]=0;
    }
    this.scale(c);
  }
  //-----------------------------------------------------------
  
  // Methods //------------------------------------------------
  private void scale(double c){
    // Scales the [0, 1]-based Brownian motion.
    // @param c the scaling parameter
    // @return void.
    double coef=Math.sqrt(c);
    for(int i=0; i<this.len; i++){
      for(int j=0; j<this.dim; j++){
        this.BrownianPath[j][i]=coef*this.BrownianPath[j][i];
      }
    }
  }
  
  private double[][] getBrownian(SchauderWavelet w, int dimension, int N){
    // Generates an approximation of the Brownian motion using the non-normalised Faber-Schauder system.
    // @param w the SchauderWavelet object used in the approximation
    // @param dimension the dimension of the wanted Brownian motion
    // @param N the cut parameter of the approximation
    // @return a double-index table representing the wanted Brownian motion approximation
    double[][] B=new double[dimension][this.len];
    double[] tmp;
    double[] g;
    int wSt;
    while(!(w.hasToStop() || w.getOrder()>N)){
      tmp=w.getValues();
      g=getGaussians(dimension);
      wSt=w.getStart();
      for(int i=wSt; i<=w.getEnd(); i++){
        for(int j=0; j<dimension; j++){
          B[j][i]+=g[j]*tmp[i-wSt];
        }
      }
      //w.printStatus(); // Debugging : show all used wavelets when they are used.
      w.iterate();
    }
    w.reinitialize();
    return(B);
  }
  
  private double[] getGaussians(int number){
    // Builds a Gaussian vector.
    // @param number size of the wanted Gaussian vector
    // @return a Gaussian vector
    if(number<=0){
      System.err.println("Error : invalid number of Gaussians requested ("+number+" requested).");
      System.exit(-1);
    }
    double[] g=new double[number];
    Random r=new Random();
    for(int i=0; i<number; i++){
      g[i]=r.nextGaussian();
    }
    return(g);
  }
  
  public void changeStartPoint(double[] coordinates){
    // Changes the starting point of the Brownian motion.
    // @param coordinates a table of coordinates for the new starting point
    // @return void
    if(coordinates.length!=this.dim){
      System.err.println("Error : dimension of requested starting point is invalid (dimension : "+coordinates.length+", Brownian motion dimension : "+this.dim+").");
      System.exit(-1);
    }
    for(int i=0; i<this.len; i++){
      for(int j=0; j<this.dim; j++){
        this.BrownianPath[j][i]-=this.start[j]; // remove the previous starting point
        this.BrownianPath[j][i]+=coordinates[j]; // add the new starting point
      }
    }
    this.start=coordinates;
  }
  
  public double[] get(int t){
    // Returns the t-th coordinate.
    if(t<0 || t>=this.len){
      System.err.println("Error : index requested is invalid (t="+t+", Kt="+this.len+").");
      System.exit(-1);
    }
    double[] r=new double[this.dim];
    for(int i=0; i<this.dim; i++){
      r[i]=this.BrownianPath[i][t];
    }
    return(r);
  }
  
  public int getLength(){
    return(this.len);
  }
  public int getDim(){
    return(this.dim);
  }
  
  public String getCharacteristics(){
    return("kt="+this.len+"; d="+this.dim+"; c="+c+"; n="+n+"; startPoint="+Arrays.toString(this.start));
  }
  //-----------------------------------------------------------
  
  // toString redefinition //----------------------------------
  public String toString(){
    // toString redefinition.
    String str="";
    str+="Brownian motion "+this.hashCode()+" ("+this.getCharacteristics()+") :\n";
    for(int i=0; i<this.len; i++){
      str+="[";
      for(int j=0; j<this.dim; j++){
        str+=Util.formatDouble(this.BrownianPath[j][i]);
        if(j<this.dim-1){str+=", ";}
      }
      str+="]\n";
    }
    return(str);
  }
  //-----------------------------------------------------------
}
