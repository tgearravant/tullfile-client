package com.gearreald.tullfileclient.controllers;

import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.utils.ImageUtils;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class FolderViewController {
	
	private TullFolder f;
	@FXML private Text titleText;
	@FXML private ImageView iconImage;
	
	public void setTullFolder(TullFolder f){
		this.f=f;
		this.titleText.setText(this.f.getName());
		this.iconImage.setImage(ImageUtils.getImage("folder-icon.png"));
	}
}
