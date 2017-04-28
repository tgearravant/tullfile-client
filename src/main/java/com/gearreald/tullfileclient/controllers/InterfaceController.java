package com.gearreald.tullfileclient.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class InterfaceController {

	@FXML public Text header;
	@FXML public ListView<HBox> fileList;
	public ObservableList<HBox> boxList = FXCollections.observableArrayList();
	
	@FXML public void initialize(){
		header.setText("TullFile Server V0.1");
		
		fileList.setItems(boxList);
	}
}
