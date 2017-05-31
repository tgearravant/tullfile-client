package com.gearreald.tullfileclient.controllers;

import java.io.File;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.models.TullObject;
import com.gearreald.tullfileclient.utils.ImageUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.control.ProgressBar;

public class FileViewController {
	
	private TullObject f;
	@FXML private Label titleLabel;
	@FXML private ImageView iconImage;
	@FXML private Label pieceLabel;
	@FXML private Text pieceText;
	@FXML private Button deleteButton;
	@FXML private Button downloadButton;
	@FXML private Label sizeLabel;
	@FXML private Text sizeText;
	@FXML private ProgressBar downloadProgressBar;
	
	public void setTullObject(TullObject f){
		this.f=f;
		this.titleLabel.setText(this.f.getName());
		this.iconImage.setImage(ImageUtils.getImage("file-icon.png"));
		if(this.f.isTullFile()){
			TullFile tullFile = (TullFile) this.f;
			this.pieceText.setText(Integer.toString(tullFile.getPieceCount()));
			this.sizeText.setText(tullFile.getFileSizeAsString());
			this.downloadProgressBar.setProgress(0.5);
			this.downloadProgressBar.setVisible(false);
		}
	}
	public void initialize(){
	}
	@FXML
	public void downloadFile(ActionEvent e){
		TullFile tullFile = (TullFile) this.f;
		FileChooser chooser = new FileChooser();
		String suffix = tullFile.getSuffix();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(suffix.toUpperCase()+" files (*."+suffix+")", "*."+suffix+"");
        chooser.getExtensionFilters().add(extFilter);
		File file = chooser.showSaveDialog(Environment.getPrimaryStage());
		this.downloadProgressBar.setProgress(0);
		this.downloadProgressBar.setVisible(true);
		if(file!=null)
			tullFile.queueAllPiecesForDownload(file);
	}
	@FXML
	public void delete(ActionEvent e){
		if(this.f.delete())
			Environment.getInterfaceController().refreshCurrentFolder();
	}
	public void setProgress(double d) {
		this.downloadProgressBar.setProgress(d);
	}
}
