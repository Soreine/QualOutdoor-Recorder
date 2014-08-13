package com.qualoutdoor.recorder.persistent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class FileGenerator extends AsyncTask<Void, Void, ByteArrayOutputStream> {
    /*
     * Cette classe va examiner les 2 tables de la bdd pour former un fichier
     * csv, il sera possible de rajouter des commentaires en en-t�te. Elle va
     * donc retranscrire la table de reference via un load data in file, mais
     * ensuite elle devra remplacer chaque reference de feuille par sa ligne
     * associ�e dans la table de mesures
     * 
     * Elle s'executera dans une tache asynchrone, et renverra un call back une
     * fois achev�e
     */

    private ByteArrayOutputStream file;// objet qui r�cup�re ce que l'on �crit
    private SQLConnector connecteur;// possede un connecteur pour obtenir des
                                    // d�tails sur la table de mesures
    private FileReadyListener callback;// l'objet sur lequel appliquer la
                                       // methode de callbak une fois la tache
                                       // termin�e
    // SOLUTION PROVISOIRE
    private String comments;// les commentaires � inserer dans le fichier

    public FileGenerator(SQLConnector conn, String com, FileReadyListener cb) {
        this.file = new ByteArrayOutputStream();
        this.comments = com;
        this.connecteur = conn;
        this.callback = cb;
    }

    /*
     * Fonction recursive qui permet de restranscrire un noeud dans
     * l'outputStream : ses d�tails, et son contenu (ses fils)
     */
    /*
     * public void nodeRetranscription(DataBaseTreeManager managerWriter){ try{
     * //si on pointe une feuille, on va chercher ses d�tails pour les �crire
     * if(managerWriter.getCursor().getLevel()==1){ int refFeuille =
     * managerWriter.getCursor().getReference();//on r�cupere la reference de la
     * feuille ArrayList<String> details =
     * connecteur.getLeafDetails(refFeuille);//on demande ses d�tails au
     * connecteur this.file.write(";".getBytes());//ouverture d'un noeud dans le
     * fichier correspondant � la feuille int compteurVirgule1 = 1; for(String
     * field : details){//on inscrit � la suite ses d�tails
     * this.file.write(field.getBytes()); if(compteurVirgule1!=details.size()){
     * this.file.write(",".getBytes());//pour chaque �l�ment non dernier de la
     * liste on les fait suivre d'un virgule } compteurVirgule1++; }
     * this.file.write("]".getBytes());//on ferme le noued feuille du fichier
     * }else{//on pointe un noeud non feuille on ecrit sa reference et on
     * recommence avec ses fils int refNode =
     * managerWriter.getCursor().getReference();
     * this.file.write(("["+refNode+",").getBytes());//on �crit le d�tail du
     * noeud du fichier int compteurVirgule=1; ArrayList<int[]> children =
     * managerWriter.getDirectChildSides();//on �crit r�cursivement les fils du
     * noeud for (int[] child : children ){//pour chaque fils
     * managerWriter.focusOn(child[0],child[1]);//on se place dessus
     * nodeRetranscription(managerWriter);//on l'�crit
     * if(compteurVirgule!=children.size()){//si ce n'est pas le dernier fils de
     * la list on met une virgule this.file.write(",".getBytes()); }
     * compteurVirgule++; } this.file.write("]".getBytes());//fermeture du noeud
     * du fichier } }catch(DataBaseException e){ e.printStackTrace();
     * }catch(IOException e){ e.printStackTrace(); } }
     */

    /*
     * Fonction qui permet de retranscrire le contenu des deux tables de la bdd
     * dans le byteBufferOutputStream
     */
    public void tablesRetransciption(DataBaseTreeManager managerWriter) {
        try {
            while (managerWriter.moveToNextLine()) {// on lit la nouvelle ligne
                Log.d("debug writer", "131");
                if (managerWriter.getCursor().getLevel() == 7) {// si la ligne
                                                                // correspond �
                                                                // une feuille
                    ArrayList<String> details = connecteur
                            .getLeafDetails(managerWriter.getCursor()
                                    .getReference());// on demande ses d�tails
                                                     // au connecteur
                    this.file
                            .write((managerWriter.getCursor().getLevel() + "/")
                                    .getBytes());// ouverture d'un noeud dans le
                                                 // fichier correspondant � la
                                                 // feuille
                    int compteurslash1 = 1;
                    for (String field : details) {// on inscrit � la suite ses
                                                  // d�tails
                        this.file.write(field.getBytes());
                        if (compteurslash1 != details.size()) {
                            this.file.write("/".getBytes());// pour chaque
                                                            // �l�ment non
                                                            // dernier de la
                                                            // liste on les fait
                                                            // suivre d'un slash
                        }
                        compteurslash1++;
                    }
                    this.file.write(";".getBytes());
                } else {// si la ligne correspond � une noeud non feuille
                    Log.d("debug writer", "132");
                    int refNode = managerWriter.getCursor().getReference();
                    int levelNode = managerWriter.getCursor().getLevel();
                    this.file.write((levelNode + "/" + refNode + "$")
                            .getBytes());// ouverture d'un noeud + �criture de
                                         // sa reference dans le fichier
                }
            }
        } catch (DataBaseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     * Fonction qui permet d'initialiser nodeRetranscription : elle permet
     * l'insertion des commentaires d'entete puis une fois l'�criture termin�e,
     * elle pr�vient le connecteur de remettre � zero tout le systeme de
     * stockage
     */
    public void completeRetranscription(String comments,
            DataBaseTreeManager managerWriter) throws DataBaseException {
        try {
            // on verifie s'il ya des feuilles � envoyer:
            if (this.connecteur.hasLeaf()) {
                Log.d("debug writer", "11");
                this.file.write(("#" + comments + "#").getBytes());// ouverture
                                                                   // du fichier
                Log.d("debug writer", "12");
                this.tablesRetransciption(managerWriter);
                Log.d("debug writer", "13");
                // une fois le fichier g�n�r� on remet � z�ro tout le systeme de
                // stockage
                this.connecteur.completeReset();
            } else {
                throw new DataBaseException("no leaf to be write!");
            }
        } catch (IOException e) {// capte les exceptions li�es � l'�criture dans
                                 // le buffer
            e.printStackTrace();
        }

    }

    // tache principale en background
    @Override
    protected ByteArrayOutputStream doInBackground(Void... params) {
        try {
            Log.d("debug writer", "1");
            completeRetranscription(this.comments,
                    this.connecteur.prepareManager());
            Log.d("debug writer", "2");
            return this.file;
        } catch (DataBaseException e) {// Dans le cas ou il n'y a pas de
                                       // feuilles � �crire
            e.printStackTrace();
            return null;
        }

    }

    // recup�re l'output stream g�n�r� par la tache principale et v�rifie s'il
    // est null ou pas, et appelle avec
    // ce dernier le callback
    @Override
    protected void onPostExecute(ByteArrayOutputStream result) {
        if (result == null) {
            Log.d("DEBUG FILE GENERATOR", "NO TEXT GENERATED");
        } else {
            Log.d("DEBUG FILE GENERATOR", result.toString());
        }
        this.callback.onFileReady(result);
    }

}
