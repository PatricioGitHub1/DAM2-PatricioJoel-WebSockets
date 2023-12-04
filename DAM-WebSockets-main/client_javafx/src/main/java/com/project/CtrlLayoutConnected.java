package com.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.project.AppData.ConnectionStatus;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CtrlLayoutConnected {

    @FXML
    private Label nameLabel1, nameLabel2;

    @FXML
    private Label pointsLabel1, pointsLabel2;

    @FXML
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16;

    private ArrayList<ImageView> imgs= new ArrayList<ImageView>();

    private AppData appData = AppData.getInstance();

    // private JSONObject board = appData.getNewBoard();

    @FXML
    private void initialize() {
        pointsLabel1.setText("0");
        pointsLabel2.setText("0");
        imgs.add(img1); imgs.add(img2); imgs.add(img3); imgs.add(img4); imgs.add(img5); imgs.add(img6); imgs.add(img7); imgs.add(img8);
        imgs.add(img9); imgs.add(img10); imgs.add(img11); imgs.add(img12); imgs.add(img13); imgs.add(img14); imgs.add(img15); imgs.add(img16);
        
        // Afegim un event del tipus onMouseClicked per cadascuna de les ImageViews
        for (ImageView img : imgs) {
            img.setImage(new Image(getClass().getResource("/assets/back_image.png").toString()));
            img.setOnMouseClicked(this::handleImageViewClick);
        }
        loadImages(appData.newBoard());
    }

    // Carreguem les imatges en els ImageViews corresponents
    private void loadImages(List<List<Integer>> rowsList) {
        for (int i = 0; i < imgs.size(); i++) {
            int row = i / rowsList.get(0).size();  // Calculem l'índex de la fila corresponent
            int col = i % rowsList.get(0).size();  // Calculem l'índex de la columna corresponent
            int imageNumber = rowsList.get(row).get(col);  // Obtenim el número de la matriu
            String imageName = "image" + imageNumber + ".png"; // Obtenim la ruta de la imatge
            imgs.get(i).setImage(new Image(getClass().getResource("/assets/" + imageName).toString())); // Establim una imatge en l'ImageView actual
            imgs.get(i).setId(imageName);  // Establim l'ID per emmagatzemar el nom de la imatge
            imgs.get(i).setOnMouseClicked(this::handleImageViewClick);  // Establim l'event
        }
    }

    // Manegem l'event de l'ImageView
    private void handleImageViewClick(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getSource();
        System.out.println("Soc l'ImageView " + clickedImageView.getId() + "!");
    }
    
    /*
    @FXML
    private Label serverAddressLabel;

    @FXML
    private Label clientIdLabel;

    @FXML
    private TextArea messagesArea;

    @FXML
    private ListView<String> clientsList;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    public void initialize() {

        clientsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        clientsList.setOnMouseClicked(event -> {

            // Set new selection (or deselect)
            AppData appData = AppData.getInstance();
            int clickedIndex = clientsList.getSelectionModel().getSelectedIndex();
            appData.selectClient(clickedIndex); 

            // Get real selection (can be unset)
            Integer selectedIndex = appData.getSelectedClientIndex();
            if (selectedIndex != null) {
                sendButton.setText("Send");
            } else {
                sendButton.setText("Broadcast");
            }
            sendButton.requestFocus();

            // De-select all
            for (int i = 0; i < clientsList.getItems().size(); i++) {
                clientsList.getSelectionModel().clearSelection(i);
            }

            appData.updateClientList();
        });
    
        clientsList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    AppData appData = AppData.getInstance();
                    Integer selectedIndex = appData.getSelectedClientIndex();
                    if (selectedIndex != null && selectedIndex.intValue() == getIndex()) {
                        setStyle("-fx-background-color: #14b5ff; -fx-text-fill: white;");
                    } else {
                        setStyle(null);
                    }
                }
            }
        });
    }
    */
    @FXML
    private void handleDisconnect(ActionEvent event) {
        AppData appData = AppData.getInstance();
        appData.disconnectFromServer();
    }
    /*
    @FXML
    private void handleSend(ActionEvent event) {
        AppData appData = AppData.getInstance();
        String message = messageField.getText();
        appData.send(message);
        messageField.clear();
    }
    public void updateInfo() {
        AppData appData = AppData.getInstance();
        serverAddressLabel.setText("ws://" + appData.getIp() + ":" + appData.getPort());
        clientIdLabel.setText(appData.getMySocketId());
    }
    
    public void updateMessages(String messages) {
        messagesArea.setText(messages);
    }

    public void updateClientList(List<String> clients) {
        Platform.runLater(() -> {
            clientsList.setItems(FXCollections.observableArrayList(clients));
        });
    }
    */
}
