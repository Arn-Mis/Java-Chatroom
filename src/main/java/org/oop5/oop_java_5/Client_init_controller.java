package org.oop5.oop_java_5;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.oop5.oop_java_5.Singleton;
import org.oop5.oop_java_5.UserData;

import java.io.IOException;

public class Client_init_controller {
    @FXML
    TextField serverIP, serverPort, userName;
    @FXML
    Button connectButton;


    public void onConnectAction(ActionEvent actionEvent) throws IOException {
        UserData data = new UserData(userName.getText(), serverIP.getText(), serverPort.getText());
        Singleton instance = Singleton.getInstance();
        instance.setUserData(data);

        Node node1 = (Node) actionEvent.getSource();
        Stage stage1 = (Stage) node1.getScene().getWindow();
        stage1.setUserData(instance);
        stage1.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Clientside_chat.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage1.setScene(scene);
        stage1.show();
    }
}