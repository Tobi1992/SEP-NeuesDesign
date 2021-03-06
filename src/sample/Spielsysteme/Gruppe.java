package sample.Spielsysteme;

import sample.*;
import sample.DAO.*;
import sample.Enums.*;

import java.lang.reflect.Array;
import java.util.*;

public class Gruppe extends Spielsystem {
	private ArrayList<Team> teamList = new ArrayList<>();
	private Spielsystem spielsystem;
	int anzahlTeams;
	int[][] schablone;
	private ArrayList<Integer> setzPlaetzeFuerEndrunde = new ArrayList<>();

	public Gruppe(ArrayList<Team> setzliste, Spielklasse spielklasse) {
		try {
			this.setSpielSystemArt(1);
			this.spielsystem=this;
			setSpielklasse(spielklasse);
			this.teamList = setzliste;
			freiloseHinzufuegen(teamList);
			setAnzahlRunden(teamList.size()-1);
			anzahlTeams = teamList.size();
			alleSpieleErstellen();
			schablone = new int[anzahlTeams][anzahlTeams];
			schabloneBauen();
			for (int i=0;i<getAnzahlRunden();i++){
				rundeErstellen(false);
				resetOffeneRundenSpiele();
			}
			setOffeneRundenSpiele(anzahlTeams/2);
			resetAktuelleRunde();
			alleSpieleSchreiben();
		/*	for (int i=0;i<getAnzahlRunden();i++) {
				rundeStarten(0);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Gruppe(ArrayList<Team> setzliste, GruppeMitEndrunde spielsystem,Spielklasse spielklasse, int extraRunde) {
		this.setSpielSystemArt(2);
		this.setExtraRunde(extraRunde);
		this.spielsystem = spielsystem;
		this.setSpielklasse(spielklasse);
		this.teamList = setzliste;
		freiloseHinzufuegen(teamList);
		setAnzahlRunden(teamList.size()-1);
		anzahlTeams = teamList.size();
		alleSpieleErstellen();
		schablone = new int[anzahlTeams][anzahlTeams];
		schabloneBauen();
		for (int i=0;i<getAnzahlRunden();i++){
			rundeErstellen(false);
			resetOffeneRundenSpiele();
		}
		setOffeneRundenSpiele(anzahlTeams/2);
		resetAktuelleRunde();
		alleSpieleSchreiben();
	/*	for (int i=0;i<getAnzahlRunden();i++) {
			rundeStarten(0);
		}*/
		setzPlaetzeFuerEndrundeBerechnen();
	}

	public Gruppe(int anzahlTeams, GruppeMitEndrunde spielsystem,Spielklasse spielklasse){ //Constructer für Gruppe als Endrunde
		this.setSpielSystemArt(2);
		this.spielsystem = spielsystem;
		this.setSpielklasse(spielklasse);
		this.anzahlTeams=anzahlTeams;
		if(anzahlTeams/2*2!=anzahlTeams){
			this.anzahlTeams++;
		}
		//freiloseHinzufuegen(teamList);
		setAnzahlRunden(this.anzahlTeams-1);
		alleSpieleErstellen();
		if(anzahlTeams!=this.anzahlTeams){
			freilosSchreiben();
		}
		schablone = new int[this.anzahlTeams][this.anzahlTeams];
		schabloneBauen();
		setOffeneRundenSpiele(this.anzahlTeams/2);
		resetAktuelleRunde();
		alleSpieleSchreiben();
		//rundeStarten(0);
	}

	private void freilosSchreiben() {
		if(getAktuelleRunde()<=getAnzahlRunden()){
			ArrayList<Team> tempList = new ArrayList<>();
			for (int i=0; i<teamList.size();i++){
				tempList.add(teamList.get(i));
			}
			erhoeheAktuelleRunde();
			for(int i=0;i<getAnzahlRunden();i++){
				Team freilos = new Team("Freilos",this.getSpielklasse());
				if(getSpielklasse()!=null){
					int systemSpielID = getSpielSystemArt()*10000000+i*1000;
					Spiel spiel = getSpielklasse().getSpiele().get(systemSpielID);
					spiel.setGast(freilos);
				}
			}
		}
	}

	public void endrundeStarten(ArrayList<Team> setzliste){
		this.teamList=setzliste;
		freiloseHinzufuegen(teamList);
		for (int i=0;i<getAnzahlRunden();i++){
			resetOffeneRundenSpiele();
			rundeErstellen(true);
		}
		for(int i=0;i<getRundenArray().size();i++){
			for(int j=0;j<getRundenArray().get(i).size();j++){
				Spiel spiel = getRundenArray().get(i).get(j);
				spiel.setFreilosErgebnis();
			}
		}
		resetOffeneRundenSpiele();
	}

	private void setzPlaetzeFuerEndrundeBerechnen() {
		GruppeMitEndrunde hauptsystem = (GruppeMitEndrunde) spielsystem;
		int anzahlGruppen = hauptsystem.getAnzahlGruppen();
		int gruppenNummer = getExtraRunde();
		for (int i=0;i<Math.ceil(hauptsystem.getAnzahlWeiterkommender()/anzahlGruppen);i++){
			int zeile = i+1;
			int setzPlatzFuerEndrunde;
			if(zeile%2==0) {
				setzPlatzFuerEndrunde = anzahlGruppen * zeile - (gruppenNummer - 1);
				if(anzahlGruppen%2 == 0) {
					if (setzPlatzFuerEndrunde % 2 == 0) {
						setzPlatzFuerEndrunde--;
					} else {
						setzPlatzFuerEndrunde++;
					}
				}
			}
			else{
				setzPlatzFuerEndrunde = anzahlGruppen*i+gruppenNummer;
			}
			Integer[] array = new Integer[2];
			array[0] = gruppenNummer;
			array[1] = zeile;
			hauptsystem.getSetzPlatze().put(setzPlatzFuerEndrunde,array);
		}
	}

	private void alleSpieleSchreiben() {
		for (int i=0; i<this.getRundenArray().size(); i++){
			for(int j=0;j<this.getRundenArray().get(i).size();j++){
				Spiel spiel = this.getRundenArray().get(i).get(j);
				spiel.getSpielDAO().create(spiel);
				spiel.setFreilosErgebnis();
			}
		}
	}

	private ArrayList<Integer> arrayVerschieben(ArrayList<Integer> arrayList){
		int temp = arrayList.get(0);
		arrayList.remove(0);
		arrayList.add(temp);
		return arrayList;
	}

	private void alleSpieleErstellen(){
		for (int i=1;i<=getAnzahlRunden();i++){
			resetOffeneRundenSpiele();
			for (int j=0; j<anzahlTeams/2;j++){
				Spiel spiel = new Spiel(spielSystemIDberechnen(),this);
				if(this.getRundenArray().size()<=getAktuelleRunde()){
					this.getRundenArray().add(new ZeitplanRunde());
				}
				if(!this.getRundenArray().get(getAktuelleRunde()).contains(spiel)) {
					this.getRundenArray().get(getAktuelleRunde()).add(spiel);
				}
				erhoeheOffeneRundenSpiele();
			}
			erhoeheAktuelleRunde();
		}
		resetOffeneRundenSpiele();
		resetAktuelleRunde();
	}


	private void schabloneBauen(){
		ArrayList<Integer> rundenArray = new ArrayList<>();
		rundenArray.add(1);
		for (int j=anzahlTeams-1;j>1;j--){
			rundenArray.add(j);
		}
		for(int i=0; i<anzahlTeams-1; i++){
			for (int k=0;k<anzahlTeams-1; k++){
				schablone[i][k] = rundenArray.get(k);
			}
			schablone[i][anzahlTeams-1]=schablone[i][i];
			schablone[anzahlTeams-1][i]=schablone[i][i];
			schablone[i][i] = 0;
			arrayVerschieben(rundenArray);
		}
	}

	private void rundeErstellen(boolean endrunde){
		if(getAktuelleRunde()<=getAnzahlRunden()){
			ArrayList<Team> tempList = new ArrayList<>();
			for (int i=0; i<teamList.size();i++){
				tempList.add(teamList.get(i));
			}
			erhoeheAktuelleRunde();
			while (tempList.size()>1){
				Team teamEins = tempList.get(tempList.size()-1);//nehme letztes Team aus der temporären Liste (höchster verbleibender Setzplatz)
				int setzplatzTeamEins = teamList.indexOf(teamEins)+1;
				tempList.remove(teamEins);
				int setzplatzTeamZwei = schabloneDurchsuchen(setzplatzTeamEins); //durchsuche die Schablone nach aktuellem Gegner für TeamEins
				Team teamZwei = teamList.get(setzplatzTeamZwei-1);  //hole Gegner aus der Setzliste! (nicht TempList, weil diese kleiner wird!
				tempList.remove(teamZwei);  						//entferne Gegner aus tempList
				//alleSpiele.add(new Spiel(teamZwei,teamEins,getSpielklasse(),spielSystemIDberechnen()));
				if(getSpielklasse()!=null){
					Spiel spiel = getSpielklasse().getSpiele().get(spielSystemIDberechnen()-1000);
					spiel.setHeim(teamZwei);
					spiel.setGast(teamEins);
					if(endrunde) {
						spiel.getSpielDAO().update(spiel);
					}

				}
				else{
					Spiel spiel =this.getSpielklasse().getSpiele().get(spielSystemIDberechnen()-1000);
					spiel.setHeim(teamZwei);
					spiel.setGast(teamEins);
					if(endrunde) {
						spiel.getSpielDAO().update(spiel);
					}
				}
				erhoeheOffeneRundenSpiele();
			}

		}
	}
	private int schabloneDurchsuchen(int setzplatzTeamEins){
		for(int i=0; i<schablone[setzplatzTeamEins-1].length;i++){
			if(schablone[setzplatzTeamEins-1][i] == getAktuelleRunde()){
				return i+1;
			}
		}
		System.out.println("Gegner für diese Runde nicht gefunden!");
		return 0;
	}

	private void freiloseHinzufuegen(List<Team> teamList){
		if ((teamList.size()/2) * 2 != teamList.size()){ // /2 *2 überprüft, ob Spieleranzahl gerade oder ungerade (int)
			this.teamList.add(new Team("Freilos",this.getSpielklasse()));
			System.out.println("Freilos zu Gruppe hinzugefügt");
			super.setzlisteDAO.create(spielsystem.getSetzliste().size(),teamList.get(teamList.size()-1),this.getSpielklasse());
		}
	}

/*
	private void rundeStarten(int rundenIndex){
		if(this.getRundenArray().get(rundenIndex)!=null){
			for (int i=0;i<this.getRundenArray().get(rundenIndex).size();i++){
				Spiel spiel = this.getRundenArray().get(rundenIndex).get(i);
				spiel.setStatus(1);
				*/
/*spiel.getSpielsystem().getSpielklasse().getTurnier().getObs_zukuenftigeSpiele().remove(spiel);
				spiel.getSpielsystem().getSpielklasse().getTurnier().getObs_ausstehendeSpiele().add(spiel);*//*

				//spiel.setFreilosErgebnis();
				if (spiel.getHeim().isFreilos()){
					//spiel.setErgebnis(new Ergebnis(0,21,0,21));
					spiel.setFreilosErgebnis();
				}
				else if(spiel.getGast().isFreilos()){
					//spiel.setErgebnis(new Ergebnis(21,0,21,0));
					spiel.setFreilosErgebnis();
				}
				spiel.getSpielDAO().update(spiel);
			}
		}
	}
*/

	@Override
	public List<Team> getPlatzierungsliste() {
		return null;
	}

	@Override
	public boolean beendeMatch(Spiel spiel) {
		senkeOffeneRundenSpiele();
		if(keineOffenenRundenSpiele()){
			erhoeheAktuelleRunde();
			setOffeneRundenSpiele(anzahlTeams/2);
			System.out.println(getAktuelleRunde());
			if(getAktuelleRunde()==getAnzahlRunden()){
				sortList(teamList);
				setPlatzierungsListe(teamList);
				if (getExtraRunde()!=0){
					GruppeMitEndrunde gruppeMitEndrunde = (GruppeMitEndrunde) spielsystem;
					gruppeMitEndrunde.addPlatzierungsliste(teamList,getExtraRunde());
				}
			}
			else {
//				rundeStarten(getRundenIndex(spiel) + 1);
			}
		}
		return false;
	}

	private int getRundenIndex(Spiel spiel){
		for (int i=0;i<this.getRundenArray().size();i++){
			if (this.getRundenArray().get(i).contains(spiel)){
				return i;
			}
		}
		return 0;
	}
	public ArrayList<Team> getSetzliste(){
		return teamList;
	}

	//Einlesenregion

	public Gruppe(ArrayList<Team> setzliste, Spielklasse spielklasse, ArrayList<Spiel> spielListe, Dictionary<Integer,Ergebnis> ergebnisse) {
		this.setSpielSystemArt(1); 							//Constructor nur für Einlesen aus der Datenbank
		setSpielklasse(spielklasse);
		this.spielsystem=this;
		this.teamList = setzliste;
		setAnzahlRunden(teamList.size()-1);
		anzahlTeams = teamList.size();
		alleSpieleEinlesen(spielListe);
		resetAktuelleRunde();
		alleErgebnisseEinlesen(ergebnisse);
	}


	public Gruppe(ArrayList<Team> setzliste, GruppeMitEndrunde spielsystem,Spielklasse spielklasse, int extraRunde,  ArrayList<Spiel> spielListe, Dictionary<Integer,Ergebnis> ergebnisse) {
		this.setSpielSystemArt(2);
		this.setExtraRunde(extraRunde);
		this.spielsystem = spielsystem;   //Constructor für einlesen von Gruppe Mit Endrunde aus der Datenbank
		this.setSpielklasse(spielklasse);
		this.teamList = setzliste;
		setAnzahlRunden(teamList.size()-1);
		anzahlTeams = teamList.size();
		/*alleSpieleEinlesen(spielListe);
		resetAktuelleRunde();
		alleErgebnisseEinlesen(ergebnisse);*/
		setzPlaetzeFuerEndrundeBerechnen();
	}

	public Gruppe(int anzahlTeams, GruppeMitEndrunde spielsystem, Spielklasse spielklasse, ArrayList<Spiel> spielListe, Dictionary<Integer,Ergebnis> ergebnisse) {
		this.setSpielSystemArt(2); 							//Constructor nur für Einlesen aus der Datenbank bei Gruppe als Endrunde in GruppeMitEndrunde
		setSpielklasse(spielklasse);
		this.spielsystem=spielsystem;
		this.anzahlTeams = anzahlTeams;
		if(this.anzahlTeams/2*2!=this.anzahlTeams){
			this.anzahlTeams++;
		}
		setAnzahlRunden(this.anzahlTeams-1);
		schablone = new int[this.anzahlTeams][this.anzahlTeams];
		schabloneBauen();
		setzlisteGenerieren(spielListe);
		/*alleSpieleEinlesen(spielListe);
		resetAktuelleRunde();
		alleErgebnisseEinlesen(ergebnisse);*/
	}

	public void allesEinlesen( ArrayList<Spiel> spielListe, Dictionary<Integer,Ergebnis> ergebnisse){
		alleSpieleEinlesen(spielListe);
		resetAktuelleRunde();
		alleErgebnisseEinlesen(ergebnisse);
	}


	private void alleSpieleEinlesen(ArrayList<Spiel> spiele){
		for (int i=0;i<spiele.size();i++){
			spiele.get(i).setSpielsystem(this.spielsystem);
		}
		rundenListeEinlesen(spiele);
		setOffeneRundenSpiele(anzahlTeams/2);
	}

	private void rundenListeEinlesen(ArrayList<Spiel> spiele){
		for (int i=0;i<spiele.size();i++){
			int spielklasseSpielID=spiele.get(i).getSystemSpielID();
			int rundenNummer = (spielklasseSpielID-getSpielSystemArt()*10000000);
			rundenNummer = (rundenNummer - (rundenNummer /100000 * 100000)) /1000;
			Spiel spiel =spiele.get(i);
			while(this.getRundenArray().size()-1<rundenNummer){
				this.getRundenArray().add(new ZeitplanRunde());
			}
			this.getRundenArray().get(rundenNummer).add(spiel);
		}
	}

	private void alleErgebnisseEinlesen(Dictionary<Integer, Ergebnis> ergebnisse){
		Enumeration e = ergebnisse.keys();
		int key;
		while(e.hasMoreElements()){
			key = (int) e.nextElement();
			getSpielklasse().getSpiele().get(key).setErgebnis(ergebnisse.get(key),"einlesen");
			if (this.getExtraRunde()!=0){
				this.beendeMatch(getSpielklasse().getSpiele().get(key),"einlesen");
			}
		}
	}


	public boolean beendeMatch(Spiel spiel, String einlesen) {
		senkeOffeneRundenSpiele();
		if(getOffeneRundenSpiele()==0){
			erhoeheAktuelleRunde();
			setOffeneRundenSpiele(anzahlTeams/2);
			if(getAktuelleRunde()==getAnzahlRunden()){
				sortList(teamList);
				setPlatzierungsListe(teamList);
			}
		}
		return false;
	}

	public void setzlisteGenerieren(ArrayList<Spiel> spielListe) {
		for (int i=0;i<spielListe.size();i++){
			Spiel spiel = spielListe.get(i);
			if(spiel.getHeim()!=null && !teamList.contains(spiel.getHeim())){
				teamList.add(spiel.getHeim());
			}
			if(spiel.getGast()!=null && !teamList.contains(spiel.getGast())){
				teamList.add(spiel.getGast());
			}
		}
	}
	//endregion

}