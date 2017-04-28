package com.gearreald.tullfileclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TullFileClient extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("TullFile Downloader v0.1");
/*		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Text sceneHeader = new Text("TullFile Server Data");
		sceneHeader.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(sceneHeader, 0, 0, 2, 1);
		
		Label serverAddress = new Label("Server Address:");
		grid.add(serverAddress, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label api = new Label("API Key:");
		grid.add(api, 0, 2);

		PasswordField apiBox = new PasswordField();
		grid.add(apiBox, 1, 2);
		

		Button btn = new Button("Connect");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);
		

		final Text errorText = new Text();
		errorText.setTextAlignment(TextAlignment.CENTER);
        grid.add(errorText, 1, 6);
		
        btn.setOnAction(new EventHandler<ActionEvent>(){

			public void handle(ActionEvent event) {
				errorText.setFill(Color.FIREBRICK);
				
				errorText.setText("Could not connect.");
			}
        	
        });
        
		Scene scene = new Scene(grid, 300, 275);*/
		System.out.println(getClass().getClassLoader().getResource("fxml/login.fxml"));
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/login.fxml"));
       
        Scene scene = new Scene(root, 300, 275);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("css/global.css").toExternalForm());
		primaryStage.setScene(scene);
		
        primaryStage.show();
		
		// TODO Auto-generated method stub
		
	}

}
