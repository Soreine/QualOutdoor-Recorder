package com.qualoutdoor.recorder.persistent;

import java.io.ByteArrayOutputStream;

/*Classe qui permet un call back des taches asynchrones vers l'activit� principale
 * main activity implementera la methode onTaskCompleted, les DataSendingManager
 * auront donc comme param�tre en plus un objet de type OnTaskListener et appelleront
 * la methode onTaskCompleted dans leur m�thode OnPostExecute */

public interface FileReadyListener {

	/*Callback pour le g�n�rateur de fichier*/
	void onFileReady(ByteArrayOutputStream file);
}
