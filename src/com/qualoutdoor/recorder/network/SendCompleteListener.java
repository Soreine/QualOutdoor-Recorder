package com.qualoutdoor.recorder.network;

import java.util.HashMap;

/*Classe qui permet un call back des taches asynchrones vers l'activit� principale
 * main activity implementera la methode onTaskCompleted, les DataSendingManager
 * auront donc comme param�tre en plus un objet de type OnTaskListener et appelleront
 * la methode onTaskCompleted dans leur m�thode OnPostExecute */

public interface SendCompleteListener {
	/*
	 * le sender va rappeler sa classe appelante en indiquant son protocole d'envoi,
	 * la liste des fichiers qu'il a envoy� et un boolean indiquant si l'envoi s'est
	 * bien pass� ou non
	 * */
	
	void onTaskCompleted(String protocole,HashMap<String,FileToUpload> filesSended, boolean result);

}
