package TypingChallenge;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

//Controller that handles user Interactions with the app
public class ViewController implements EventHandler<Event>, Initializable{

    //JavaFX View Elements
    @FXML
    private Label sampleTextLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label wordsTypedLabel;
    @FXML
    private Label wpmTitleLabel;
    @FXML
    private Label wordsPerMinuteLabel;
    @FXML
    private Label epTitleLabel;
    @FXML
    private Label errorPercentageLabel;
    @FXML
    private TextFlow textFlow;
    @FXML
    private TextArea textArea;
    @FXML
    private Button resetBtn;

    private final String[] sampleTexts = {
        "Hello World",
        "Basketball is my favorite sport. I like it when they dribble up and down the court.",
        "Just a small town girl, living in a lonely world. She took the midnight train going anywhere.",
        "I have a problem that I can not explain. I have no reason why it should have been so plain. " +
        "Have no questions but I sure have excuse. I lack the reason why I should be so confused.",
        "Everybody take it to the top. We're gonna stomp all night. In the neighborhood, don't it feel alright? " +
        "We're gonna stomp all night. Wanna party till the morning light."
    };

    //Variables required for execution of Typing Challenge
    private Timeline timerInterval;
    private String sampleText;
    private String typedText;
    private boolean timer;
    private boolean blankSpace;
    private int timeElapsed;
    private int cursorPosition;

    //Variable needed so that text doesn't get updated twice when a user deletes text
    private boolean alreadyChanged;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addTextAreaEventListners();
        setUpBuild();
    }

    @Override
    public void handle(Event event) {
        if(event.getSource() == resetBtn){
            //Reset button clicked
            setUpBuild();
        }
    }

    private void addTextAreaEventListners(){
        //Listens for changes in text
        textArea.textProperty().addListener((obs, oldValue, newValue)->{
            if(newValue.length()>0){
                if(newValue.length()<oldValue.length()) {
                    //Prevents this event from firing twice when a deletion occurs
                    if(alreadyChanged){
                        alreadyChanged=false;
                        return;
                    }
                    //Character Deleted scenario
                    updateText("DEL");
                }else {
                    updateText(Character.toString(newValue.charAt(cursorPosition)));
                }
            }
            else{
                //Challenge already started and user deleted all characters in textArea so reset values
                typedText = "";
                textArea.setText("");
                wordsTypedLabel.setText("0");
                blankSpace=true;
                cursorPosition = 0;
            }
        });

        //Determine proper cursorPosition when user clicks on arrow keys and set blankSpace to appropriate value
        //Event filter is friendlier with older versions of Java
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent ->  {
            int currentPosition = textArea.getCaretPosition();
            if(keyEvent.getCode() == KeyCode.LEFT){
                cursorPosition = currentPosition > 0 ? currentPosition-1 : 0;
                blankSpace = determineBlankSpace();
            }
            if(keyEvent.getCode() == KeyCode.RIGHT){
                cursorPosition = currentPosition < typedText.length() ? currentPosition + 1 : typedText.length();
                blankSpace = determineBlankSpace();
            }
        });

        //Determine proper cursorPosition when user clicks on different parts of the textArea
        textArea.caretPositionProperty().addListener((obs, oldPosition, newPosition)->{
            if(textArea.pressedProperty().get()){
                if(newPosition.intValue()==0){
                    cursorPosition = 0;
                    blankSpace = true;
                }
                else{
                    cursorPosition = newPosition.intValue();
                    blankSpace = determineBlankSpace();
                }
            }
        });
    }

    private void setUpBuild(){
        //Stop timer if running
        if(timerInterval != null){
            timerInterval.stop();
        }

        setSampleText();

        //Set challenge variables and view elements to default values
        typedText="";
        timer=false;
        blankSpace=true;
        cursorPosition=0;
        alreadyChanged=false;

        textArea.setText("");
        timeLabel.setText("0:00");
        wordsTypedLabel.setText("0");
        wordsPerMinuteLabel.setText("");
        errorPercentageLabel.setText("");

        wpmTitleLabel.setVisible(false);
        epTitleLabel.setVisible(false);
        textFlow.setVisible(false);
        textArea.setVisible(true);
        textArea.setEditable(true);
    }

    private void setSampleText(){
        final int randomNumber = (int)(Math.random() * sampleTexts.length);
        sampleText = sampleTexts[randomNumber];
        sampleTextLabel.setText(sampleText);
    }

    private void updateText(String c){
        if(!timer && !c.equals(" ")){
            startTimer();
            timer=true;
        }

        //Caret position adjustment if user deletes or inputs a character
        int shift = c.equals("DEL") ? -1 : 1;

        //Prevents user from entering two blank spaces in a row
        if(blankSpace && c.equals(" ")){
            alreadyChanged=true;
            textArea.setText(typedText);

            //Places textArea caret/cursor back to proper position
            textArea.selectRange(cursorPosition, cursorPosition);
            return;
        }

        //Maintains typedText is equal to input in textArea and displays correct # of words typed
        typedText = textArea.getText();
        wordsTypedLabel.setText(typedText.length()==0 ? "0" : (typedText.split(" ").length) + "");

        //Maintains proper cursorPosition. getCaretPosition returns differing values when adding vs deleting text
        cursorPosition = textArea.getCaretPosition()+shift;
        blankSpace = determineBlankSpace();

        checkFinished();
    }

    private void startTimer(){
        timeElapsed=0;
        timerInterval = new Timeline(new KeyFrame(
                Duration.millis(1000),
                actionEvent -> updateTime()));
        timerInterval.setCycleCount(Animation.INDEFINITE);
        timerInterval.play();
    }

    private void updateTime(){
        timeElapsed++;
        timeLabel.setText(convertTime(timeElapsed));
    }

    //Converts timeElapsed to string format mm:ss
    private String convertTime(int time){
        String timeString = "";
        timeString += (time/60) + ":";
        time -= 60*(time/60);
        if(time < 10){
            timeString += "0"+time;
        }
        else{
            timeString += time+"";
        }
        return timeString;
    }

    //Properly determines if the cursor is next to a blank space so users cannot enter multiple blank spaces in a row
    private Boolean determineBlankSpace(){
        if(cursorPosition == typedText.length() && typedText.length() > 0){
            return typedText.charAt(cursorPosition-1) == ' ';
        }
        else if(cursorPosition == 0){
            return true;
        }
        else{
            return typedText.charAt(cursorPosition) == ' ' || typedText.charAt(cursorPosition-1) == ' ';
        }
    }

    private void checkFinished(){
        if(typedText.length() == sampleText.length()){
            timerInterval.stop();

            //Show correct Word per Minute to Screen
            double wpm = ((typedText.split(" ").length) / ((double)timeElapsed/60));
            wordsPerMinuteLabel.setText(String.format("%.1f", wpm));
            wpmTitleLabel.setVisible(true);

            textArea.setEditable(false);
            calculateErrors();
        }
    }

    private void calculateErrors(){
        textArea.setVisible(false);
        textFlow.setVisible(true);
        textFlow.getChildren().clear();

        //Count how many errors by comparing sampleText with typedText character by character
        int errors = 0;
        for(int i=0; i < sampleText.length(); i++){
            Text text = new Text(Character.toString(typedText.charAt((i))));
            text.setFont(Font.font(24));
            if(typedText.charAt(i) != sampleText.charAt(i)){
                errors++;
                text.setFill(Color.RED);
                text.setFont(Font.font("", FontWeight.BOLD, 24));
            }
            textFlow.getChildren().add(text);
        }

        //Display error percentage
        epTitleLabel.setVisible(true);
        double errorPercentage = (((double)errors/sampleText.length())*100);
        errorPercentageLabel.setText(String.format("%.2f", errorPercentage)+"%");
    }

}
