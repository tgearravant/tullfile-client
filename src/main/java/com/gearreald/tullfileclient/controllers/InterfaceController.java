package com.gearreald.tullfileclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class InterfaceController {

	@FXML public Text header;
	@FXML public ListView<HBox> fileList;
	@FXML public Button refreshButton;
	@FXML public Button exitButton;
	@FXML public Button downloadButton;
	@FXML public Button uploadButton;
	public ObservableList<HBox> boxList = FXCollections.observableArrayList();
	
	@FXML public void initialize(){
		header.setText("TullFile Server V0.1");
		
		fileList.setItems(boxList);
	}
	@FXML public void exitApplication(ActionEvent event){
		System.out.println("oops");
		Platform.exit();
	}
}
