package com.qualoutdoor.recorder.persistent;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/*Classe ayant pour but de cr�er la base de donn�e et de la 
 * r�initialiser en cas de mise � jour, elle definit aussi 
 * le nom de la table et le nom de ses colonnes 
 * 
 * Le Systeme de base de donn�es de Recorder est constitu� de 2 tables:
 * une table qui represente la structure de l'arbre : la table de reference 
 * une autre qui stocke le d�tail de chaque feuille : la table de mesures
 * 
 */
public class SQLDataBaseCreator extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "recorder.db";//nom de la base de donn�e
	private static final int DATABASE_VERSION = 1;//version de la base de donn�e
	//les diff�rentes tables de la base de donn�es
	private TableDB table_reference;
	private TableDB table_measure;
	
	
	
	
	//Constructeur du createur
	public SQLDataBaseCreator(Context context) throws DataBaseException {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		this.table_reference = new TableDB("recorder_tt",new String[] {"LINE","VALUE","LEVEL"}, new String[] {"INTEGER","INTEGER NOT NULL","INTEGER"});
		this.table_measure = new TableDB("measure_it",new String[] {"ID","DATE","LAT","LNG","DATA"}, new String[] {"INTEGER PRIMARY KEY AUTOINCREMENT","TIMESTAMP default (strftime('%s', 'now'))","REAL","REAL","VARCHAR"});
		
	}
	
	/*Fonction � implementer obligatoirement
	 *elle d�crit les requetes � executer sur la bdd lorsque  la version est augment�e
	 *Ici on indique de d�truire nos tables et de les recr�er */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//requete correspondant � la destruction de la table 
		db.execSQL("DROP TABLE IF EXISTS '" + this.table_reference.getName() + "' ");
		db.execSQL("DROP TABLE IF EXISTS '" + this.table_measure.getName() + "'");
		//on recr�e les nouvelles tables
		onCreate(db);
	}
	
	/*Fonction � implementer obligatoirement
	 *elle d�crit les requetes � executer sur la bdd � la cr�ation du cr�ateur
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {	
	
			//CONSTRUCTION DES TABLES DE LA BASE DE DONNEE:
				//CREATION DES OBJETS TableDB
			
				//EXECUTION DES REQUETES
			db.execSQL(table_reference.createTableintoDB());//construction de la table de r�f�rence
			db.execSQL(table_measure.createTableintoDB());//construction de la table de mesure

				//On INSERE LE ROOT DANS LA TABLE DE REFERENCE
			db.execSQL("INSERT INTO "+this.table_reference.getName()+" (LINE,VALUE,LEVEL) VALUES (2,0,0); ");
			
			Log.d("DEBUG SQL CREATOR", "tables cr��s");
			
		
	}
	
	
	
	public TableDB getTableReference() {
			return this.table_reference;
	}
	
	public TableDB getTableMeasure(){
		return this.table_measure;
	}
	
	
	
}
