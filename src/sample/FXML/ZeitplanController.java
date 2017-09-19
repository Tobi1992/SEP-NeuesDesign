package sample.FXML;

import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;

/**
 * Created by jens on 19.09.2017.
 */
public class ZeitplanController {


    @FXML
    private GridPane grid_zeitplan;

    @FXML
    private Label Objekt1;

    @FXML
    private Label Objekt2;

    @FXML
    private Label Objekt3;

    @FXML
    void DragvomObjekt(MouseEvent event) {

    }

    @FXML
    void dragaufObjekt(DragEvent event) {

    }

    @FXML
    void label1dragdetected(MouseEvent event) {

            Dragboard db = Objekt1.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(Objekt1.getText());
            db.setContent(content);

            event.consume();
    }

    @FXML
    void label1dragdropped(DragEvent event) {

        String str = event.getDragboard().getString();

        if(str.equals(Objekt2.getText()))
        {


            Objekt2.setText(Objekt1.getText());
            Objekt1.setText(str);

        }
        if(str.equals(Objekt3.getText()))
        {

            Objekt3.setText(Objekt1.getText());
            Objekt1.setText(str);
        }


    }
    @FXML
    void label1dragover(DragEvent event) {
        if(event.getDragboard().hasString())
        {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    @FXML
    void label2dragover(DragEvent event) {
        if(event.getDragboard().hasString())
        {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    @FXML
    void label3dragover(DragEvent event) {
        if(event.getDragboard().hasString())
        {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    @FXML
    void label2dragdetected(MouseEvent event) {
        Dragboard db = Objekt2.startDragAndDrop(TransferMode.ANY);

        ClipboardContent content = new ClipboardContent();
        content.putString(Objekt2.getText());
        db.setContent(content);

        event.consume();
    }

    @FXML
    void label2dragdropped(DragEvent event) {
        String str = event.getDragboard().getString();
        if(str.equals(Objekt1.getText()))
        {


            Objekt1.setText(Objekt2.getText());
            Objekt2.setText(str);

        }
        if(str.equals(Objekt3.getText()))
        {

            Objekt3.setText(Objekt2.getText());
            Objekt2.setText(str);
        }
    }

    @FXML
    void label3dragdetected(MouseEvent event) {
        Dragboard db = Objekt3.startDragAndDrop(TransferMode.ANY);

        ClipboardContent content = new ClipboardContent();
        content.putString(Objekt3.getText());
        db.setContent(content);

        event.consume();
    }

    @FXML
    void label3dragdropped(DragEvent event) {
        String str = event.getDragboard().getString();
        if(str.equals(Objekt1.getText()))
        {


            Objekt1.setText(Objekt3.getText());
            Objekt3.setText(str);

        }
        if(str.equals(Objekt2.getText()))
        {

            Objekt2.setText(Objekt3.getText());
            Objekt3.setText(str);
        }
    }




}