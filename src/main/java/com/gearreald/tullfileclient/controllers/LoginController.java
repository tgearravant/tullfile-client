package com.gearreald.tullfileclient.controllers;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.ServerConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {
	@FXML private Text headerText;
    @FXML private TextField serverAddressField;
    @FXML private TextField portField;
    @FXML private PasswordField keyField;
    @FXML private Text actionTarget;
	@FXML private CheckBox sslBox;
        
    @FXML public void initialize(){
    	autofillDefaults();
    }
    
    @FXML public void keyListener(KeyEvent event) {
    	if(event.getCode() == KeyCode.ENTER){
    		event.consume();
    		changeToInterface();
    	}
    }
    
    @FXML public void handleSubmitButtonAction(ActionEvent event) {
    	changeToInterface();
    }
    
    private void changeToInterface(){
    	String serverAddress = this.serverAddressField.getText()+":"+this.portField.getText()+"/";
    	String apiKey = this.keyField.getText();
    	Environment.setConfiguration("HOSTNAME", serverAddress);
    	Environment.setConfiguration("API_KEY", apiKey);
    	Environment.setConfiguration("USE_SSL", Boolean.toString(this.sslBox.isSelected()));
    	try {
			ServerConnection.checkKey();
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
    	Stage stage = Environment.getPrimaryStage();
		Scene scene = new Scene(root,600,400);
    	stage.setScene(scene);
    }
    
    private void autofillDefaults(){
    	this.portField.setText(Environment.getConfiguration("default_port"));
		this.serverAddressField.setText(Environment.getConfiguration("default_host"));
		this.keyField.setText(Environment.getConfiguration("default_key"));
		if(Environment.getConfiguration("default_ssl").equals("true")){
			this.sslBox.setSelected(true);
		}else{
			this.sslBox.setSelected(false);
		}
    }
}
