package com.gearreald.tullfileclient.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.job.UploadFile;
import com.gearreald.tullfileclient.models.ErrorDialogBox;
import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.worker.WorkerQueues;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class InterfaceController {

	@FXML public BorderPane borderPane;
	@FXML public Text header;
	@FXML public ListView<GridPane> fileList;
	@FXML public Button homeButton;
	@FXML public Button backButton;
	@FXML public Button newFolderButton;
	@FXML public Button refreshButton;
	@FXML public Button exitButton;
	@FXML public Button uploadButton;
	
	public ObservableList<GridPane> boxList = FXCollections.<GridPane>observableArrayList();
	
	public TullFolder current;
	
	@FXML private void initialize(){
		this.header.setText("TullFile Server V0.1");
		this.fileList.setItems(boxList);
		setDisplayFolder(new TullFolder("/"),false);
		fileList.setItems(boxList);
		Environment.setInterfaceController(this);
	}
	@FXML private void exitApplication(ActionEvent event){
		Platform.exit();
	}
	@FXML private void refreshFolder(ActionEvent event){
		this.setDisplayFolder(this.current,true);
	}
	@FXML private void uploadFile(ActionEvent event){
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(borderPane.getScene().getWindow());
		if(file!=null)
			WorkerQueues.addJobToQueue("upload", new UploadFile(file,current.getLocalPath(),file.getName()));
	}
	public void setDisplayFolder(TullFolder f){
		setDisplayFolder(f,false);
	}
	public void setDisplayFolder(TullFolder f,boolean forceRefresh){
		this.current=f;
		try{
			this.current.fetchFolderData(forceRefresh);
		}catch(IOException e){
			ErrorDialogBox.dialogFor(e);
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
			ErrorDialogBox.dialogFor(e);
		}
		FileViewController controller = loader.<FileViewController>getController();
		controller.setTullFile(f);
	}
	private void addTullFolder(TullFolder f){
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/folderListItem.fxml"));
		try {
			boxList.add(loader.<GridPane>load());
		} catch (IOException e) {
			ErrorDialogBox.dialogFor(e);
		}
		FolderViewController controller = loader.<FolderViewController>getController();
		controller.setTullFolder(f);
	}
	@FXML private void toHomeFolder(){
		this.setDisplayFolder(this.current.getRootFolder());
	}
	@FXML private void toParentFolder(){
		TullFolder parentFolder = this.current.getParentFolder();
		if(parentFolder == null){
			parentFolder = this.current;
		}
		this.setDisplayFolder(parentFolder);
	}
	@FXML private void createNewFolder(){
		TextInputDialog dialog = new TextInputDialog("New Folder");
		dialog.setTitle("Create a New Folder");
		dialog.setContentText("Enter a name for the new folder:");
		dialog.setHeaderText(null);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			String newFolderName = result.get();
			try {
				ServerConnection.createNewFolder(current.getLocalPath(), newFolderName);
			} catch (IOException e) {
				ErrorDialogBox.dialogFor(e);
			}
			this.setDisplayFolder(current, true);
		}
	}
}
