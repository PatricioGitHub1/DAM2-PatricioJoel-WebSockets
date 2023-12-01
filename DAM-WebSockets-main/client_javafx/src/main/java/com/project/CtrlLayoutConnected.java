package com.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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

public class CtrlLayoutConnected {

    @FXML
    private Label nameLabel1, nameLabel2;

    @FXML
    private Label pointsLabel1, pointsLabel2;

    @FXML
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16;

    private ArrayList<ImageView> imgs= new ArrayList<ImageView>();

    private boolean loadingImage = true;

    @FXML
    private void initialize() {
        pointsLabel1.setText("0");
        pointsLabel2.setText("0");
        imgs.add(img1); imgs.add(img2); imgs.add(img3); imgs.add(img4); imgs.add(img5); imgs.add(img6); imgs.add(img7); imgs.add(img8);
        imgs.add(img9); imgs.add(img10); imgs.add(img11); imgs.add(img12); imgs.add(img13); imgs.add(img14); imgs.add(img15); imgs.add(img16);
        loadImages();
    }

    private void loadImages() {
        Random random = new Random();
        for (int i=0; i < imgs.size(); i++) {
            imgs.get(i).setImage(new Image(getClass().getResource("/assets/" + "image" + (random.nextInt(4) + 1) + ".png").toString()));
        }
    }
    /*
    private void loadImageAtIndex(int index) {
        Random random = new Random();
        if (!loadingImage) {
            for (int i=0; i < imgs.size(); i++) {
                imgs.get(i).setImage(null);
            }
            this.loadingImage = true;
        }
        else {
            if (index < imgs.size()) {
                loadImageBackground((image) -> {
                    imgs.get(index).setImage(image); // Estableix una imatge per l'ImageView actual
                    int currentImageIndex = index + 1; // Actualiza l'índex de l'imatge actual
                    // Programa la càrrega de la pròxima imatge després d'un retard
                    Platform.runLater(() -> {
                        loadImageAtIndex(currentImageIndex);
                    });
                }, "image" + (random.nextInt(3) + 1) + ".png");
            }
        }
    }
    
    public void loadImageBackground(Consumer<Image> callBack, String imageName) {
        Random random = new Random();
        // Utilitza un thread per tal d'evitar bloquejar l'UI
        CompletableFuture<Image> futureImage = CompletableFuture.supplyAsync(() -> {
            try {
                // Espera entre 5 i 50 segons per simular un temps de càrrega llarg
                Thread.sleep(random.nextInt(3000) + 1000);
                // Carrega l'informació de la carpeta assets
                Image image = new Image(getClass().getResource("/assets/" + imageName).toString());
                return image;

            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        })
        .exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });

        futureImage.thenAcceptAsync(result -> {
            callBack.accept(result);
        }, Platform::runLater);
    }
    */
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
