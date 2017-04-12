package org.educationalProject.surfacePathfinder.timing;
/**
 * Class that is used for easy time measuring
 */
public class NanoClock {
	private long start;
	private long end;
	boolean ticked = false;
	/**
	 * starts time measuring
	 */
	public void tic(){
		start = System.nanoTime();
		ticked = true;
	}
	/**
	 * ends time measuring and prints the result. requires tic invocation.
	 * */
	public void toc() throws Exception{
		if(ticked){
			end = System.nanoTime();
			ticked = false;
			System.out.println("elapsed time: " + (end - start)/1000000000.0);
		}else{
			throw new TicTocException("no tic - no toc!");
		}
		
	}
	/**
	 * ends time measuring and returns the result. requires tic invocation.
	 * */
	public double tocd() throws Exception{
		end = System.nanoTime();
		if(ticked){
			ticked = false;
			return (end - start)/1000000000.0;
		}else{
			throw new TicTocException("no tic - no toc!");
		}
		
	}
}
