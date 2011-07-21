/*
 * ArithmeticBenchmarkingForm.java
 *
 * Created on 27 de agosto de 2004, 2:55
 */

package com.grimo.me.product.midpsysinfo;


import java.util.Random;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.StringItem;

import de.enough.sysinfo.MIDPSysInfoMIDlet;

/**
 * Benchmarks the device using different access strategies.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <j2mepolish@enough.de> (architectural changes)
 */
public class ArithmeticBenchmarkInfoCollector 
extends InfoCollector 
implements Runnable
{    
    
    private final static int NUMBER_OF_OPS = 100 * 1000;
    
    private int arrayA[];
    private int arrayB[];
    
    private static int staticA;
    private static int staticB;
    
    private int instanceA;
    private int instanceB;
    
    private final Random random = new Random();

	private boolean isFinished;
    
    
    public ArithmeticBenchmarkInfoCollector(){
        
        do {
            this.instanceA = this.random.nextInt();
        } while (this.instanceA == 0);
        do {
            this.instanceB = this.random.nextInt();
        } while (this.instanceB == 0);
        do {
            staticA = this.random.nextInt();
        } while (staticA == 0);
        do {
            staticB = this.random.nextInt();
        } while (staticB == 0);
        
        this.arrayA = new int[100];
        this.arrayB = new int[100];
        Random r = new Random();
        for ( int i = 0; i < 100; i++){
            do {
                this.arrayA[i] = r.nextInt();
            } while ( this.arrayA[i] == 0 );
            do {
                this.arrayB[i] = r.nextInt();
            } while ( this.arrayB[i] == 0 );
        }
    }
    
    private void performAdditionBenchmark(){
        long before;
        long after;
        int times = NUMBER_OF_OPS * 100;
        addInfo("Addition: ", "adding " + times + " integers");
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < NUMBER_OF_OPS; i++){
            for ( int j = 0; j < 100; j++){
                int result = this.arrayA[j] + this.arrayB[j];
            }
        }
        after = System.currentTimeMillis();
        long elapsedArray = after - before;
        addInfo("instance array: ", elapsedArray  + " ms" );
        
        
        int localA = this.random.nextInt();
        int localB = this.random.nextInt();
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = localA + localB;
        }
        after = System.currentTimeMillis();
        long elapsedLocal = after - before;
        addInfo("local: ", elapsedLocal + " ms" );
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = this.instanceA + this.instanceB;
        }
        after = System.currentTimeMillis();
        long elapsedInstance = after - before;
        addInfo("instance: ",  elapsedInstance + " ms" );
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = staticA + staticB;
        }
        after = System.currentTimeMillis();
        long elapsedStatic = after - before;
        addInfo("static: ", elapsedStatic + " ms" );
    }
    
    private void performMultiplicationBenchmark(){
  
        long before;
        long after;
        int times = NUMBER_OF_OPS * 100;
        addInfo("Muliplication: ", "multiplying " + times + " integers");
        
        before = System.currentTimeMillis();     
        for ( int i = 0; i < NUMBER_OF_OPS; i++){
            for ( int j = 0; j < 100; j++){
                int result = this.arrayA[j] * this.arrayB[j];
            }
        }
        after = System.currentTimeMillis();
        long elapsedArray = after - before;
        addInfo("instance array: ", elapsedArray + " ms" );
        
        int localA = this.random.nextInt();
        int localB = this.random.nextInt();
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = localA * localB;
        }
        after = System.currentTimeMillis();
        long elapsedLocal = after - before;
        addInfo("local: ", elapsedLocal + " ms" );
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = this.instanceA * this.instanceB;
        }
        after = System.currentTimeMillis();
        long elapsedInstance = after - before;
        addInfo("instance: ",  elapsedInstance + " ms" );
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = staticA * staticB;
        }
        after = System.currentTimeMillis();
        long elapsedStatic = after - before;        
        addInfo("static: ", elapsedStatic + " ms" );
    }
    
    private void performDivisionBenchmark(){
        
        
        long before;
        long after;
        int times = NUMBER_OF_OPS * 100;
        addInfo("Division: ", "dividing " + times + " integers");
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < NUMBER_OF_OPS; i++){
            for ( int j = 0; j < 100; j++){
                int result = this.arrayA[j] / this.arrayB[j];
            }
        }
        after = System.currentTimeMillis();
        long elapsedArray = after - before;
        addInfo("instance array: ", elapsedArray + " ms" );
        
        int localA, localB;
        do {
            localA = this.random.nextInt();
        } while (localA == 0);
        do {
            localB = this.random.nextInt();
        } while (localB == 0);
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = localA / localB;
        }
        after = System.currentTimeMillis();
        long elapsedLocal = after - before;
        addInfo("local: ", elapsedLocal + " ms" );
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = this.instanceA / this.instanceB;
        }
        after = System.currentTimeMillis();
        long elapsedInstance = after - before;
        addInfo("instance: ", elapsedInstance + " ms" );
        
        before = System.currentTimeMillis();
        for ( int i = 0; i < times; i++){
            int result = staticA / staticB;
        }
        after = System.currentTimeMillis();
        long elapsedStatic = after - before;
        addInfo("static: ", elapsedStatic + " ms" );
    }
    
        

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(com.grimo.me.product.midpsysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display display) {
		addInfo( "Status: ", "benchmarking...");
		Thread thread = new Thread( this );
		thread.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Thread.sleep( 500 );
		} catch (InterruptedException e) {
			// ignore
		}
		performAdditionBenchmark();
		performDivisionBenchmark();
		performMultiplicationBenchmark();
		if (this.view != null) {
			this.view.set(0, new StringItem("Status: ", "Done.") );
		}
		this.isFinished = true;
	}
	
	public boolean isFinished() {
		return this.isFinished;
	}

}
