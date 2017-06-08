package com.gearreald.tullfileclient.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.job.LoadTullFolderData;
import com.gearreald.tullfileclient.job.QueueUpload;
import com.gearreald.tullfileclient.models.ErrorDialogBox;
import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.models.TullObject;
import com.gearreald.tullfileclient.utils.ResourceLoader;
import com.gearreald.tullfileclient.utils.SystemUtils;
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
					WorkerQueues.addJobToQueue("quick", new QueueUpload(file, this.current.getLocalPath(), file.getName()));
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
	public void refreshDisplay(){
		List<TullObject> tullObjects = new ArrayList<TullObject>();
		for(TullFolder subfolder : this.current.getSubfolders()){
			tullObjects.add(subfolder);
		}
		for(TullFile file : this.current.getFiles()){
			tullObjects.add(file);
		}
		for(TullObject item: tullObjects){
			if(!containsTullObject(item)){
				addTullObject(item);
			}
		}
		for(TullObject item: getListOfContainedTullObjects()){
			if(!tullObjects.contains(item))
				removeTullObject(item);
		}
		for(ItemListController controller: this.itemControllers){
			controller.refresh();
		}
	}
	public void setDisplayFolder(TullFolder f,boolean forceRefresh){
		this.current=f;
		WorkerQueues.addJobToQueue("quick", new LoadTullFolderData(this.current,forceRefresh));
		refreshDisplay();
	}
	private List<TullObject> getListOfContainedTullObjects(){
		List<TullObject> objects = new ArrayList<TullObject>();
		for(ItemListController controller: this.itemControllers){
			objects.add(controller.getTullObject());
		}
		return objects;
	}
	private boolean containsTullObject(TullObject object){
		for(ItemListController controller: this.itemControllers){
			if(controller.getTullObject().equals(object)) {
				return true;
			}
		}
		return false;
	}
	private void addTullObject(TullObject object){
		if(containsTullObject(object))
			return;
		FXMLLoader loader = new FXMLLoader(ResourceLoader.getResourceURL("fxml/listItem.fxml"));
		BorderPane pane=null;
		try {
			pane = loader.<BorderPane>load();
		} catch (IOException e) {
			ErrorDialogBox.dialogFor(e);
		}
		if(pane!=null){
			ItemListController controller = loader.<ItemListController>getController();
			controller.setTullObject(object);
			this.itemControllers.add(controller);
			this.itemControllers.sort(null);
			boxList.add(this.itemControllers.indexOf(controller), pane);
		}
	}
	private void removeTullObject(TullObject object){
		ItemListController container=null;
		for(ItemListController controller: this.itemControllers){
			if(controller.getTullObject().equals(object)) {
				container=controller;
			}
		}
		if(container==null)
			return;
		int index = this.itemControllers.indexOf(container);
		this.itemControllers.remove(index);
		this.boxList.remove(index);
	}
}
