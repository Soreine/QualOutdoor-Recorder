package com.qualoutdoor.recorder.persistent;

/*
 * Exception lanc�e si un probl�me est rencontr� lors de la collecte des donn�es 
 * � inserer dans la base de donn�es
 * */
public class CollectMeasureException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public CollectMeasureException(String message) {
        super(message);
  }

}
