package sample.FXML;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import sample.*;
import sample.DAO.*;

import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static sample.DAO.auswahlklasse.getTurnierzumupdaten;

/**
 * Created by jens on 26.09.2017.
 */
public class VereinsuebersichtController implements Initializable {

    String baseName = "resources.Main";
    String titel ="";

    ContextMenu contextMenu=new ContextMenu();

    @FXML
    private TableView tabelle_vereine;
    @FXML
    private JFXTextField vereinsuche;
    @FXML
    public TableColumn Vereinsname;
    @FXML
    public TableColumn Vereinsverband;
    @FXML
    public TableColumn Vereinsextvereinsid;
    @FXML
    private JFXButton Btn_NeuerVerein;
    @FXML
    private Tab tab_Vereinsuebersicht;
    @FXML
    private Tab tab_Startgeldliste;

    public void SpracheLaden()
    {
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( baseName );

            titel = bundle.getString("Vereinsname");
            Vereinsname.setText(titel);

            titel = bundle.getString("Vereinsverband");
            Vereinsverband.setText(titel);

            titel = bundle.getString("Vereinsextvereinsid");
            Vereinsextvereinsid.setText(titel);

            titel = bundle.getString("Btn_NeuerVerein");
            Btn_NeuerVerein.setText(titel);

            titel = bundle.getString("vereinsuche");
            vereinsuche.setPromptText(titel);
            vereinsuche.setLabelFloat(true);

            titel = bundle.getString("tab_Vereinsuebersicht");
            tab_Vereinsuebersicht.setText(titel);

            titel = bundle.getString("tab_Startgeldliste");
            tab_Startgeldliste.setText(titel);

        }
        catch ( MissingResourceException e ) {
            System.err.println( e );
        }
    }

    ObservableList <Verein> obs_Vereine  = FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SpracheLaden();

        auswahlklasse.setVereinsuebersichtController(this);
        fulleObsVereine();
        zeigeTabelle();
        tabelleListener();


        vereinsuche.textProperty().addListener((observable, oldValue, newValue) -> {
            // System.out.println("textfield changed from " + oldValue + " to " + newValue);
            //obs_spieler.clear();
            obs_Vereine.clear();
            //auswahlklasse.getTurniere().clear();

            Enumeration enumeration = auswahlklasse.getVereine().keys();
            while (enumeration.hasMoreElements())
            {
                String key = (String) enumeration.nextElement();
                if(auswahlklasse.getVereine().get(key).toString().toUpperCase().contains(vereinsuche.getText().toUpperCase()))
                {
                    obs_Vereine.add((Verein) auswahlklasse.getVereine().get(key));
                }

            }

            tabelle_vereine.refresh();

            tabelle_vereine.setItems(obs_Vereine);
            //auswahlklasse.setTurniere(obs_turniere_anzeige);



        });


    }

    private void tabelleListener() {
        tabelle_vereine.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                Verein clickedRow = (Verein) row.getItem();
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {

                    auswahlklasse.getDashboardController().setNodeNeuerVerein();
                    auswahlklasse.getNeuer_vereinController().setUpdateverein(clickedRow);
                    auswahlklasse.getNeuer_vereinController().updateVerein();
                    //   a.getStagesdict().get("")
                }
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                } else if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {


                    //System.out.println("R-KLICK");
                    MenuItem item1 = new MenuItem("Verein bearbeiten");
                    item1.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {


                            auswahlklasse.getDashboardController().setNodeNeuerVerein();
                            auswahlklasse.getNeuer_vereinController().setUpdateverein(clickedRow);
                            auswahlklasse.getNeuer_vereinController().updateVerein();
                            //tabpane_spieler.getSelectionModel().select(tab_sphin);
                        }
                    });
                    MenuItem item3 = new MenuItem("Verein löschen");
                    item3.setOnAction(new EventHandler<ActionEvent>() {


                        @Override
                        public void handle(ActionEvent event) {
                        boolean erfolg = clickedRow.getVereinDAO().delete(clickedRow);


                        if(erfolg)
                        {
                            auswahlklasse.InfoBenachrichtigung("Löschen erf","erf");
                            obs_Vereine.remove(clickedRow);
                            tabelle_vereine.getItems().remove(clickedRow);
                            auswahlklasse.getVereine().remove(clickedRow.getExtVereinsID());
                            for (int i=0;i<auswahlklasse.getObs_spieler().size();i++){
                                Spieler spieler = auswahlklasse.getObs_spieler().get(i);
                                if (spieler.getVerein()==clickedRow){
                                    spieler.setVerein(null);
                                }
                            }
                        }
                        else
                        {
                            auswahlklasse.WarnungBenachrichtigung("Fehler","Verein enthält Spieler");
                        }

                        }
                    });
                    if (!contextMenu.isShowing()) {
                        contextMenu.getItems().clear();
                        contextMenu.getItems().addAll(item1, item3);
                    }
                    tabelle_vereine.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

                        @Override
                        public void handle(ContextMenuEvent event) {

                            if (!contextMenu.isShowing()) {
                                contextMenu.show(tabelle_vereine, event.getScreenX(), event.getScreenY());
                            }

                        }
                    });
                }
            });

            return row;
        });
    }

    public void fulleObsVereine()
    {
        obs_Vereine.clear();
        Enumeration enumeration = auswahlklasse.getVereine().keys();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            obs_Vereine.add((Verein) auswahlklasse.getVereine().get(key));
        }
    tabelle_vereine.setItems(obs_Vereine);
    }
    @FXML
    private void zeigeTabelle() {
        //System.out.println("Print table");





        if(vereinsuche.getText().equals(""))
        {

        }
        else
        {

        }
        tabelle_vereine.setItems(obs_Vereine);/**/
        Vereinsname.setCellValueFactory(new PropertyValueFactory<Verein,String>("name"));

        //TurnierIDSpalte.setCellFactory(integerCellFactory);
        Vereinsverband.setCellValueFactory(new PropertyValueFactory<Verein,String>("verband"));
        Vereinsextvereinsid.setCellValueFactory(new PropertyValueFactory<Verein,String>("ExtVereinsID"));

//        tabelle_vereine.getColumns().addAll(Vereinsname,Vereinsverband,Vereinsextvereinsid);

        //TurnierDatumSpalte.setCellValueFactory(new PropertyValueFactory<Turnier, Date>("datum"));
        //TurnierNameSpalte.setCellValueFactory(new PropertyValueFactory<Turnier, String>("name"));

        //TurnierIDSpalte.setCellValueFactory(new PropertyValueFactory<Turnier, Integer>("turnierid"));

    }
    @FXML
    public void btn_neueklasse(ActionEvent event)
    {
        auswahlklasse.getDashboardController().setNodeNeuerVerein();
    }
}
