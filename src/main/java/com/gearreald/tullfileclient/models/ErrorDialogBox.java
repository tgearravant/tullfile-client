package com.gearreald.tullfileclient.models;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.gearreald.tullfileclient.utils.ResourceLoader;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ErrorDialogBox extends Alert {

	private static String STYLESHEET_PATH="css/dialog.css";
	
	public ErrorDialogBox(Exception e) {
		super(AlertType.ERROR);
		this.setTitle("An Error was encountered.");
		this.setContentText(e.getMessage());
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		String stackTrace = stringWriter.toString();
		
		TextArea textArea = new TextArea(stackTrace);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		Label label = new Label("Stacktrace:");
		
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		this.getDialogPane().setExpandableContent(expContent);
		this.getDialogPane().getStylesheets().add(ResourceLoader.getAbsoluteResourcePath(STYLESHEET_PATH));
	}
	public static void dialogFor(Exception e){
		Platform.runLater(() ->{
			new ErrorDialogBox(e).show();
		});
	}
}
