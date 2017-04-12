package org.educationalProject.surfacePathfinder.timing;
/**
 * Exception class for NanoClock 
 * */
public class TicTocException extends Exception{
	private static final long serialVersionUID = 1L;
	public TicTocException() { super(); }
	 public TicTocException(String message) { super(message); }
	 public TicTocException(String message, Throwable cause) { super(message, cause); }
	 public TicTocException(Throwable cause) { super(cause); }
}
