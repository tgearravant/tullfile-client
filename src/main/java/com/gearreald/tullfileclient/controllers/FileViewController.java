package com.gearreald.tullfileclient.controllers;

import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.utils.ImageUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class FileViewController {
	
	private TullFile f;
	@FXML private Text titleText;
	@FXML private ImageView iconImage;
	@FXML private Text pieceLabel;
	@FXML private Button downloadButton;
	
	public void setTullFile(TullFile f){
		this.f=f;
		this.titleText.setText(this.f.getName());
		this.iconImage.setImage(ImageUtils.getImage("file-icon.png"));
		this.pieceLabel.setText(Integer.toString(this.f.getPieces()));
	}
	public void initialize(){
	}
	public void downloadFile(ActionEvent e){
		this.f.downloadFile();
	}
}
