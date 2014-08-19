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
 * Cette classe r�unit les param�tres sp�cifiques � un transfert d'information de type ftp
 * et les m�thodes pour r�aliser ce transfert
 * 
 * Une connexion FTP �tant intimement li�e au transfert d'un fichier donn�, la classe FtpFileSender
 * ne pr�sentera que la m�thode envoyerFichier. 
 * 
 * avec cette implementation il faut voir un objet FtpFileSender comme un envoi ponctuel entre un client
 * et un serveur
*/
public class FtpFileSender implements Sender{
	
	//PARAMETRES PROPRES A UNE CONNEXION FTP:
	
	private String user;//le login que doit pr�senter l'utilisateur au serveur
	private String password;//le mot de passe que doit pr�senter l'utilisateur au serveur
	private String storingPath;//le chemin de stockage du fichier dans le serveur
	
	
	public FtpFileSender(String user, String password, String storingPath) {
		this.user = user;
		this.password = password;
		this.storingPath = storingPath;
	}

	/*Implementation de la m�thode envoyerFichier
	 * 
	 * les param�tres sont donc les informations necessaires communes
	 */
	@Override
	public boolean sendFile(String url,String fileName,InputStream content){
		//initialisation de la r�ponse � retourner
		boolean response;
		try {
			
	
			//mise en forme de l'url complete
			String target = "ftp://"+this.user+":"+this.password+"@"+url+this.storingPath+fileName+"FTP";
			//cr�ation de l'URL
			URL targetAddress = new URL(target);
			//ouverture de la connection
			URLConnection connection = targetAddress.openConnection();
			//autorisation des entr�es afin d'�crire le contenu du fichier
			connection.setDoOutput(true);
			//on ne peut pas autoriser � la fois sortie et entr�e.
			
			
			//creation d'un flux d'�criture pour l'upload
			OutputStream os = connection.getOutputStream();
			
			//Mise en place d'un m�canisme de lecture-�criture: on lit dans le flux pass� en param�tre et on �crit dans le flux cr�e
			
			//buffer de transfert
			byte[] temp = new byte[1024];
			//indicateur permettant de savoir si la lecture du fichier est termin�e
			int indic;
			while((indic = content.read(temp)) != -1){//on lit le flux entrant dans le buffer
				os.write(temp, 0, indic);//on �crit le buffer dans le flux sortant
			}
			
			/*une fois le fichier envoy� on r�cupere la r�ponse
			 * or ici il ya un probl�me avec les sorties de la connexion
			 * on utilise donc une r�ponse g�n�r�e en local : 
			 * si aucune exception a �t� lev� lors de la suite d'instruction le transfert s'est bien pass�
			 * (solution optimiste)
			 */
			//le transfert s'est bien pass�
        	response = true;
        	os.close();
        	
        	//dans les cas ou il y a eu une exception lev�e on l'indique dans la phrase de retour
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			response = false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			response = false;
		}
		//� la fin, dans chaque cas on renvoie la phrase de retour
		return response;
		
	
	}
	
	
	
	
	
}
