// Title       : Generator.java
// Description : This class is designed to make the package
//               usable directly by command line and to enable
//               the user to generate Brownian motions as
//               freely as possible.
// Author      : Léo Martire.
// Date        : 2016.
// Notes       : None.

package BrownianGenerator;

import java.io.*; // for file export
import java.util.Arrays; // useful ?
import java.util.HashMap; // for arguments parsing
import java.util.Map; // for arguments parsing

public class Generator{
  // Utilitary methods //--------------------------------------
  private static Map<String, String> parseArgs(String[] tab){
    Map<String, String> params=new HashMap<String, String>();
    String full="";
    for(int i=0; i<tab.length; i++){
      if(tab[i].contains("=")){ // c'est un paramètre
        String parts[]=tab[i].split("\\=");
        params.put(parts[0], parts[1]);
      }
      else{ // c'est une option
        params.put(tab[i], "1");
      }
    }
    return(params);
  }
  
  public static void exportToFile(Brownian b, String fileName){
    Writer out;
    String tmp;
    try{
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
      try {
        out.write(b.getCharacteristics());
        out.write("\r\n");
        for(int i=0; i<b.getLength(); i++){
          tmp="";
          for(int j=0; j<b.getDim(); j++){
            tmp+=Util.formatDouble(b.get(i)[j]);
            if(j<b.getDim()-1){tmp+=" ";}
          }
          out.write(tmp);
          out.write("\r\n");
        }
        out.close();
      }
      catch(IOException e){
        System.err.println("Error : IOException. See stack trace below."); e.printStackTrace(); System.exit(-1);
      }
    }
    catch(FileNotFoundException e){
      System.err.println("Error : FileNotFoundException. See stack trace below."); e.printStackTrace(); System.exit(-1);
    }
    catch(UnsupportedEncodingException e){
      System.err.println("Error : UnsupportedEncodingException. See stack trace below."); e.printStackTrace(); System.exit(-1);
    }
  }
  
  private static double[] parseArray(String s){
    double[] ar=new double[4];
    String tmp="";
    if(!s.substring(0, 1).equals("[") || !s.substring(s.length()-1, s.length()).equals("]")){
      System.err.println("Error : the start point must be formatted with \"[\" as starting character and with \"]\" as last character.");
      System.exit(-1);
    }
    else{
      tmp=s.substring(1, s.length()-1);
      String parts[]=tmp.split(",");
      ar=new double[parts.length];
      for(int i=0; i<parts.length; i++){
        try{ar[i]=Double.parseDouble(parts[i]);}
        catch(NumberFormatException e){System.err.println("Error : one coordinate of the start point is not parseable into double."); System.exit(-1);};
      }
    }
    return(ar);
  }
  //-----------------------------------------------------------
  
  // Main method //--------------------------------------------
  public static void main(String[] args){
    // Declarations and default parameters //------------------
    boolean export=false;
    boolean startPointSpecified=false;
    int kt=0;
    int d=1;
    int n=15;
    double c=1.0;
    String exportFilename="";
    double[] startPoint=new double[1];
    //---------------------------------------------------------
    
    // Arguments parsing //------------------------------------
    Map<String, String> pMap=parseArgs(args);
    String[] parameters={"kt", "d", "c", "n", "start", "output"};
    if(pMap.containsKey("help")){ // The user needs help.
      System.out.println("Manual :\n"+
                         "> Command line :\n"+
                         ">> java -jar BrownianGenerator.jar  kt=...\n"+
                         "                                   [d=...]\n"+
                         "                                   [c=...]\n"+
                         "                                   [n=...]\n"+
                         "                                   [start=...]\n"+
                         "                                   [output=...]\n"+
                         "> Options :\n"+
                         ">> kt : wanted number of discretisation points (mandatory),\n"+
                         ">> d : wanted Brownian motion dimension (optionnal, default at 1),\n"+
                         ">> c : wanted Brownian motion length (optionnal, default at 1),\n"+
                         ">> n : wanted cutting point in the decomposition (optionnal, default at 15),\n"+
                         ">> start : starting point ([...,...,...] format, according to the wanted dimension, optionnal, default at [0]),\n"+
                         ">> output : optionnal destination file.");
      System.exit(-1);
    }
    if(!pMap.containsKey("kt")){ // essential
      System.err.println("Error : parameter kt is mandatory. Type :\n java -jar BrownianGenerator.jar help\nto show a short manual.");
      System.exit(-1);
    }
    for(String p : parameters){
      if(pMap.containsKey(p)){
        String value=pMap.get(p);
        switch(p){
          case "kt" :
            try{kt=Integer.parseInt(value);}
            catch(NumberFormatException e){System.err.println("Error : option kt is not parseable into integer."); System.exit(-1);}
            break;
          case "d" :
            try{d=Integer.parseInt(value);}
            catch(NumberFormatException e){System.err.println("Error : option d is not parseable into integer."); System.exit(-1);}
            break;
          case "c" :
            try{c=Double.parseDouble(value);}
            catch(NumberFormatException e){System.err.println("Error : option c is not parseable into double."); System.exit(-1);}
            break;
          case "n" :
            try{n=Integer.parseInt(value);}
            catch(NumberFormatException e){System.err.println("Error : option n is not parseable into integer."); System.exit(-1);}
            break;
          case "start" :
            startPoint=parseArray(value);
            startPointSpecified=true;
            System.out.println("lle");
            break;
          case "output" :
            export=true;
            exportFilename=value;
            break;
        }
      }
    }
    //---------------------------------------------------------

    // Post-verifications //-----------------------------------
    if(startPointSpecified && startPoint.length!=d){
      System.err.println("Error : starting point dimension and Brownian motion dimension must be the same (given dimensions : "+startPoint.length+" and "+d+", respectively).");
      System.exit(-1);
    }
    //---------------------------------------------------------
    
    // Generation //-------------------------------------------
    Brownian b=new Brownian(kt, d, c, n);
    if(startPointSpecified){b.changeStartPoint(startPoint);}
    //---------------------------------------------------------
    
    // Execution //--------------------------------------------
    if(export){
      exportToFile(b, exportFilename);
      System.out.println("Brownian motion successfully exported to \""+exportFilename+"\".");
    }
    else{
      System.out.println(b);
    }
    //---------------------------------------------------------
  }
  //-----------------------------------------------------------
}