package com.qualoutdoor.recorder.network;

import java.io.InputStream;

public class FileToUpload {

	/* afin de clarifier le code on cr�e un classe d�crivant l'objet que repr�sente un fichier � uploader
	 * en particulier un nom sous lequel on veut qu'il soit stock� et un intputstream qui joue un r�le de 
	 * lecteur du contenu 
	 */
	
	private String fileName;//nom du fichier
	private InputStream content;// lecteur du contenu
	
	
	public FileToUpload(String fileName, InputStream content) {		
		this.fileName = fileName;
		this.content = content;
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public InputStream getContent() {
		return content;
	}
	public void setContent(InputStream content) {
		this.content = content;
	}
	
	

}
