package com.gearreald.tullfileclient.models;

import com.gearreald.tullfileclient.utils.ResourceLoader;

import javafx.scene.control.Alert;

public class ConfirmationDialog extends Alert {

	private static String STYLESHEET_PATH="css/dialog.css";
	
	public ConfirmationDialog(String title, String confirmationText) {
		super(AlertType.CONFIRMATION);
		this.setTitle(title);
		this.setContentText(confirmationText);

		this.getDialogPane().getStylesheets().add(ResourceLoader.getAbsoluteResourcePath(STYLESHEET_PATH));
	}
}
