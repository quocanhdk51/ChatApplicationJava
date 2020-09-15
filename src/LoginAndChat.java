/*
RMIT University Vietnam
  Course: INTE2512 Object-Oriented Programming
  Semester: 2018A
  Assessment: Assignment 3
  Authors:  Nguyen Tuan Anh
            Luu Huynh Triet
            Bui Quoc Anh
            Nguyen Hoang Long
  ID:   s3577537
        s3594528
        s3634132
        s3727634
  Created date: 30/05/2018
  Acknowledgment:Below are the sources for the information we used to complete the chat application
                    http://www.java2s.com/Code/Java/JavaFX/SetScenebackgroundcolorandsize.htm
                    https://stackoverflow.com/questions/28243156/autoscroll-javafx-textflow
                    https://stackoverflow.com/questions/20230503/resize-textarea-horizontally-and-vertically
                    http://www.java2s.com/Code/Java/JavaFX/fxbordercolorwhite.htm
                    http://www.java2s.com/Tutorials/Java/JavaFX/0350__JavaFX_ScrollPane.htm
                    https://stackoverflow.com/questions/9738146/javafx-how-to-set-scene-background-image
                    Emoji source:
                    https://emojiisland.com/pages/free-download-emoji-icons-png
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;


public class LoginAndChat extends Application{
    private static ChatClient client;
    private TextField text1;
    private TextField text2;
    private Button button;
    private TextFlow chat;
    private Button files;
    private TextField text;
    private TextFlow guide;
    private TextArea usersListView;
    private Button exit;
    private Text text3;
    private ScrollPane scrollPane;
    private ScrollPane scrollPane1;
    String s="";

    public static void main(String[] args) {
        Application.launch(args);
        client.sendMessage("/quit");
    }

    public void start (Stage primaryStage) throws Exception {

        //Login GUI
        BackgroundImage image = new BackgroundImage(new Image("file:src/CC_1004.jpg",
                true),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);

        GridPane lpane = new GridPane();
        lpane.add(getBoxLeft(),1,1);
        lpane.add(getBoxRight(),2,1);
        lpane.add(getButton(),2,2);

        BorderPane pane = new BorderPane();
        pane.setPrefSize(450, 300);
        pane.setBackground(new Background(image));
        pane.setCenter(lpane);


        Group gpane = new Group(pane);

        Scene scene = new Scene(gpane);
        primaryStage.setTitle("Login Menu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //handle event of buttons in Login window
        // Enter key press
        text1.setOnKeyPressed(e ->{
            if (e.getCode() == KeyCode.ENTER) {
                buttonActionContent(primaryStage);
            }
        });
        // Enter key press
        text2.setOnKeyPressed(e ->{
            if (e.getCode() == KeyCode.ENTER) {
                buttonActionContent(primaryStage);
            }
        });
        button.setOnAction(event -> {
            buttonActionContent(primaryStage);
        });
    }

    //handle the confirmation of server IP and user name
    private void buttonActionContent(Stage primaryStage) {
        String IP = text1.getText();
        //Condition for IP if it blank the App will warning to input
        if( IP.isEmpty()) {
            Alert a1 = new Alert(Alert.AlertType.ERROR);
            a1.setTitle("Warning!!!");
            a1.setHeaderText(null);
            a1.setContentText("Please input IP");
            a1.showAndWait();
            return;
        }
        //Condition for username if it blank the App will warning to input
        String username= text2.getText();
        // check user name
        if(username.isEmpty()){
            Alert a2 = new Alert(Alert.AlertType.ERROR);
            a2.setTitle("Warning!!!");
            a2.setHeaderText(null);
            a2.setContentText("Please input User name");
            a2.showAndWait();
            return;
        }
        //access chat GUI
        // If IP and username is not blank , this app will connect with chat client
        if (IP != null) {
            client = new ChatClient(IP, 2228, this);
            if(client.connect()) {
                if (username != null) {
                    client.sendMessage(username);

                    //the GUI of the chat application
                    BackgroundImage image = new BackgroundImage(new Image("file:src/Stormy_Night.jpeg",
                            true),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);
                    final BorderPane pane1 = new BorderPane();
                    pane1.setPrefSize(1000, 800);
                    pane1.setCenter(getCenterPart());
                    pane1.setBackground(new Background(image));
                    pane1.setRight(getRightPart());
                    pane1.setBottom(getBottomPart());

                    startMessageReader();   //DEPENDS ON CHAT BOX, CALL AFTER getCenterPart()

                    final Group bPane = new Group(pane1);

                    final Scene scene1 = new Scene(bPane);
                    primaryStage.setTitle("Chat Application");
                    primaryStage.setScene(scene1);
                    primaryStage.setResizable(false);
                    primaryStage.setMinHeight(800);
                    primaryStage.setMinWidth(1000);
                    primaryStage.show();

                    // Enter key press for sending chat
                    text.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                            s = text.getText() + "\n";
                            text.setText("");
                            client.sendMessage(s);
                        }
                    });
                    //click on EXIT. The application will send a message "/quit" to server, which will trigger a quitting process
                    exit.setOnAction(event1 -> {
                        client.sendMessage("/quit");
                    });
                }
            }
            else {    //warning when the connection is failed
                Alert a3 = new Alert(Alert.AlertType.ERROR);
                a3.setTitle("Warning!!!");
                a3.setHeaderText(null);
                a3.setContentText("Connection Failed");
                a3.showAndWait();
                return;
            }
        }
    }

    // the label to inform the user about the information needed to login to the program
    public VBox getBoxLeft() {
        VBox vBoxLeft = new VBox();
        vBoxLeft.setSpacing(10);
        vBoxLeft.setPadding(new Insets(10));

        Label address = new Label("IP: ");
        address.setFont(Font.font("Arial", 20));
        address.setTextFill(Color.WHITESMOKE);
        Label name = new Label("User: ");
        name.setFont(Font.font("Arial", 20));
        name.setTextFill(Color.WHITESMOKE);

        vBoxLeft.getChildren().addAll(address,name);

        return vBoxLeft;
    }

    //the textfield to input IP and User name
    public VBox getBoxRight() {
        VBox vBoxRight = new VBox();
        vBoxRight.setSpacing(10);
        vBoxRight.setPadding(new Insets(10));

        this.text1 = new TextField();
        this.text2 = new TextField();

        vBoxRight.getChildren().addAll(text1, text2);
        return vBoxRight;
    }

    // the button used to login to the chat server
    public Button getButton() {
        this.button = new Button("Login");
        button.setPrefSize(100, 50);

        return button;
    }

    //the chat box, main part of the GUI
    public VBox getCenterPart() {
        VBox vboxLeft = new VBox();
        vboxLeft.setSpacing(10);
        vboxLeft.setPadding(new Insets(10));

        Label title = new Label("Server");
        title.setFont(Font.font("Arial",32));
        title.setTextFill(Color.WHITE);
        this.chat = new TextFlow();
        chat.setPadding(new Insets(10));
        chat.setLineSpacing(10);
        chat.setPrefHeight(800);
        this.scrollPane = new ScrollPane();
        chat.getChildren().addListener(
                (ListChangeListener<Node>) ((change) -> {
                    chat.layout();
                    scrollPane.layout();
                    scrollPane.setVvalue(1.0f);
                }));
        scrollPane.setContent(chat);

        vboxLeft.setAlignment(Pos.TOP_LEFT);
        vboxLeft.getChildren().addAll(title,chat,scrollPane);
        VBox.setVgrow(chat, Priority.ALWAYS);
        return vboxLeft;
    }

    public VBox getRightPart() {
        VBox vboxRight = new VBox();
        vboxRight.setPadding(new Insets(10));
        vboxRight.setSpacing(8);

        //the users list of the chat application
        Label title = new Label("Users");
        title.setFont(Font.font("Arial",32));
        title.setTextFill(Color.WHITE);
        this.usersListView = new TextArea();
        usersListView.setEditable(false);
        usersListView.setWrapText(true);
        usersListView.setPrefWidth(300);
        usersListView.setFont(Font.font("Arial", 20));

        //set up the guide note to introduce some function to the users
        Group group = new Group();
        this.guide = new TextFlow();
        guide.setPadding(new Insets(10));
        guide.setLineSpacing(10);
        guide.setPrefHeight(400);
        this.scrollPane1 = new ScrollPane();
        guide.getChildren().addListener(
                (ListChangeListener<Node>) ((change) -> {
                    guide.layout();
                    scrollPane1.layout();
                }));
        scrollPane1.setContent(guide);

        group.getChildren().add(guide);

        //the exit button
        ImageView iv1 = new ImageView("file:src/cry.png");
        iv1.setFitWidth(60);
        iv1.setPreserveRatio(true);
        iv1.setSmooth(true);
        this.exit = new Button("Exit",iv1);
        exit.setFont(Font.font("Arial", 70));
        exit.setPrefSize(300, 100);

        VBox.setVgrow(usersListView, Priority.ALWAYS);
        vboxRight.getChildren().addAll(title, usersListView, group,scrollPane1, exit);

        guildNoteDetail();

        return vboxRight;
    }

    //the bottom part of the chat application, consist of the area to input text and the send button
    public HBox getBottomPart() {
        HBox hboxBottomLeft = new HBox();
        hboxBottomLeft.setPadding(new Insets(10));
        hboxBottomLeft.setSpacing(10);

        this.text = new TextField();
        text.setPrefWidth(600);
        Group group = new Group();

        ImageView iv2 = new ImageView("file:src/clip.png");
        iv2.setFitWidth(20);
        iv2.setPreserveRatio(true);
        iv2.setSmooth(true);
        this.files = new Button("Files", iv2);
        files.setStyle("-fx-background-color: white;");

        group.getChildren().add(files);

        hboxBottomLeft.getChildren().addAll(text, group);
        return hboxBottomLeft;
    }

    public void startMessageReader(){       //start the thread to read message from all users from the server
        Thread t = new Thread(() -> readMessageLoop());
        t.start();
    }

    private void readMessageLoop() {
        String responseLine;
        try {
            while ((responseLine = client.getInputStream().readLine()) != null) {
                if(!responseLine.contains("/***@UsersList:")) {       //if the string contains /***@UsersList:, that means it is an
                    String finalResponseLine = responseLine;          //message to update userlist
                    Platform.runLater(() -> {
                        displayMessage(finalResponseLine);
                    });
                    if (finalResponseLine.contains("*** EXIT_APPLICATION_NOW")) {  //when the users received from the server,
                        client.close();                                            //it will trigger an event that force the application
                        break;                                                     //to shut down along with the connection
                    }

                }
                else{
                    if (usersListView != null) {        //clearing userlistView before update
                        usersListView.clear();
                    }
                    String[] word = responseLine.split("\\s");
                    for (String w:word){
                        if(!w.contains("/***@UsersList:")){
                            usersListView.appendText(w+"\n");
                        }
                    }
                }

            }

        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

    private void displayMessage(String finalResponseLine) { //this object is for analyzing the incoming message
        ArrayList<ImageView> imageViews = new ArrayList<>();
        if(chat.getChildren().size()==0){
            text3 = new Text(finalResponseLine);
        } else {
            // Add new line if not the first child
            text3 = new Text("\n"+ finalResponseLine);
        }
        if(finalResponseLine.contains(":)")) {  //check if users want to type a smiling face
            ImageView imageView1 = new ImageView("file:src/Emoji/Smile.png");
            // Remove :) from text
            text3.setText(text3.getText().replace(":)"," "));
            imageViews.add(imageView1);
        }
        if(finalResponseLine.contains(":(")) {   //check if users want to type a sad face
            ImageView imageView2 = new ImageView("file:src/Emoji/Sad.png");
            // Remove :( from text
            text3.setText(text3.getText().replace(":("," "));
            imageViews.add(imageView2);
        }
        if(finalResponseLine.contains(":|")) {   //check if users want to type a poker face
            ImageView imageView3 = new ImageView("file:src/Emoji/Poker face.png");
            // Remove :| from text
            text3.setText(text3.getText().replace(":|"," "));
            imageViews.add(imageView3);
        }
        if(finalResponseLine.contains(":O")) {   //check if users want to type a OMG face
            ImageView imageView4 = new ImageView("file:src/Emoji/OMG.png");
            // Remove :O from text
            text3.setText(text3.getText().replace(":O"," "));
            imageViews.add(imageView4);
        }
        if(finalResponseLine.contains(":'(")) {  //check if users want to type a crying face
            ImageView imageView5 = new ImageView("file:src/Emoji/Crying.png");
            // Remove :'( from text
            text3.setText(text3.getText().replace(":'("," "));
            imageViews.add(imageView5);
        }
        chat.getChildren().add(text3);   //add message on the chat
        chat.getChildren().addAll(imageViews);  //add emoji on the chat

        //this method to print emoji has 2 problem is that it cannot print multiple same emoji and user cannot decide
        //the position of the emoji on the chat. All input emojis are displayed at the end of the line
    }

    private void guildNoteDetail() {   //This object prints a guild note
        Text l1 = new Text("To leave enter /quit in a new line.\n");
        Text l2 = new Text("\t:Type :)\n");
        Text l3 = new Text("\t:Type :(\n");
        Text l4 = new Text("\t:Type :|\n");
        Text l5 = new Text("\t:Type :O\n");
        Text l6 = new Text("\t:Type :'(\n");
        ImageView imageView1 = new ImageView("file:src/Emoji/Smile.png");
        ImageView imageView2 = new ImageView("file:src/Emoji/Sad.png");
        ImageView imageView3 = new ImageView("file:src/Emoji/Poker face.png");
        ImageView imageView4 = new ImageView("file:src/Emoji/OMG.png");
        ImageView imageView5 = new ImageView("file:src/Emoji/Crying.png");
        guide.getChildren().addAll(l1, imageView1, l2, imageView2, l3, imageView3, l4, imageView4, l5, imageView5, l6);
    }
}