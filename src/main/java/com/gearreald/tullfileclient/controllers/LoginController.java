package com.gearreald.tullfileclient.controllers;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.lang3.tuple.Pair;

import com.gearreald.tullfileclient.Environment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.tullco.tullutils.NetworkUtils;

public class LoginController {
	@FXML private Text headerText;
    @FXML private TextField serverAddressField;
    @FXML private TextField portField;
    @FXML private PasswordField keyField;
    @FXML private Text actionTarget;
    
    public final static String DEFAULT_PORT="12345";
    
    @FXML public void initialize(){
    	portField.setText(DEFAULT_PORT);
    }
    
    @FXML public void handleSubmitButtonAction(ActionEvent event) {
    	
    	//First, let's see if a server actually exists with the given details.
    	String serverAddress = "http://"+this.serverAddressField.getText()+":"+this.portField.getText()+"/";
    	String apiKey = this.keyField.getText();
    	Environment.setConfiguration("HOSTNAME", serverAddress);
    	Environment.setConfiguration("API_KEY", apiKey);
    	try {
			NetworkUtils.getDataFromURL(serverAddress, false, NetworkUtils.HEAD, Pair.of("Authorization", apiKey));
		} catch (MalformedURLException e) {
			actionTarget.setText("Invalid Server Address.");
			System.err.println(e.getMessage());
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			if(e.getMessage().startsWith("Conn")){
				actionTarget.setText("Could not Connect.");
			}
			else{
				actionTarget.setText("Could not Authenticate.");
			}
			return;
		}
    	
    	//If we successfully found one that responded correctly, let's switch to the main Interface.
    	Parent root;
    	try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/interface.fxml"));
		} catch (IOException e) {
			actionTarget.setText("Really bad error...");
			e.printStackTrace();
			return;
		}
    	Stage stage = (Stage) actionTarget.getScene().getWindow();
		Scene scene = new Scene(root,600,400);
    	stage.setScene(scene);
    }
}
