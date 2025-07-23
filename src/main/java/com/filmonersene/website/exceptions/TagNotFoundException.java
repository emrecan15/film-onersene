package com.filmonersene.website.exceptions;

public class TagNotFoundException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -493767641391982636L;

	public TagNotFoundException(String message)
	{
		super(message);
	}
}
