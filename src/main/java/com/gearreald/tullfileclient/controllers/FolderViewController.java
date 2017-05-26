package com.gearreald.tullfileclient.controllers;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.utils.ImageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class FolderViewController {
	
	private TullFolder f;
	@FXML private Label titleLabel;
	@FXML private ImageView iconImage;
	@FXML private Button openButton;
	@FXML private Button deleteButton;
	
	public void setTullFolder(TullFolder f){
		this.f=f;
		this.titleLabel.setText(this.f.getName());
		this.iconImage.setImage(ImageUtils.getImage("folder-icon.png"));
	}
	@FXML public void changeInterfaceTullFolder(){
		InterfaceController controller = Environment.getInterfaceController();
		controller.setDisplayFolder(this.f, false);
	}
	@FXML public void deleteTullFolder(){
		if(this.f.delete())
			Environment.getInterfaceController().refreshCurrentFolder();
	}
}
