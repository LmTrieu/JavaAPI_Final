package com.example;

@SuppressWarnings("serial")
public class DatabaseActionException extends RuntimeException {
	  public DatabaseActionException(final Throwable cause) {
	    super(cause);
	  }
}