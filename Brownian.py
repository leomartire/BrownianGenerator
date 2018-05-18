# Author:        Valentin Debarnot, Léo Martire.
# Mail:          leo.martire@outlook.com
# Description:   TODO.
# Last modified: See file metadata.
# Usage:         N/A.
# Notes:         TODO.

# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
plt.close('all')

# Triangle functions generator ################################
def Triangle_Generator(n, k, nbPoint):
    # @param n index j of the desired triangle function
    #          tau_{j, k}
    # @param k index k of the desired triangle function
    #          tau_{j, k}
    # @param nbPoint number of points of the discretization
    # @return vect the approximation of the desired triangle
    #              function
    vect=np.zeros(nbPoint)
    if(n==0 and k==0):
        # Base triangle function: height 1, centered on 0.5, ##
        #                         width 0.5.
        debut=np.floor(0.25*nbPoint)
        fin=np.floor(0.75*nbPoint)
        milieu=np.floor((debut+fin)*0.5)
        vect[debut:milieu]=(np.arange(debut, milieu, 1)-debut)/(debut)
        vect[milieu]=1
        vect[milieu+1:fin+1]=((np.ones(fin-milieu)-np.arange(milieu+2, fin+2, 1))+fin)/(debut)
        return vect
    else:
        # Adjustement of length and centering #################
        debut=np.maximum(np.floor((2*k)/(2.**(n+1))*nbPoint), 1)
        fin=np.maximum(np.floor((2*k+2)/(2.**(n+1))*nbPoint), 1)
        milieu=np.floor((debut+fin)*0.5)
        vect[debut:milieu+1]=(np.arange(debut, milieu+1, 1)-debut)/np.maximum((milieu-debut), 1)
        vect[milieu:fin]=((np.ones(fin-milieu)-np.arange(milieu+1, fin+1, 1))+fin-1)/np.maximum((milieu-debut), 1)
        # Adjustement of height ###############################
        vect=vect*2**(-n*0.5-1)        
        return vect
###############################################################
        
# 1-d Brownian motion #########################################
def Brownian(nbPoint):
    # @param nbPoint number of points of the discretization
    # @return B the approximation of the 1-d Brownian motion
    N=15 # sum troncature
    gauss=np.random.randn(N+1, 2**N-1)
    gauss_0=np.random.randn(1)
    temps=np.linspace(0, 1, nbPoint)
    B=np.zeros(nbPoint)
    dblSum=np.zeros(nbPoint)
    for n in range(0, N):
        for k in range(1, 2**n-1):
            dblSum=dblSum+Triangle_Generator(n, k, nbPoint)*gauss[n, k]
    B=temps*gauss_0+dblSum
    return B
###############################################################

# 2-d Brownian motion #########################################
def Brownian2D(nbPoint):
    # @param nbPoint number of points of the discretization
    # @return B the approximation of the 1-d Brownian motion
    B=np.zeros((nbPoint, 2))
    B[:, 0]=Brownian(nbPoint)
    B[:, 1]=Brownian(nbPoint)
    return B
###############################################################

# Main program ################################################

plt.close('all')
nbPoint=1000 # number of points of the discretization
k=6 # for case 2 : the number of different Brownian motions to plot

case=5
# 1: draw triangle functions examples
# 2: draw 1-d Brownian motions
# 3: draw a 2-d Brownian motion
# 4: draw the error (in infinite norm) comitted on the
#    approximation of the triangle functions
# 5: draw illustrations

if case==1:
    x=np.linspace(0, 1, nbPoint)
    f, (ax0, ax1, ax2, ax3)=plt.subplots(4, sharex=True, sharey=True)
    v=Triangle_Generator(2, 0, nbPoint)
    ax0.plot(x, v)
    ax0.set_title('n=2, k=0')
    v=Triangle_Generator(2, 1, nbPoint)
    ax1.plot(x, v, color='r')
    ax1.set_title('n=2, k=1')
    v=Triangle_Generator(4, 1, nbPoint)
    ax2.plot(x, v, color='g')
    ax2.set_title('n=4, k=1')
    v=Triangle_Generator(5, 1, nbPoint)
    ax3.plot(x, v, color='k')
    ax3.set_title('n=5, k=1')
    f.subplots_adjust(hspace=0.2)
    plt.setp([a.get_xticklabels() for a in f.axes[:-1]], visible=False)
    
if case==2:
    x=np.linspace(0, 1, nbPoint)
    for i in range(1, k+1):
        B=Brownian(nbPoint)
        plt.plot(x, B)
    #plt.title('Example of '+str(k)+' Brownian motions')
    plt.title('Example of a Brownian motion')
    plt.xlabel('t')
    plt.ylabel('B(t)')
        
if case==3:
    B=Brownian2D(nbPoint)
    plt.plot(B[:, 0], B[:, 1])
    plt.title('Example of a 2-d Brownian motion')
    plt.xlabel('x')
    plt.ylabel('y')
    
if case==4:
    N=15
    w=0
    norme_inf=np.zeros(((N+1)*2**N, 1))
    for n in range(0, N):
        for k in range(1, 2**n-1):
            w=w+1
            norme_inf[w]=np.max(Triangle_Generator(n, k, nbPoint))
            if norme_inf[w]<10**-8:
                break
    x=np.linspace(1, w, w)
    plt.plot(x, norme_inf[:w])

if case==5:
    k=6
    nbPoint=1000
    x=np.linspace(0, 1, nbPoint)
    plt.figure()
    for i in range(1, k+1):
        B=Brownian(nbPoint)
        plt.plot(x, B)
    plt.title('Example of '+str(k)+' Brownian motions')
    plt.xlabel('t')
    plt.ylabel('B(t)')
    
    plt.figure()
    B=Brownian2D(nbPoint)
    plt.plot(B[:, 0], B[:, 1])
    plt.title('Example of a 2-d Brownian motion')
    plt.xlabel('x')
    plt.ylabel('y')
    
    nbPoint=100
    x=np.linspace(0, 1, nbPoint)
    plt.figure()
    B=Brownian(nbPoint)
    plt.plot(x, B)
    plt.title('Example of a 1-d \'raw\' Brownian motion')
    plt.xlabel('t')
    plt.ylabel('B(t)')
    plt.figure()
    B=Brownian2D(nbPoint)
    plt.plot(B[:, 0], B[:, 1])
    plt.title('Example of a 2-d \'raw\'Brownian motion')
    plt.xlabel('x')
    plt.ylabel('y')
    
    nbPoint=10000
    x=np.linspace(0, 1, nbPoint)
    plt.figure()
    B=Brownian(nbPoint)
    plt.plot(x, B)
    plt.title('Example of a 1-d refined Brownian motion')
    plt.xlabel('t')
    plt.ylabel('B(t)')
    plt.figure()
    B=Brownian2D(nbPoint)
    plt.plot(B[:, 0], B[:, 1])
    plt.title('Example of a 2-d refined Brownian motion')
    plt.xlabel('x')
    plt.ylabel('y')

plt.show()
