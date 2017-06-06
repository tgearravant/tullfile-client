package com.gearreald.tullfileclient.controllers;

import java.io.File;
import java.util.Optional;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.ConfirmationDialog;
import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.models.TullObject;
import com.gearreald.tullfileclient.utils.ImageUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.ProgressBar;

public class ItemListController implements Comparable<ItemListController> {
	
	private TullObject f;
	@FXML private Label titleLabel;
	@FXML private ImageView iconImage;
	@FXML private Label pieceLabel;
	@FXML private Text pieceText;
	@FXML private Button deleteButton;
	@FXML private Button openButton;
	@FXML private Button downloadButton;
	@FXML private Label sizeLabel;
	@FXML private Text sizeText;
	@FXML private ProgressBar downloadProgressBar;
	
	public void setTullObject(TullObject f){
		this.f=f;
		bindManagedAndVisible(this.openButton, this.downloadButton, this.downloadProgressBar, this.pieceLabel, this.pieceText, this.sizeLabel, this.sizeText);
		this.openButton.managedProperty().bind(this.openButton.visibleProperty());
		this.downloadButton.managedProperty().bind(this.downloadButton.visibleProperty());
		this.downloadProgressBar.managedProperty().bind(this.downloadProgressBar.visibleProperty());
		this.titleLabel.setText(this.f.getName());
		this.downloadProgressBar.setVisible(false);
		if(this.f.isTullFile()){
			this.iconImage.setImage(ImageUtils.getImage("file-icon.png"));
			this.downloadButton.setVisible(true);
		}
		if(this.f.isTullFolder()){
			this.iconImage.setImage(ImageUtils.getImage("folder-icon.png"));
			this.openButton.setVisible(true);
			this.pieceLabel.setText("Folders:");
			this.sizeLabel.setText("Files:");
		}
		refresh();
	}
	private void bindManagedAndVisible(Node... toBind){
		for(Node n: toBind){
			n.managedProperty().bind(n.visibleProperty());
		}
	}
	public void initialize(){
	}
	public void refresh(){
		if(this.f.isTullFile()){
			TullFile tullFile = (TullFile) this.f;
			this.pieceText.setText(Integer.toString(tullFile.getPieceCount()));
			this.sizeText.setText(tullFile.getFileSizeAsString());
		}
		if(this.f.isTullFolder()){
			TullFolder tullFolder = (TullFolder) this.f;
			this.pieceText.setText(Integer.toString(tullFolder.getSubfolders().size()));
			this.sizeText.setText(Integer.toString(tullFolder.getFiles().size()));
		}
	}
	@FXML
	public void downloadFile(ActionEvent e){
		TullFile tullFile = (TullFile) this.f;
		FileChooser chooser = new FileChooser();
		chooser.setInitialFileName(this.getTullObject().getName());
		String suffix = tullFile.getSuffix();
        ExtensionFilter extFilter = new FileChooser.ExtensionFilter(suffix.toUpperCase()+" files (*."+suffix+")", "*."+suffix);
        chooser.getExtensionFilters().add(extFilter);
        extFilter = new ExtensionFilter("All files","*.*");
        chooser.getExtensionFilters().add(extFilter);
		File file = chooser.showSaveDialog(Environment.getPrimaryStage());
		if(file!=null){
			tullFile.queueAllPiecesForDownload(file);
			this.downloadProgressBar.setProgress(0);
			this.downloadProgressBar.setVisible(true);
		}
	}
	@FXML
	public void delete(ActionEvent e){
		Alert a = new ConfirmationDialog("Delete Confirmation", "Are you sure you want to delete this item? (There is no undo.)");
		Optional<ButtonType> response = a.showAndWait();
		if(response.get() == ButtonType.OK){
			if(this.f.isTullFolder()){
				a = new ConfirmationDialog("Delete Confirmation", "You're requesting a directory delete. Are you /SUPER/ sure? All subfiles will also be irrevocably trashed.");
				response = a.showAndWait();
				if(response.get() != ButtonType.OK){
					return;
				}
			}
		}else{
			return;
		}
		if(this.f.delete())
			Environment.getInterfaceController().refreshCurrentFolder();
	}
	@FXML 
	public void changeInterfaceTullFolder(){
		if(!this.f.isTullFolder())
			return;
		InterfaceController controller = Environment.getInterfaceController();
		controller.setDisplayFolder((TullFolder)this.f, false);
	}
	public void setProgress(double d) {
		this.downloadProgressBar.setProgress(d);
		if(d>=0.999999d)
			this.downloadProgressBar.setVisible(false);
	}
	public TullObject getTullObject(){
		return this.f;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((f == null) ? 0 : f.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemListController other = (ItemListController) obj;
		if (f == null) {
			if (other.f != null)
				return false;
		} else if (!f.equals(other.f))
			return false;
		return true;
	}
	@Override
	public int compareTo(ItemListController comp) {
		if(this.equals(comp)){
			return 0;
		}else if(this.f.isTullFile() && comp.f.isTullFolder()){
			return 1;
		}else if(this.f.isTullFolder() && comp.f.isTullFile()){
			return -1;
		}else{
			return this.f.getName().toLowerCase().compareTo(comp.f.getName().toLowerCase());
		}
	}
}
