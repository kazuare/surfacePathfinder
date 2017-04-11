package org.educationalProject.surfacePathfinder;
/**
 * Exception class for NanoClock 
 * */
public class TicTocException extends Exception{
	 public TicTocException() { super(); }
	 public TicTocException(String message) { super(message); }
	 public TicTocException(String message, Throwable cause) { super(message, cause); }
	 public TicTocException(Throwable cause) { super(cause); }
}