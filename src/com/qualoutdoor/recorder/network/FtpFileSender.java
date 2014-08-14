package com.qualoutdoor.recorder.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/*
 * Classe fille de Sender propre au transfert de type FTP
 * 
 * Cette classe rï¿½unit les paramï¿½tres spï¿½cifiques ï¿½ un transfert d'information de type ftp
 * et les mï¿½thodes pour rï¿½aliser ce transfert
 * 
 * Une connexion FTP ï¿½tant intimement liï¿½e au transfert d'un fichier donnï¿½, la classe FtpFileSender
 * ne prï¿½sentera que la mï¿½thode envoyerFichier. 
 * 
 * avec cette implementation il faut voir un objet FtpFileSender comme un envoi ponctuel entre un client
 * et un serveur
*/
public class FtpFileSender implements Sender{
	
	//PARAMETRES PROPRES A UNE CONNEXION FTP:
	
	private String user;//le login que doit prï¿½senter l'utilisateur au serveur
	private String password;//le mot de passe que doit prï¿½senter l'utilisateur au serveur
	private String storingPath;//le chemin de stockage du fichier dans le serveur
	
	
	public FtpFileSender(String user, String password, String storingPath) {
		this.user = user;
		this.password = password;
		this.storingPath = storingPath;
	}

	/*Implementation de la mï¿½thode envoyerFichier
	 * 
	 * les paramï¿½tres sont donc les informations necessaires communes
	 */
	@Override
	public boolean sendFile(String url,String fileName,InputStream content){
		//initialisation de la rï¿½ponse ï¿½ retourner
		boolean response;
		try {
			
	
			//mise en forme de l'url complete
			String target = "ftp://"+this.user+":"+this.password+"@"+url+this.storingPath+fileName+"FTP";
			//crï¿½ation de l'URL
			URL targetAddress = new URL(target);
			//ouverture de la connection
			URLConnection connection = targetAddress.openConnection();
			//autorisation des entrï¿½es afin d'ï¿½crire le contenu du fichier
			connection.setDoOutput(true);
			//on ne peut pas autoriser ï¿½ la fois sortie et entrï¿½e.
			
			
			//creation d'un flux d'ï¿½criture pour l'upload
			OutputStream os = connection.getOutputStream();
			
			//Mise en place d'un mï¿½canisme de lecture-ï¿½criture: on lit dans le flux passï¿½ en paramï¿½tre et on ï¿½crit dans le flux crï¿½e
			
			//buffer de transfert
			byte[] temp = new byte[1024];
			//indicateur permettant de savoir si la lecture du fichier est terminï¿½e
			int indic;
			while((indic = content.read(temp)) != -1){//on lit le flux entrant dans le buffer
				os.write(temp, 0, indic);//on ï¿½crit le buffer dans le flux sortant
			}
			
			/*une fois le fichier envoyï¿½ on rï¿½cupere la rï¿½ponse
			 * or ici il ya un problï¿½me avec les sorties de la connexion
			 * on utilise donc une rï¿½ponse gï¿½nï¿½rï¿½e en local : 
			 * si aucune exception a ï¿½tï¿½ levï¿½ lors de la suite d'instruction le transfert s'est bien passï¿½
			 * (solution optimiste)
			 */
			//le transfert s'est bien passé
        	response = true;
        	os.close();
        	
        	//dans les cas ou il y a eu une exception levï¿½e on l'indique dans la phrase de retour
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			response = false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			response = false;
		}
		//ï¿½ la fin, dans chaque cas on renvoie la phrase de retour
		return response;
		
	
	}
	
	
	
	
	
}
