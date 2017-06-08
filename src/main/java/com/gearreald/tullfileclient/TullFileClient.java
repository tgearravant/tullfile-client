package com.gearreald.tullfileclient;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gearreald.tullfileclient.utils.ResourceLoader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TullFileClient extends Application {

	public static void main(String[] args) throws MalformedURLException, IOException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("TullFile Downloader v0.1");
		Parent root = FXMLLoader.load(ResourceLoader.getResourceURL("fxml/login.fxml"));
       
        Scene scene = new Scene(root, 300, 275);
		scene.getStylesheets().add(ResourceLoader.getAbsoluteResourcePath("css/global.css"));
		primaryStage.setScene(scene);
		
        primaryStage.show();
        Environment.setPrimaryStage(primaryStage);
        Environment.initializeWorkers();
        Environment.startWorkers();
		
	}
	@Override
	public void stop(){
		Environment.stopWorkers();
	}
}
