package com.qualoutdoor.recorder.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.util.Log;

/*
 * Classe fille de Sender propre au transfert de type HTTP
 * 
 * Cette classe r�unit les param�tres sp�cifiques � un transfert d'information de type http
 * et les m�thodes pour r�aliser ce transfert
 * 
 * Une connexion HTTP est ind�pendante des transferts qu'elle met en oeuvre ainsi on d�finit
 * diff�rentes m�thodes correspondant � des actions �l�mentaires qui seront ensuites reprises
 * dans la fonction envoyerFichier
*/

public class HttpFileSender implements Sender{
	//PARAMETRES PROPRES A UNE CONNEXION HTTP:
	
	private String fileFieldName;//nom du champs auquel se rapporte le fichier transferer
	/*La pr�sence de ce param�tre peut s'averer problematique en effet elle cr�e une relation
	 * �troite entre l'envoi d'un fichier particulier et une instance de HttpFileSender
	 * alors qu'une m�me instance pourrait �tre utilis� pour l'envoi de plusieurs fichiers
	 * 
	 *  La valeur du champs n'a pas une importance consid�rable, ainsi dans le cas o� l'on souhairait
	 *  mettre fin � cette d�pendance on supprimera cet attribut et on laissera � la fonction UploadFile
	 *  le soin de fixer le nom du champ
	 */
	
	/*ATTENTION: ces param�tres ne sont pas sp�cifique aux transferts http mais ils permettent d'associer
	 * une instance HttpFileSender avec une communication donn�e et de limiter le nombre d'arguments 
	 * des fonctions de la classe
	 * 
	 * Avec cette implementation il faut voir une instance comme un dialogue entre client et serveur
	 * dans lequel circulent plusieurs types de donn�es*/	
	private HttpURLConnection connection;//la connexion correspondant au dialogue
	private String delimiter;//chaine de caract�re delimitant les entr�es du formulaire
	private OutputStream os;//output stream permettant d'�crire dans le contenu de la requete
	private PrintWriter writer;//outil issu de os qui va nous permettre d'ecrire des octets dans la requete

	/*Constructeur pertinant si on associe la connexion � un envoi simple d'un fichier
	 *Les autres attributs sont fix�s avec la m�thode initialize.
	 */
	public HttpFileSender(String fileFieldName) {
		this.fileFieldName = fileFieldName;//on fixe donc le nom du champs correspond au fichier � envoyer
	}
	
	
	/*Implementation de la m�thode envoyerFichier
	 * 
	 * les param�tres sont donc les informations necessaires communes
	 * la m�thode fera donc appel aux fonctions d�finies si dessous.
	 * 
	 * 
	 */
	@Override
	public String envoyerFichier(String url,String fileName,InputStream content){
		try{
			this.initialize(url);
			this.UploadTextFile(this.fileFieldName,fileName,content);
			this.endSending();
			return this.readResponseStatus();
		}catch(HttpTransfertException e){
			return "An exception occured :"+e.toString() ;
		}
	}
	
	
	public void initialize(String url) throws HttpTransfertException{
	/*fonction qui permet l'initialisation d'une requete HTTP post � partir d'une URL	
	 *cette fonction permet de construire l'entete de la requete HTTP	
	*/
		try{
			Log.d("DEBUG","...begining initialisation");
			//on construit l'url � partir de l'adresse pass�e en param�tre
			URL targetAddress = new URL(url);
			//ouverture de la connection vers l'addresse indiqu�e
			this.connection = (HttpURLConnection) targetAddress.openConnection();
			//on pr�cise le type de la requ�te
			this.connection.setRequestMethod("POST");
			//on autorise les flux entrants dans la connexion: pour pouvoir �crire les informations
			this.connection.setDoInput(true);
			//on autotise les flux sortant de la connexion: pour pouvoir lire les informations
			this.connection.setDoOutput(true);
			//on rend la connection TCP sous jacente persistente
			this.connection.setRequestProperty("Connection", "Keep-Alive");
			//on g�n�re un d�limiteur
			this.delimiter = "******"+Long.toString(System.currentTimeMillis())+"******";
			//on pr�cise le format de la parti� donn�es de la requete
			this.connection.setRequestProperty("Content-type", "multipart/form-data; boundary="+this.delimiter);
			//on �tablit la connexion
			this.connection.connect();
			//on cr�e un acces pour �crire dans la requ�te via un output stream
			this.os = connection.getOutputStream();
			//on d�finit le writer associ� � l'output stream qui permettra d'�crire les octects dans le flux
		    this.writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		    Log.d("DEBUG","...initialisation complete...trying to send post request to"+url);	
		    
		}catch(MalformedURLException e){
			Log.d("URL exception",e.toString());
			e.printStackTrace();
			throw new HttpTransfertException("URL exception"+e.toString());
		}catch(IOException e){
			Log.d("initialize IO Exception", e.toString());
			e.printStackTrace();
			throw new HttpTransfertException("initialize IO Exception :"+e.toString());
		}	
	}
	
	public void SendSimpleInput(String fieldName, String val){
		/*cette fonction permet d'inserer un champ �l�mentaire dans le formulaire � envoyer
		 *un input fieldName sera donc cr�e, comportant la valeur val
		 *cette fonction ne sera appel� que sur un sender initialis�.
		*/
			Log.d("DEBUG","...trying to write input" + fieldName +" in request");
			//on indique une nouvelle entr�e du formulaire dans la requete avec une limite
			this.writer.append("--"+this.delimiter+"\r\n");
			//on indique une entr�e du formulaire se rapportant au champ s'appelant fieldName
			this.writer.append("Content-Disposition: form-data; name=\""+fieldName+"\"\r\n");
			//on indique le format de la valeur du champ : ici texte encod� avec UTF8
			this.writer.append("Content-Type: text/plain; charset=UTF-8\r\n");
			//on indique la valeur que l'on affecte au champ
			this.writer.append("\r\n" + val + "\r\n");
			//on �crit le flux dans la requete
			this.writer.flush();
			Log.d("DEBUG","...input writen");
		
	}
	
	public void UploadTextFile(String fieldName, String fileName,InputStream content)throws HttpTransfertException{
		/*cette fonction permet d'inserer un fichier texte dans le formulaire � envoyer
		 *un input appel� filedName sera cr�e et affect� � un fichier fileName dont le contenu 
		 *sera r�cupr�rer par la lecture de content
		 *cette fonction ne sera appel� que sur un sender initialis�.
		*/
		try{
			Log.d("DEBUG","...trying to write file" + fileName +" in request");
			Log.d("DEBUG","delimiter: "+this.delimiter);
			//on indique une nouvelle entr�e du formulaire dans la requete avec une limite
			this.writer.append("--"+this.delimiter+"\r\n");
			//on indique une entr�e du formulaire se rapportant au champ s'appelant nomChamp
			this.writer.append("Content-Disposition: form-data; name=\"" + fieldName+ "\"; filename=\"" + fileName+ "\"\r\n");
			//on indique que le contenu correspond � du texte
			this.writer.append("Content-Type: text/plain\r\n");
			//on indique comment sera encod� le texte pour la transmission
			this.writer.append("Content-Transfer-Encoding: binary\r\n");
			this.writer.append("\r\n");
			//on �crit le flux dans la requete
			this.writer.flush();
		
			/*il faut maintenant remplir la requ�te avec le contenu du fichier
			 *on met en place un syst�me de lecture �criture
			 *on definit un buffer intermediaire pour transvaser les donn�es
			 */
			byte[] temp = new byte[1024];
			//indicateur permettant de savoir si la lecture du fichier est termin�e
			int indic;
			while((indic = content.read(temp)) != -1){//on lit le flux entrant dans le buffer
				os.write(temp, 0, indic);//on �crit le buffer dans le flux sortant
			}
			
			this.writer.append("\r\n");		
		}catch(IOException e){
			Log.d("Uploading text IO Exception", e.toString());
			e.printStackTrace();
			throw new HttpTransfertException("Uploading text reading-writing mecanisme exception :"+e.toString());
		}	
		
	}
	
	public void endSending()throws HttpTransfertException{
		/*fonction qui permet de clore le champ data de la requete
		 * une fois que tous les champs ont �t� inscrits dedans
		 * on aura donc achev� de remplir la requ�te qui sera donc 
		 * communiqu�e � l'adresse cible. 
		 */
		try{
			
			Log.d("DEBUG","...ending request");
			//on inscrit le d�limiteur final dans la requete. cette ligne signifie au server la fin de la requete
			this.writer.append("--"+this.delimiter+"--"+"\r\n");
			//on �crit le flux dans la requete
			this.writer.flush();	
			//on ferme tous les outils d'�criture
			this.writer.close();
			this.os.close();
			Log.d("DEBUG","...request ended");		
		}catch(IOException e){
			Log.d("end sending IO Exception", e.toString());
			e.printStackTrace();
			throw new HttpTransfertException("end sending closing outputstream exception :"+e.toString());
			
		}	
		
	}
	
	public String readResponseStatus() throws HttpTransfertException {
		/*fonction qui permet de lire le code r�ponse du serveur apr�s qu'on 
		 * lui ai adress� une requete, cette m�thode doit donc etre appel�
		 * apr�s avoir termin� l'�criture de la requete (apr�s un endSending).
		 */
		int response = 0;
		String response2="";
		try{
			Log.d("DEBUG","...trying to read status");
			//on r�cupere le code retour envoy� par le serveur 
			response = this.connection.getResponseCode();
			//on r�cup�re la r�ponse envoy� par le serveur:
			//on commence par ouvrir un flux de lecture
			InputStream fluxRetour = this.connection.getInputStream();
			//mise en place d'un m�canisme de lecture-ecriture
        	int retourLecture2;	        	
        	StringBuffer buffRecep = new StringBuffer();
        	while((retourLecture2 = fluxRetour.read())!=-1){
        		buffRecep.append((char)retourLecture2);
        	}
        	//on stocke la r�ponse lue
        	response2 = buffRecep.toString();
        	Log.d("reponse","reponse du serveur:"+response2);
			Log.d("DEBUG","...status read, end of transmission, status"+String.valueOf(response));
			//Si une exception est lev�e on g�n�re un r�ponse en locale
		}catch(IOException e){
			Log.d("reading  IO Exception, returned response equals to 0 ", e.toString());
			e.printStackTrace();
			throw new HttpTransfertException("readin server response exception :"+e.toString());
		}
		//renvoi de la r�ponse
		return response2 ;
		
	}
	
	
	
	
	
	

}
