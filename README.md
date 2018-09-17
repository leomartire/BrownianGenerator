# BrownianGenerator
A Java module for the generation of multidimensional Brownian motions.

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1420230.svg)](https://doi.org/10.5281/zenodo.1420230)

## Theory
The theory behind this module can be found in the PDF report `Decomposing_the_standard_Brownian_Motion_along_the_non_normalized_Faber_Schauder_System.pdf`.

##  Usage
This Java Toolbox is packed in a .jar archive.

### Package Usage
The source classes can be copied besides any other source code, and compiled alongside them. This enables one to call the methods directly in its program.

### Command Line Usage
Java is needed in order to execute the .jar directly, the command line format being the following:
```
java -jar BrownianGenerator.jar kt=... [d=...] [c=...] [n=...] [start=...] [output=...]
```
Where option names are directly linked to the degrees of freedom detailed below:
- `kt`: number of discretisation points for the time interval (mandatory),
- `d`: dimension of the wanted Brownian motion (optionnal, default at 1),
- `c`: length of the wanted Brownian (optionnal, default at 1),
- `N`: cut parameter (see report, optionnal, default at 15),
- `output`: output file (optionnal, default is the standard output).

If an output file is specified for a default 1000-point 2-D Brownian motion will start like so:
```
kt=1000; d=2; c=1.0; n=15; startPoint=[0.0, 0.0]
0.0000000000000000e+00  0.0000000000000000e+00
3.8088021391987150e-02 -2.9328389173551024e-02
7.5191443043892860e-02 -5.5503525835590165e-02
9.3114861759012070e-02 -2.0253303573377730e-02
```

#### Examples of Command Line Calls :
```
java -jar BrownianGenerator.jar kt=10
java -jar BrownianGenerator.jar kt=10 d=2
java -jar BrownianGenerator.jar kt=10 c=14.5
java -jar BrownianGenerator.jar kt=10 n=5
java -jar BrownianGenerator.jar kt=10 start=[1]
java -jar BrownianGenerator.jar kt=10 d=2 start=[1,1]
java -jar BrownianGenerator.jar kt=10 start=[10.45]
java -jar BrownianGenerator.jar kt=10 d=3 start=[10.8,4.6,1.4e6]
java -jar BrownianGenerator.jar kt=10 d=2 output=test.txt
java -jar BrownianGenerator.jar kt=10 d=2 c=10 n=7 start=[0.0,1.47] output=brownian.txt
```
