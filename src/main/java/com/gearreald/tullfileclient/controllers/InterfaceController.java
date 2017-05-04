package com.gearreald.tullfileclient.controllers;

import java.io.File;
import java.io.IOException;

import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.models.TullFolder;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class InterfaceController {

	@FXML public BorderPane borderPane;
	@FXML public Text header;
	@FXML public ListView<GridPane> fileList;
	@FXML public Button refreshButton;
	@FXML public Button exitButton;
	@FXML public Button uploadButton;
	
	public ObservableList<GridPane> boxList = FXCollections.<GridPane>observableArrayList();
	
	public TullFolder current;
	
	@FXML public void initialize(){
		this.header.setText("TullFile Server V0.1");
		this.fileList.setItems(boxList);
		setDisplayFolder(new TullFolder("/"),false);
		fileList.setItems(boxList);
	}
	@FXML public void exitApplication(ActionEvent event){
		Platform.exit();
	}
	@FXML public void refreshFolder(ActionEvent event){
		this.setDisplayFolder(this.current,true);
	}
	@FXML public void uploadFile(ActionEvent event){
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(borderPane.getScene().getWindow());
		System.out.println(file.getAbsolutePath());
	}
	public void setDisplayFolder(TullFolder f,boolean forceRefresh){
		this.current=f;
		try{
			this.current.fetchFolderData(forceRefresh);
		}catch(IOException e){
			e.printStackTrace();
		}
		this.boxList.clear();
		for(TullFile file : this.current.getFiles())
			addTullFile(file);
		for(TullFolder subfolder : this.current.getSubfolders())
			addTullFolder(subfolder);
	}
	private void addTullFile(TullFile f){
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/fileListItem.fxml"));
		try {
			boxList.add(loader.<GridPane>load());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileViewController controller = loader.<FileViewController>getController();
		controller.setTullFile(f);
	}
	private void addTullFolder(TullFolder f){
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/folderListItem.fxml"));
		try {
			boxList.add(loader.<GridPane>load());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FolderViewController controller = loader.<FolderViewController>getController();
		controller.setTullFolder(f);
		
	}
}
