// Title       : SchauderWavelet.java
// Description : This class implements a discretisation of the
//               functions in the non-normalized Faber-Schauder
//               system.
// Author      : Léo Martire.
// Date        : 2016.
// Notes       : None.

package BrownianGenerator;

import java.util.Arrays; // useful ?

public class SchauderWavelet{
  // Attributes //---------------------------------------------
  private boolean s1; // encodes the use of the s_1 wavelet
  private boolean powerOf2; // encodes the mode
  private int len; // number of [0, 1] interval discretisation points
  private int j; // j index of the non-normalised Faber-Schauder function
  private int k; // k index of the non-normalised Faber-Schauder function
  private int width; // width of the wavelet support (only used in powerOf2 mode)
  private double step; // step of the discretisation
  //-----------------------------------------------------------
  
  // Constructors //-------------------------------------------
  public SchauderWavelet(int Kt){
    this.len=Kt;
    this.step=1/(double)(this.len-1);
    if(Math.abs(Math.log(Kt-1)/Math.log(2)-(int)(Math.log(Kt-1)/Math.log(2)))==0){ // checks if powerOf2 mode must be activated
      this.powerOf2=true;
    }
    else{
      this.powerOf2=false;
    }
    reinitialize();
  }
  //-----------------------------------------------------------
  
  // Methods //------------------------------------------------
  public void reinitialize(){
    // Reinitialises the wavelet.
    // @param void
    // @return void
    this.j=0;
    this.k=0;
    this.width=this.len; // initial width of the support (only used in powerOf2 mode)
    this.s1=true; // the first used wavelet is s_1
  }
  
  private void updateWidth(){
    // Calculates width of the support, based on the previous width (only used in powerOf2 mode).
    // @param void
    // @return void
    this.width=(int)((this.width-1)/2+1);
  }
  
  public void iterate(){
    // Iterates the wavelet. That means :
    //   If the current wavelet is s_1, make the boolean false ((j, k) indexes are already initialised).
    //   If k is between 0 and 2^j-1-1, add 1 to k and translate support by the width of it.
    //   If k=2^n-1, add 1 to j bring back k to 0 : start index gets to 0 and height is updated.
    // @param void
    // @return void
    if(this.s1){
      this.s1=false;
    }
    else{
      if(this.k<Math.pow(2, this.j)-1){
        this.k+=1;
      }
      else{
        if(k==Math.pow(2, this.j)-1){
          this.k=0;
          this.j+=1;
          updateWidth(); // (only used in powerOf2 mode)
        }
        else{ // k >= 2^n
          System.err.println("Warning : bad wavelet iteration.");
        }
      }
    }
  }
  
  public boolean hasToStop(){
    // If the width of the support of the wavelet is too small compared to the wanted discretisation (typically, when the length is under 3 indexes), it is no use to try to iterate more.
    // @param void
    // @return true if the width of the support is too small, false if not
    return(this.width<3);
  }
  
  public int getOrder(){
    // Returns the order, that means the j index (encoding the height of the wavelet).
    // @param void
    // @return j index
    return(this.j);
  }
  
  public double[] getValues(){
    // Returns the values of the current wavelet on its discretised support. Lengths of returned tables vary depending on the indexes. If the number of discretisation points is a power of two plus one, getValuesPowerOf2 is used. If not, getValuesGeneral is used.
    // @param void
    // @return the values of the wavelet on its current discretised support.
    if(this.powerOf2){
      return(getValuesPowerOf2());
    }
    else{
      return(getValuesGeneral());
    }
  }
  
  public int getStart(){
    return((int)Math.ceil(2*this.k/Math.pow(2, this.j+1)/this.step));
  }
  public int getEnd(){
    return((int)Math.floor(2*(this.k+1)/Math.pow(2, this.j+1)/this.step));
  }
  
  public double[] getValuesPowerOf2(){
    // Returns the values of the current wavelet on its discretised support when the number of discretisation points is a power of two plus one by using the exact values of the triangle slopes on the indexes.
    // @param void
    // @return the values of the wavelet on its current discretised support.
    double[] vals=new double[this.width];
    if(this.s1){
      for(int i=0; i<this.width; i++){
        vals[i]=(double)(i)/(this.width-1);
      }
    }
    else{
      double height=Math.pow(2, -1-(double)(this.j)/2);
      int indice_milieu=(int)((this.width-1)/2);
      for(int i=0; i<=indice_milieu; i++){ // montée
        vals[i]=i*height/indice_milieu;
      }
      for(int i=indice_milieu+1; i<this.width; i++){ // descente
        vals[i]=(this.width-1-i)*height/indice_milieu;
      }
    }
    return(vals);
  }
  
  public double[] getValuesGeneral(){
    // Returns the values of the current wavelet on its discretised support when the number of discretisation points is not a power of two plus one by sampling over the discretised interval of the two slopes of the triangle.
    // @param void
    // @return the values of the wavelet on its current discretised support.
    double[] vals;
    
    if(this.s1){
      vals=Util.sample(Util.fillWithStep(this.len, 0, this.step),
                       1.0,
                       0.0);
    }
    else{
      double[] up, down, coefs;
      int upperStart, lowerMid, upperMid, lowerEnd;
      upperStart=this.getStart(); // Upper integer value of start point, when projected on the discretised interval.
      lowerMid=(int)Math.floor((2*this.k+1)/(Math.pow(2, this.j+1)*this.step));  // Lower integer value of middle point, when projected on the discretised interval.
      upperMid=(int)Math.ceil((2*this.k+1)/(Math.pow(2, this.j+1)*this.step)); // Upper integer value of middle point, when projected on the discretised interval.
      lowerEnd=this.getEnd(); // Lower integer value of start point, when projected on the discretised interval.
      coefs=Util.getTriangleCoefs(this.j, this.k);
      up=Util.sample(Util.fillWithStep(lowerMid-upperStart+1,
                                       upperStart*this.step,
                                       this.step),
                     coefs[0],
                     coefs[1]);
      down=Util.sample(Util.fillWithStep(lowerEnd-upperMid+1,
                                         upperMid*this.step,
                                         this.step),
                       coefs[2],
                       coefs[3]);
      if(lowerMid!=upperMid){ // If the middle of the triangles drops between two indexes, just concatenate the two arrays.
        vals=Util.concat(up, down);
      }
      else{ // If the middle of the triangles drops exaclty on an index, do not duplicate the vertex' value.
        vals=Util.concat(up, Arrays.copyOfRange(down, 1, down.length));
      }
    }
    return(vals);
  }
  
  public void printStatus(){
    // Prints the status of the current wavelet, using the toString method.
    // @param void
    // @return void
    System.out.println(this);
  }
  //-----------------------------------------------------------
  
  // toString redefinition //----------------------------------
  public String toString(){
    return("\n> SchauderWavelet "+this.hashCode()+" (mode = "+(this.powerOf2?"powerOf2":"general")+") :"+
           "\n>> index"+(this.s1?"":"es")+" : "+(this.s1?"s1":"(j="+this.j+", k="+this.k+")")+","+
           "\n>> support (table indexes) : width "+this.width+", start "+this.getStart()+","+
           "\n>> height : "+(Math.pow(2, -1-(double)(this.j)/2)+","+
           "\n>> values : "+Arrays.toString(this.getValues())+".\n"));
  }
  //-----------------------------------------------------------
}