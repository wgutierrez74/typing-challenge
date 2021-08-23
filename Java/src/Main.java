package TypingChallenge;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Creates JavaFX Application
public class Main extends Application {

    //Launches JavaFX App
    public static void main(String[] args){
        launch( args);
    }

    //Function which will open Window to display App
    @Override
    public void start(Stage stage) throws Exception {
        //Load FXML Stylesheet
        Parent root = FXMLLoader.load(getClass().getResource("typingScene.fxml"));

        //Stage opens window which displays Scene styled by typingScene fxml file
        stage.setResizable(false);
        stage.setTitle("Typing Challenge");
        stage.setScene( new Scene(root, 1000, 600));
        stage.show();
    }

}
