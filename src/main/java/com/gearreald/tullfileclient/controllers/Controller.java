package com.gearreald.tullfileclient.controllers;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.lang3.tuple.Pair;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import net.tullco.tullutils.NetworkUtils;

public class Controller {
	@FXML private Text headerText;
    @FXML private TextField serverAddressField;
    @FXML private TextField portField;
    @FXML private PasswordField keyField;
    @FXML private Text actionTarget;
    
    @FXML public void handleSubmitButtonAction(ActionEvent event) {
    	
    	//First, let's see if a server actually exists with the given details.
    	String serverAddress = "http://"+this.serverAddressField.getText()+":"+this.portField.getText();
    	String apiKey = this.keyField.getText();
    	try {
			NetworkUtils.getDataFromURL(serverAddress, false, NetworkUtils.HEAD, Pair.of("Authorization", apiKey));
		} catch (MalformedURLException e) {
			actionTarget.setText("Invalid Server Address.");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			if(e.getMessage().startsWith("Conn")){
				actionTarget.setText("Could not Connect.");
			}
			else{
				actionTarget.setText("Could not Authenticate.");
			}
		}
    	
    	//If we successfully found one that responded correctly, let's switch to the Tull
    }
}
