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
 * Cette classe rï¿½unit les paramï¿½tres spï¿½cifiques ï¿½ un transfert d'information de type http
 * et les mï¿½thodes pour rï¿½aliser ce transfert
 * 
 * Une connexion HTTP est indï¿½pendante des transferts qu'elle met en oeuvre ainsi on dï¿½finit
 * diffï¿½rentes mï¿½thodes correspondant ï¿½ des actions ï¿½lï¿½mentaires qui seront ensuites reprises
 * dans la fonction envoyerFichier
*/

public class HttpFileSender implements Sender{
	//PARAMETRES PROPRES A UNE CONNEXION HTTP:
	
	private String fileFieldName;//nom du champs auquel se rapporte le fichier transferer
	/*La prï¿½sence de ce paramï¿½tre peut s'averer problematique en effet elle crï¿½e une relation
	 * ï¿½troite entre l'envoi d'un fichier particulier et une instance de HttpFileSender
	 * alors qu'une mï¿½me instance pourrait ï¿½tre utilisï¿½ pour l'envoi de plusieurs fichiers
	 * 
	 *  La valeur du champs n'a pas une importance considï¿½rable, ainsi dans le cas oï¿½ l'on souhairait
	 *  mettre fin ï¿½ cette dï¿½pendance on supprimera cet attribut et on laissera ï¿½ la fonction UploadFile
	 *  le soin de fixer le nom du champ
	 */
	
	/*ATTENTION: ces paramï¿½tres ne sont pas spï¿½cifique aux transferts http mais ils permettent d'associer
	 * une instance HttpFileSender avec une communication donnï¿½e et de limiter le nombre d'arguments 
	 * des fonctions de la classe
	 * 
	 * Avec cette implementation il faut voir une instance comme un dialogue entre client et serveur
	 * dans lequel circulent plusieurs types de donnï¿½es*/	
	private HttpURLConnection connection;//la connexion correspondant au dialogue
	private String delimiter;//chaine de caractï¿½re delimitant les entrï¿½es du formulaire
	private OutputStream os;//output stream permettant d'ï¿½crire dans le contenu de la requete
	private PrintWriter writer;//outil issu de os qui va nous permettre d'ecrire des octets dans la requete

	/*Constructeur pertinant si on associe la connexion ï¿½ un envoi simple d'un fichier
	 *Les autres attributs sont fixï¿½s avec la mï¿½thode initialize.
	 */
	public HttpFileSender(String fileFieldName) {
		this.fileFieldName = fileFieldName;//on fixe donc le nom du champs correspond au fichier ï¿½ envoyer
	}
	
	
	/*Implementation de la mï¿½thode envoyerFichier
	 * 
	 * les paramï¿½tres sont donc les informations necessaires communes
	 * la mï¿½thode fera donc appel aux fonctions dï¿½finies si dessous.
	 * 
	 * 
	 */
	@Override
	public boolean envoyerFichier(String url,String fileName,InputStream content){
		try{
			this.initialize(url);
			this.UploadTextFile(this.fileFieldName,fileName,content);
			this.endSending();
			return this.readResponseStatus();
		}catch(HttpTransfertException e){
			return false;
		}catch(IOException e){
            return false;
        }
	}
	
	
	public void initialize(String url) throws HttpTransfertException{
	/*fonction qui permet l'initialisation d'une requete HTTP post ï¿½ partir d'une URL	
	 *cette fonction permet de construire l'entete de la requete HTTP	
	*/
		try{
			Log.d("DEBUG","...begining initialisation");
			//on construit l'url ï¿½ partir de l'adresse passï¿½e en paramï¿½tre
			URL targetAddress = new URL(url);
			//ouverture de la connection vers l'addresse indiquï¿½e
			this.connection = (HttpURLConnection) targetAddress.openConnection();
			//on prï¿½cise le type de la requï¿½te
			this.connection.setRequestMethod("POST");
			//on autorise les flux entrants dans la connexion: pour pouvoir ï¿½crire les informations
			this.connection.setDoInput(true);
			//on autotise les flux sortant de la connexion: pour pouvoir lire les informations
			this.connection.setDoOutput(true);
			//on rend la connection TCP sous jacente persistente
			this.connection.setRequestProperty("Connection", "Keep-Alive");
			//on gï¿½nï¿½re un dï¿½limiteur
			this.delimiter = "******"+Long.toString(System.currentTimeMillis())+"******";
			//on prï¿½cise le format de la partiï¿½ donnï¿½es de la requete
			this.connection.setRequestProperty("Content-type", "multipart/form-data; boundary="+this.delimiter);
			//on ï¿½tablit la connexion
			this.connection.connect();
			//on crï¿½e un acces pour ï¿½crire dans la requï¿½te via un output stream
			this.os = connection.getOutputStream();
			//on dï¿½finit le writer associï¿½ ï¿½ l'output stream qui permettra d'ï¿½crire les octects dans le flux
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
		/*cette fonction permet d'inserer un champ ï¿½lï¿½mentaire dans le formulaire ï¿½ envoyer
		 *un input fieldName sera donc crï¿½e, comportant la valeur val
		 *cette fonction ne sera appelï¿½ que sur un sender initialisï¿½.
		*/
			Log.d("DEBUG","...trying to write input" + fieldName +" in request");
			//on indique une nouvelle entrï¿½e du formulaire dans la requete avec une limite
			this.writer.append("--"+this.delimiter+"\r\n");
			//on indique une entrï¿½e du formulaire se rapportant au champ s'appelant fieldName
			this.writer.append("Content-Disposition: form-data; name=\""+fieldName+"\"\r\n");
			//on indique le format de la valeur du champ : ici texte encodï¿½ avec UTF8
			this.writer.append("Content-Type: text/plain; charset=UTF-8\r\n");
			//on indique la valeur que l'on affecte au champ
			this.writer.append("\r\n" + val + "\r\n");
			//on ï¿½crit le flux dans la requete
			this.writer.flush();
			Log.d("DEBUG","...input writen");
		
	}
	
	public void UploadTextFile(String fieldName, String fileName,InputStream content)throws HttpTransfertException{
		/*cette fonction permet d'inserer un fichier texte dans le formulaire ï¿½ envoyer
		 *un input appelï¿½ filedName sera crï¿½e et affectï¿½ ï¿½ un fichier fileName dont le contenu 
		 *sera rï¿½cuprï¿½rer par la lecture de content
		 *cette fonction ne sera appelï¿½ que sur un sender initialisï¿½.
		*/
		try{
			Log.d("DEBUG","...trying to write file" + fileName +" in request");
			Log.d("DEBUG","delimiter: "+this.delimiter);
			//on indique une nouvelle entrï¿½e du formulaire dans la requete avec une limite
			this.writer.append("--"+this.delimiter+"\r\n");
			//on indique une entrï¿½e du formulaire se rapportant au champ s'appelant nomChamp
			this.writer.append("Content-Disposition: form-data; name=\"" + fieldName+ "\"; filename=\"" + fileName+ "\"\r\n");
			//on indique que le contenu correspond ï¿½ du texte
			this.writer.append("Content-Type: text/plain\r\n");
			//on indique comment sera encodï¿½ le texte pour la transmission
			this.writer.append("Content-Transfer-Encoding: binary\r\n");
			this.writer.append("\r\n");
			//on ï¿½crit le flux dans la requete
			this.writer.flush();
		
			/*il faut maintenant remplir la requï¿½te avec le contenu du fichier
			 *on met en place un systï¿½me de lecture ï¿½criture
			 *on definit un buffer intermediaire pour transvaser les donnï¿½es
			 */
			byte[] temp = new byte[1024];
			//indicateur permettant de savoir si la lecture du fichier est terminï¿½e
			int indic;
			while((indic = content.read(temp)) != -1){//on lit le flux entrant dans le buffer
				os.write(temp, 0, indic);//on ï¿½crit le buffer dans le flux sortant
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
		 * une fois que tous les champs ont ï¿½tï¿½ inscrits dedans
		 * on aura donc achevï¿½ de remplir la requï¿½te qui sera donc 
		 * communiquï¿½e ï¿½ l'adresse cible. 
		 */
		try{
			
			Log.d("DEBUG","...ending request");
			//on inscrit le dï¿½limiteur final dans la requete. cette ligne signifie au server la fin de la requete
			this.writer.append("--"+this.delimiter+"--"+"\r\n");
			//on ï¿½crit le flux dans la requete
			this.writer.flush();	
			//on ferme tous les outils d'ï¿½criture
			this.writer.close();
			this.os.close();
			Log.d("DEBUG","...request ended");		
		}catch(IOException e){
			Log.d("end sending IO Exception", e.toString());
			e.printStackTrace();
			throw new HttpTransfertException("end sending closing outputstream exception :"+e.toString());
			
		}	
		
	}
	
	/*fonction qui indique si le transfert s'est bien déroulé*/
	public boolean readResponseStatus() throws IOException{
			int response = this.connection.getResponseCode();
			return (response==HttpURLConnection.HTTP_OK);
			
		}
	
	
	
	
	
	

}
