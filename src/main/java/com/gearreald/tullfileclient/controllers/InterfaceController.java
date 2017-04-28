package com.gearreald.tullfileclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class InterfaceController {

	@FXML public Text header;
	
	@FXML public void initialize(){
		header.setText("TullFile Server V0.1");
	}
}
