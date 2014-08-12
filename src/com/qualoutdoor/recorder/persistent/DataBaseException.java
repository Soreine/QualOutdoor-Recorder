package com.qualoutdoor.recorder.persistent;

/*Exeption lanc�e si un probleme dans la manipulation de la base de donn�e est rencontr�*/
public class DataBaseException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DataBaseException(String message) {
        super(message);
  }

}
