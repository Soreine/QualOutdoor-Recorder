package com.qualoutdoor.recorder.network;

import java.io.InputStream;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

/*Ce script ne mettra pas en oeuvre des politiques complexes d'ordonnancement
 * on utilisera donc simplement une classe asynchrone dans laquelle on 
 * inserera les m�thodes relatives � la transmission de donn�es
 */
public class DataSendingManager extends AsyncTask<Void, Void, Boolean> {

	/*Un DataSendingManager permet d'initialiser des connections de diff�rents types selon
	 * les param�tres avec lesquels il a �t� initialis� et de les utiliser pour envoyer
	 * des requ�tes. 
	 * 
	 * Il est donc d�fini avec des attributs qui seront tous les param�tres COMMUNS des diff�rents 
	 * type de connexion : une analogie un peu tordue peut �tre que ces attributs sont le "PGCD" de l'ensemble 
	 * des parem�tres necessaires pour chaque connexion :). 
	 * 
	 */	
	private String target;//l'addresse du serveur cible
	private HashMap<String,FileToUpload>  filesToUploadlist;//les fichiers � envoyers
	private String protocole;//le protocole � utiliser
	//Ces param�tres r�pr�sentent donc tous les param�tres communs aux diff�rents types de connexions
	
	private SendCompleteListener callback;//l'objet sur lequel appliquer la methode de callbak une fois la tache termin�e
	
	private ProgressDialog progressDialog;	
	
	//Constructeur avec lequel on intitalise le DataSendingManager
	public DataSendingManager(String url, HashMap<String,FileToUpload> filesToUpload,String proto,SendCompleteListener cb){
		this.target=url;
		this.filesToUploadlist=filesToUpload;
		this.protocole=proto;
		this.callback=cb;
	}
	
	/*Fonction qui prend un temps relativement long par rapport au reste du code, on l'execute donc en arri�re plan
	 * cette fonction ouvre les connection, effectue le transfert et retourne la r�ponse */
    protected Boolean doInBackground(Void... params) {
    	/*IMPORTANT: afin de factoriser le code une fonction commmune � tous les sender � �t� cr��e
    	 * et permet d'envoyer un fichier or il existe des connexions qui peuvent supporter un envoie de fichier
    	 * multiples comme les connexions http mais il existes aussi des connexion qui dont l'existence est fond�e 
    	 * sur le tranfert d'un et un seul fichier comme les connexions ftp. Ainsi cette m�thode commune d'envoi
    	 * de fichier permettra l'envoi d'un fichier unique */
    	
    	//On se place dans le cas ou un seul fichier est dans la hashmap:
    	
    		//on r�cupere donc la premi�re entr�e de la hashmap
    		String cle = this.filesToUploadlist.keySet().iterator().next();
    		//on r�cup�re le nom du fichier
    		String fileName = this.filesToUploadlist.get(cle).getFileName();
    		//on r�cup�re le contenu du fichier
    		InputStream content = this.filesToUploadlist.get(cle).getContent();
    		//nouveaux sender
    		Sender sender = null;
    		
    		//Si le manager a �t� initialis� avec HTTP: on fera pointer la poign�e vers un HTTPsender
    		if(this.protocole.equals("http")){
    			/*ici comme tous les param�tres communs des connexions ont d�j� �t� fix�
    			*les Sender sont initialis�s avec seulement les param�tres qui leurs sont sp�cifiques.
    			*
    			*ICI le seul param�tre sp�cifique � HTTP est le nom de l'entr�e du formulaire � laquelle
    			*se rapporte le fichier: que l'on fixe donc (sans avis de l'utilisateur)
    			*/
    		 sender = new HttpFileSender("uploadedFile");
    		}
    		//Si le manager a �t� initialis� avec FTP: on fera pointer la poign�e vers un FTPsender
    		else if(this.protocole.equals("ftp")){
    			/*
    			 * idem:
    			 * 
    			 * ICI les param�tres sp�cifiques � FTP sont le login et le mot de passe � pr�senter 
    			 * au serveur et le chemin de stockage dans le repertoire du serveur. L� aussi ce
    			 * n'est pas l'utilisateur qui les choisit
    			*/
    		 sender = new FtpFileSender("client", "alsett", "/myUploads/");
    		}
    		
    		/*une fois le sender cr�� on peut donc appeler la m�thode envoyerFichier qui comporte
    		*comme param�tres les attributs avec lesquells l'instance dataSendingManager a �t� cr�e
    		*ce sont donc les param�tres communs � toute connexion:
    		*
    		*on peut donc appeler cette methode sans savoir sur quel type de Sender on l'applique.
    		*/
    		boolean result = sender.envoyerFichier(this.target, fileName, content);
    		//envoyer fichier renvoie la r�ponse du serveur, on transfert cette derniere � la fonction de postexecution
    		return result;

    }

    

    /*
     * Fonction execut�e apr�s la fonction d'arri�re plan, elle r�cup�re la
     * r�ponse du serveur et l'affiche dans la vue avec laquelle le
     * dataSendingManager a �t� cr��.
     */
    @Override

    protected void onPostExecute(Boolean result) {
        this.callback.onTaskCompleted(this.protocole, this.filesToUploadlist,result);

    }

}
