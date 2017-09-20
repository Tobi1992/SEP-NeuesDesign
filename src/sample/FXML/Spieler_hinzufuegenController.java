package sample.FXML;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import sample.*;
import sample.DAO.TurnierDAO;
import sample.DAO.auswahlklasse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by jens on 20.09.2017.
 */
public class Spieler_hinzufuegenController implements Initializable{
    ContextMenu contextMenu = new ContextMenu();
    String baseName = "resources.Main";
    String titel ="";

//region Deklaration

    private Spieler doppelpartner1;
    private Spielklasse doppelspielklasse=null;


    @FXML
    private JFXTextField t_suchleistespielerhinzu;
    @FXML
    private Tab tab_sphin;
    @FXML
    private Tab tab_spbea;
    @FXML
    private JFXTabPane tabpane_spieler;
    @FXML
    private ChoiceBox<Verein> combo_verein;
    @FXML
    private JFXTextField t_vn;
    @FXML
    private JFXTextField t_nn;
    @FXML
    private JFXDatePicker d_geb;
    @FXML
    private JFXTextField t_spid;
    @FXML
    private JFXTextField t_re;
    @FXML
    private JFXTextField t_rd;
    @FXML
    private JFXTextField t_rm;
    @FXML
    private JFXRadioButton r_m;
    @FXML
    private JFXRadioButton r_w;

    //Tab2
    @FXML
    private TableView tabelle_spielerliste;



    @FXML
    private Button b_neuerVerein;
    @FXML
    private Button b_abbrechen;
    @FXML
    private Button b_spielerspeichern;
    @FXML
    private TableColumn tabelle_spielerliste_vorname;
    @FXML
    private TableColumn tabelle_spielerliste_nachname;
    @FXML
    private TableColumn tabelle_spielerliste_verein;
    @FXML
    private TableColumn tabelle_spielerliste_SpielerID;
    @FXML
    private TableColumn tabelle_spielerliste_geschlecht;
    @FXML
    private TableColumn tabelle_spielerliste_geburtstag;

    @FXML
            private Label label_partner;



    //endregion

    Boolean Update=false;
    HashMap<Integer, Spieler> spielerhash = new HashMap<Integer, Spieler>();

    public static Spieler spieler_neu=null;
    public static ObservableList<Spieler> obs_spieler = auswahlklasse.getObs_spieler();


    private void printSpielerZuordnenTableNeu() throws Exception {

        if(auswahlklasse.getAktuelleTurnierAuswahl()!=null) {

            obs_spieler.clear();
            Enumeration enumSpielerIDs = auswahlklasse.getSpieler().keys();
            while (enumSpielerIDs.hasMoreElements()){
                int key = (int)enumSpielerIDs.nextElement();
                obs_spieler.add(auswahlklasse.getSpieler().get(key));
                spielerhash.put(auswahlklasse.getSpieler().get(key).getSpielerID(),auswahlklasse.getSpieler().get(key));
            }

            //region PropertyValueFactory
            tabelle_spielerliste_vorname.setCellValueFactory(new PropertyValueFactory<Spieler,String>("vName"));
            tabelle_spielerliste_geschlecht.setCellValueFactory(new PropertyValueFactory<ImageView,String>("iGeschlecht"));

            tabelle_spielerliste_SpielerID.setCellValueFactory(new PropertyValueFactory<Spieler,String>("ExtSpielerID"));


            tabelle_spielerliste_nachname.setCellValueFactory(new PropertyValueFactory<Spieler,String>("nName"));

            tabelle_spielerliste_verein.setCellValueFactory(new PropertyValueFactory<Spieler,String>("verein"));

            tabelle_spielerliste_geburtstag.setCellValueFactory(new PropertyValueFactory<Spieler,Date>("gDatum"));
            tabelle_spielerliste.setItems(obs_spieler);
            //endregion

        }
        else{
            System.out.println("kann Turnier nicht laden");
        }

    }

    private void felderLeeren()
    {
        t_vn.setText("");
        t_nn.setText("");
        d_geb.setValue(null);
        t_spid.setText("");

        combo_verein.getSelectionModel().select(0);
        t_re.setText("0");
        t_rd.setText("0");
        t_rm.setText("0");
        r_m.setSelected(true);
        r_w.setSelected(false);


    }

    public static ObservableList<Spieler> getObs_spieler() {
        return obs_spieler;
    }

    public static void setObs_spieler(ObservableList<Spieler> obs_spielerr) {
        obs_spieler = obs_spielerr;
    }

    @FXML
    public void pressBtn_SpielerSpeichern(ActionEvent event) throws Exception
    {


        if(!Update)
        {
            if(t_vn.getText().equals("")||t_nn.getText().equals("")||t_re.getText().equals("")||t_rd.getText().equals("")||t_rm.getText().equals("")||d_geb.getValue()==null)
            {
                System.out.println("Spielerdaten unvollständig");
                auswahlklasse.WarnungBenachrichtigung("Unvollständig","Unvollständig");
            }
            else
            {
                //region Spieler erstellen
                boolean geschlecht = false;
                if (r_m.isSelected())
                {
                    geschlecht=true;
                }
                else
                {
                    geschlecht=false;
                }
                int []rpunkte = new int[3];
                rpunkte[0]=Integer.parseInt(t_re.getText());
                rpunkte[1]=Integer.parseInt(t_rd.getText());
                rpunkte[2]=Integer.parseInt(t_rm.getText());


                Verein verein = combo_verein.getSelectionModel().getSelectedItem();
                System.out.println(auswahlklasse.getSpieler().size());

                spieler_neu= new Spieler(t_vn.getText(),t_nn.getText(),d_geb.getValue(),geschlecht,rpunkte,verein,t_spid.getText(),"");

                //endregion

                //ArrayList<Spieler> vorhandeneSpieler = new ArrayList<>();

                felderLeeren();
                Enumeration e = auswahlklasse.getSpieler().elements();
                ObservableList obs_vorhanden= FXCollections.observableArrayList();
                while(e.hasMoreElements())
                {
                    Spieler sp = (Spieler) e.nextElement();
                    if(sp!=null&&spieler_neu!=null&&sp.getVName()!=null&&sp.getNName()!=null) {
                        if (sp.getNName().equalsIgnoreCase(spieler_neu.getNName()) && sp.getVName().equalsIgnoreCase(spieler_neu.getVName())) {

                            System.out.println("Übereinstimmung gefunden:");
                            System.out.println(sp.getVName() + " " + sp.getNName() + " --- " + spieler_neu.getVName() + " " + spieler_neu.getNName());
                            TurnierDAO t;
                            //t = new TurnierDAOimpl();

                            obs_vorhanden.add(sp);

                        }
                    }
                }
                Dictionary<Spieler,ObservableList> obs = new Hashtable<>();
               auswahlklasse.setDict_doppelte_spieler(obs);
                //auswahlklasse.setAktuellerSpieler(spieler_neu);

                auswahlklasse.getDict_doppelte_spieler().put(spieler_neu,obs_vorhanden);






                if(obs_vorhanden.size()>0)
                {
           /* ExcelImport.setSpielerzumHinzufeuegen(spieler_neu);
            ExcelImport.setVorhandeneSpieler(vorhandeneSpieler);*/

                    pressBtn_Popup(event);
                }
                else
                {
                    spieler_neu.getSpielerDAO().create(spieler_neu);
                    auswahlklasse.addSpieler(spieler_neu);
                    auswahlklasse.getSpieler().put(spieler_neu.getSpielerID(),spieler_neu);
                    printSpielerZuordnenTableNeu();
                    auswahlklasse.InfoBenachrichtigung("erf",spieler_neu.toString());
                }



            }
        }
        else
        {
            pressBtn_SpielerUpdaten();
        }

        spielerTabelleAktualisieren();
        tabpane_spieler.getSelectionModel().select(tab_spbea);
        tabelle_spielerliste.getSelectionModel().select(spieler_neu);
        throw new Exception("");
    }


//    @FXML
//    public void SpielerSpeichern(ActionEvent event)
//    {
//        obs_spieler.add(spieler_neu);
//        a.addSpieler(spieler_neu);
//        spielerTabelleAktualisieren();
//        felderLeeren();
//
//    }

    public void  spielerTabelleAktualisieren()
    {
        tabelle_spielerliste.refresh();
    }

    public void pressBtn_Popup (ActionEvent event) throws Exception {
        //System.out.println("test");
auswahlklasse.getDashboardController().setNodeSpielervorhanden();
    }

    private void zeigePopup()
    {
        Popup popup = new Popup();
        //CustomController controller = new CustomController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("spielerVorhanden.fxml"));
        loader.setController(this);
        try {
            popup.getContent().add((Parent)loader.load());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    public void pressBtn_SpielerUpdaten() throws Exception
    {

        tab_spbea.setDisable(false);

        tabpane_spieler.getSelectionModel().select(tab_spbea);
        boolean geschlecht = false;
        if (r_m.isSelected())
        {
            geschlecht=true;
        }
        else
        {
            geschlecht=false;
        }
        int []rpunkte = new int[3];
        rpunkte[0]=Integer.parseInt(t_re.getText());
        rpunkte[1]=Integer.parseInt(t_rd.getText());
        rpunkte[2]=Integer.parseInt(t_rm.getText());




        Verein v = combo_verein.getSelectionModel().getSelectedItem();


        spieler_neu.setvName(t_vn.getText());
        spieler_neu.setnName(t_nn.getText());
        spieler_neu.setgDatum(d_geb.getValue());
        spieler_neu.setExtSpielerID(t_spid.getText());
        spieler_neu.setrPunkte(rpunkte);
        spieler_neu.setGeschlecht(geschlecht);
        spieler_neu.setVerein(v);

        boolean erfolg = spieler_neu.getSpielerDAO().update(spieler_neu);

        if(!erfolg)
        {
            auswahlklasse.WarnungBenachrichtigung("Spieler Update Fehler","fehler");
        }
        else
        {
            auswahlklasse.InfoBenachrichtigung("erfolg","erfolg");
        }


        auswahlklasse.getSpieler().remove(spieler_neu.getSpielerID());
        auswahlklasse.getSpieler().put(spieler_neu.getSpielerID(),spieler_neu);
        int index = obs_spieler.indexOf(spieler_neu);
        obs_spieler.remove(spieler_neu);
        obs_spieler.add(index, spieler_neu);
        //Spieler spieler = new Spieler(t_vn1.getText(),t_nn1.getText(),d_geb1.getValue(),a.getSpieler().size()+1,geschlecht,rpunkte,verein,t_spid.getText());
        //System.out.println(spieler.getNName());
        //a.addSpieler(spieler);

        beschriftungHinzu();


    }


    @FXML
    private void choiceclick(ActionEvent event)
    {
        try {
            ladeVereine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void ladeVereine() throws Exception
    {

        System.out.println(auswahlklasse.getVereine().size());
        ObservableList vereine = FXCollections.observableArrayList();
        Enumeration enumKeys = auswahlklasse.getVereine().keys();
        while (enumKeys.hasMoreElements()){
            int key = (int) enumKeys.nextElement();
            vereine.add(auswahlklasse.getVereine().get(key));

        }
        try {
            combo_verein.setItems(vereine);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
auswahlklasse.setSpieler_hinzufuegenController(this);
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_vorname");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        t_vn.setPromptText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_nachname");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        t_nn.setPromptText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_gdatum");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        d_geb.setPromptText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_spielerid");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        t_spid.setPromptText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_ranglistenpunkte");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }



        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_verein");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_geschlecht");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }


        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_einzel");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        t_re.setPromptText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_doppel");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        t_rd.setPromptText(titel);
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_doppel");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        t_rm.setPromptText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("b_neuerVerein");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        b_neuerVerein.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("b_abbrechen");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        b_abbrechen.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("b_spielerspeichern");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        b_spielerspeichern.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("t_spielersuche");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        t_suchleistespielerhinzu.setPromptText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("r_m");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        r_m.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("r_w");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        r_w.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tab_sphin");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tab_sphin.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tab_spbea");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tab_spbea.setText(titel);


        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tabelle_spielerliste_vorname");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tabelle_spielerliste_vorname.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tabelle_spielerliste_nachname");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tabelle_spielerliste_nachname.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tabelle_spielerliste_verein");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tabelle_spielerliste_verein.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tabelle_spielerliste_SpielerID");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tabelle_spielerliste_SpielerID.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tabelle_spielerliste_geschlecht");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tabelle_spielerliste_geschlecht.setText(titel);

        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tabelle_spielerliste_geburtstag");
        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
        tabelle_spielerliste_geburtstag.setText(titel);



        //region Tabelle Spielerliste RowFactory
        tabelle_spielerliste.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY)
                {
                    contextMenu.hide();
                }
                if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY
                        && event.getClickCount() == 2) {
                    Spieler clickedRow = (sample.Spieler) row.getItem();
                    Update=true;
                    UpdateSpieler(clickedRow);

                    //(((Node)(event.getSource())).getScene().getWindow().hide();
                }
                if(! row.isEmpty() && event.getButton()== MouseButton.SECONDARY)
                {
                    Spieler clickedRow = (Spieler) row.getItem();
                    MenuItem item1 = new MenuItem("Spieler hinzufügen");
                    item1.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            tabpane_spieler.getSelectionModel().select(tab_sphin);
                        }
                    });
                    MenuItem item2 = new MenuItem("Spieler bearbeiten");
                    item2.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            tabpane_spieler.getSelectionModel().select(tab_sphin);
                            FuelleFelder(clickedRow);
                            spieler_neu=clickedRow;
                        }
                    });
                    Menu item4 = new Menu("zur Setzliste hinzufügen");
                    item4.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {

                        }
                    });
                    Enumeration enumeration=auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().keys();

                    MenuItem[] i = new MenuItem[auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().size()];
                    int index=0;
                    ArrayList besetzt = clickedRow.checkeSetzlisteMitglied(clickedRow);
                    while(enumeration.hasMoreElements())
                    {
                        boolean bbesetzt=false;

                        int key = (int) enumeration.nextElement();


                        for(int j =0;j<besetzt.size();j++)
                        {
                            if(auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key).equals(besetzt.get(j)))
                            {
                                bbesetzt=true;
                            }
                        }


                        if(!bbesetzt&&

                                (clickedRow.getGeschlecht()&&auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key).toString().toUpperCase().contains("HERREN")

                                        ||(!clickedRow.getGeschlecht()&&auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key).toString().toUpperCase().contains("DAMEN"))

                                ))

                        {


                            i[index] = new MenuItem(auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key).toString());
                            i[index].setOnAction(new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent event) {
                                    boolean b =false;
                                    if(auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key).isEinzel())
                                    {
                                        Team team = new Team(clickedRow,auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key));
                                        auswahlklasse.getAktuelleTurnierAuswahl().getTeams().put(team.getTeamid(),team);
                                        auswahlklasse.InfoBenachrichtigung("Erf",clickedRow.toString()+" wurde "+auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key).toString()+" hinzugefügt");
                                    }
                                    else
                                    {
                                        if(label_partner.getText().equals(""))
                                        {
                                            label_partner.setText(clickedRow.toString());
                                            doppelpartner1=clickedRow;
                                            doppelspielklasse=auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key);
                                        }
                                        else
                                        {
                                            if(!label_partner.getText().equals(clickedRow.toString()))
                                            {
                                                Team team = new Team(doppelpartner1,clickedRow,auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key));
                                                auswahlklasse.getAktuelleTurnierAuswahl().getTeams().put(team.getTeamid(),team);
                                                auswahlklasse.InfoBenachrichtigung("Erf",team.toString()+" wurde "+auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key).toString()+" hinzugefügt");

                                                label_partner.setText("");
                                                doppelpartner1=null;
                                                doppelspielklasse=null;
                                            }
                                            else
                                            {
                                                auswahlklasse.WarnungBenachrichtigung("Gleicher Spieler","gleich");
                                            }
                                            System.out.println("t");
                                        }
                                    }

                                }
                            });
                            if(doppelspielklasse==null)
                            {
                                item4.getItems().addAll(i[index]);
                            }
                            if(doppelspielklasse!=null)
                            {
                                if(doppelspielklasse==auswahlklasse.getAktuelleTurnierAuswahl().getSpielklassen().get(key))
                                {
                                    item4.getItems().addAll(i[index]);
                                }

                            }

                            index++;
                        }

                    }


                    MenuItem item3 = new MenuItem("Spieler löschen");
                    item3.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {


                            boolean loeschespieler = clickedRow.getSpielerDAO().delete(clickedRow);
                            if(loeschespieler) {
                                obs_spieler.remove(clickedRow);
                                //auswahlklasse.getAktuelleSpielklassenAuswahl().getSetzliste().remove(clickedRow);
                                auswahlklasse.getSpieler().remove(clickedRow);
                                tabelle_spielerliste.refresh();
                                System.out.println("Lösche   " + clickedRow.getNName());
                                auswahlklasse.InfoBenachrichtigung("erf","erfolg");

                            }
                            else
                            {
                                auswahlklasse.WarnungBenachrichtigung("Fehler","fehler");
                            }
                        }
                    });
                    MenuItem item5= new MenuItem("Eigenschaften anzeigen");
                    item5.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                //auswahlklasse.setauswahlklasse.setSpielerzumHinzufeuegen(clickedRow);
                                spielerEigenschaftenAnzeigen();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    // Add MenuItem to ContextMenu
                    contextMenu.getItems().clear();

                    if(item4.getItems().size()>0)
                    {
                        contextMenu.getItems().addAll(item1, item2, item3,item4,item5);
                    }
                    else
                    {
                        contextMenu.getItems().addAll(item1, item2, item3,item5);
                    }


                    // When user right-click on Circle
                    tabelle_spielerliste.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

                        @Override
                        public void handle(ContextMenuEvent event) {

                            contextMenu.show(tabelle_spielerliste, event.getScreenX(), event.getScreenY());
                        }
                    });
                }
            });
            return row ;
        });
//endregion

        try {
            ladeVereine();
            combo_verein.getSelectionModel().select(0);

            printSpielerZuordnenTableNeu();
            spielerTabelleAktualisieren();
        } catch (Exception e) {
            e.printStackTrace();
        }




        if(Update)
        {

            tab_spbea.setDisable(true);
            tabpane_spieler.getSelectionModel().select(tab_sphin);
            FuelleFelder(auswahlklasse.getUpdateSpieler());
        }

        t_suchleistespielerhinzu.textProperty().addListener((observable, oldValue, newValue) -> {
            // System.out.println("textfield changed from " + oldValue + " to " + newValue);
            //obs_spieler.clear();

            obs_spieler.clear();

            tabelle_spielerliste.refresh();
            Enumeration e = auswahlklasse.getSpieler().keys();
            while (e.hasMoreElements()){
                int key = (int) e.nextElement();

                try {
                    if(auswahlklasse.getSpieler().get(key).toString().toUpperCase().contains(t_suchleistespielerhinzu.getText().toUpperCase()))
                    {
                        obs_spieler.add(auswahlklasse.getSpieler().get(key));
                        System.out.println(auswahlklasse.getSpieler().get(key));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                ;
            }


        });

    }

    @FXML
    public void pressBtn_ExcelImport (ActionEvent event) throws Exception {
        try {

            FileChooser fileChooser = new FileChooser();

            Stage stage = new Stage();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {

                if (ExcelImport.importExcelData(file.getAbsolutePath())) {
                    /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ExcelImportAbgeschlossen.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage2 = new Stage();
                    a.addStage(stage2);
                    stage2.setScene(new Scene(root1));
                    stage2.show();*/
                    if(auswahlklasse.getSpielererfolgreich().size()>0) {
                        String s ="";
                        Enumeration e = auswahlklasse.getSpielererfolgreich().keys();
                        while(e.hasMoreElements())
                        {
                            s+=e.nextElement().toString();
                            if(e.hasMoreElements())
                            {
                                s+=" --- ";
                            }
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Spielerimport - Neue Spieler");
                        alert.setHeaderText("Spieler erfolgreich eingelesen! ");
                        alert.setContentText(s);
                        alert.showAndWait();
                        //ExcelImport.setSpielererfolgreich(null);
                    }

                    if(auswahlklasse.getSpielerupdate().size()>0) {
                        Enumeration eu = auswahlklasse.getSpielerupdate().keys();
                        String s ="";
                        while(eu.hasMoreElements())
                        {
                            s+=eu.nextElement().toString();
                            if(eu.hasMoreElements())
                            {
                                s+=" --- ";
                            }
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Spielerimport - Update");
                        alert.setHeaderText("Spieler erfolgreich aktualisiert! ");
                        alert.setContentText(String.valueOf(s));
                        alert.showAndWait();
                        //ExcelImport.setSpielerupdate(null);
                    }
                    if(auswahlklasse.getObs_vereine_erfolgreich().size()>0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Vereinimport - Neue Vereine");
                        alert.setHeaderText("Vereine erfolgreich hinzugefügt ");
                        alert.setContentText(String.valueOf(auswahlklasse.getObs_vereine_erfolgreich()));
                        alert.showAndWait();
                        //ExcelImport.getObs_vereine_erfolgreich().clear();
                    }
                    //ExcelImport ex = new ExcelImport();
                    //ex.pressBtn_Popup();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Spielerimport");
                    alert.setHeaderText("Spieler konnten nicht eingelesen werden!");
                    alert.setContentText("Schade :(");

                    alert.showAndWait();
                }
                ExcelImport.resetteAlles();
            }
        }
        catch (Exception e)
        {

        }
    }
    private void FuelleFelder(Spieler clickedRow)
    {

        beschriftungUpdate();


        Update=true;
        tab_sphin.setDisable(false);
        tab_spbea.setDisable(true);

        tabpane_spieler.getSelectionModel().select(tab_sphin);
        t_vn.setText(clickedRow.getVName());
        t_nn.setText(clickedRow.getNName());
        d_geb.setValue(clickedRow.getGDatum());
        t_spid.setText(clickedRow.getExtSpielerID());
        t_re.setText(String.valueOf(clickedRow.getrPunkte()[0]));
        t_rd.setText(String.valueOf(clickedRow.getrPunkte()[1]));
        t_rm.setText(String.valueOf(clickedRow.getrPunkte()[2]));
        combo_verein.getSelectionModel().select(clickedRow.getVerein());
        if(clickedRow.getGeschlecht())
        {
            r_m.setSelected(true);
        }
        else
        {
            r_w.setSelected(true);
        }
    }

    private void beschriftungUpdate() {
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tab_spupdate");
            tab_sphin.setText(titel);
            titel = bundle.getString("btn_Spielerupdaten");
            b_spielerspeichern.setText(titel);

        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
    }
    private void beschriftungHinzu() {
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );
            titel = bundle.getString("tab_sphin");
            tab_sphin.setText(titel);
            titel = bundle.getString("btn_speichern");
            b_spielerspeichern.setText(titel);

        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
    }
    private void UpdateSpieler(Spieler clickedRow) {

        if(tabelle_spielerliste.getSelectionModel().getSelectedItem()!=null )
        {
            FuelleFelder(clickedRow);
            System.out.println("geklickt: "+clickedRow.getNName());

            System.out.println("turnier="+auswahlklasse.getAktuelleTurnierAuswahl().getName());

            spieler_neu=clickedRow;
        }

    }
    @FXML
    public void pressBtn_neuerVerein(ActionEvent event) throws Exception {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("neuerVerein.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();

            stage.setScene(new Scene(root1));
            stage.show();
            stage.setTitle("Neuer Verein");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void spielerEigenschaftenAnzeigen() throws Exception {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("spielerEigenschaften.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();

            stage.setScene(new Scene(root1));
            stage.show();
            stage.setTitle("Spieler Eigenschaften");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void pressBtn_Abbrechen(ActionEvent event) throws Exception {
        try {
            // a.getStages().get(0).close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void pressBtn_AbbrechenBearbeiten (ActionEvent event) throws Exception {
        try {
            tab_sphin.setDisable(false);
            tab_spbea.setDisable(false);

            tabpane_spieler.getSelectionModel().select(tab_spbea);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
