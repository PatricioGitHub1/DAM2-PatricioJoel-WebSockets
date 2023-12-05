package com.project;

import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CtrlLayoutDisconnected {

    @FXML
    private AnchorPane refAnchorPane;

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField nameTextField;

    private AppData appData;

    Rectangle rect = new Rectangle();
    Circle circle = new Circle();

    public void initialize() {
        appData = AppData.getInstance();

        try {
            appData.setIp(appData.getLocalIPAddress());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ipTextField.setText(appData.getIp());
        portTextField.setText(appData.getPort());
        nameTextField.setText(appData.getName());
    }

    @FXML
    private void connectToServer() {
        if (nameTextField.getText().isEmpty() || nameTextField.getText().isBlank()) {
            return;
        }
        appData.setIp(ipTextField.getText());
        appData.setPort(portTextField.getText());
        appData.setName(nameTextField.getText());
        appData.connectToServer();

    }
}
