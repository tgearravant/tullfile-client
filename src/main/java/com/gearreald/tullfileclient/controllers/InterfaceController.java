package com.gearreald.tullfileclient.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.ErrorDialogBox;
import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.models.TullObject;
import com.gearreald.tullfileclient.utils.ResourceLoader;
import com.gearreald.tullfileclient.utils.SystemUtils;

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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class InterfaceController {

	@FXML public BorderPane borderPane;
	@FXML public Text header;
	@FXML public ListView<BorderPane> fileList;
	@FXML public Button homeButton;
	@FXML public Button backButton;
	@FXML public Button newFolderButton;
	@FXML public Button refreshButton;
	@FXML public Button exitButton;
	@FXML public Button uploadButton;
	
	public ObservableList<BorderPane> boxList;
	public List<ItemListController> itemControllers;
	
	public TullFolder current;
	
	@FXML private void initialize(){
		this.boxList = FXCollections.<BorderPane>observableArrayList();
		this.itemControllers = new CopyOnWriteArrayList<ItemListController>();
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
		this.refreshCurrentFolder();
	}
	@FXML private void uploadFile(ActionEvent event){
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File (SystemUtils.getUserDirectory()));
		List<File> files = chooser.showOpenMultipleDialog(Environment.getPrimaryStage());
		if(files!=null){
			for(File file: files){
				if(file.isDirectory()){
					ErrorDialogBox.dialogFor(new Exception("Cannot currently upload directories."));
				}else{
					TullFile.queueAllPiecesForUpload(file, this.current.getLocalPath(), file.getName());
				}
			}
		}
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
	public void refreshCurrentFolder(){
		this.setDisplayFolder(this.current, true);
	}
	public void updateProgressOfTullFile(TullFile file){
		for(ItemListController controller:this.itemControllers){
			if(file.equals(controller.getTullObject())){
				controller.setProgress(file.getDownloadProgress());
			}
		}
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
		for(TullFolder subfolder : this.current.getSubfolders()){
			this.addTullObject(subfolder);
		}
		for(TullFile file : this.current.getFiles()){
			this.addTullObject(file);
		}
	}
	private void addTullObject(TullObject object){
		FXMLLoader loader = new FXMLLoader(ResourceLoader.getResourceURL("fxml/listItem.fxml"));
		try {
			boxList.add(loader.<BorderPane>load());
		} catch (IOException e) {
			ErrorDialogBox.dialogFor(e);
		}
		ItemListController controller = loader.<ItemListController>getController();
		this.itemControllers.add(controller);
		controller.setTullObject(object);
	}
}
