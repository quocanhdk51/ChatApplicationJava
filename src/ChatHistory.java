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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChatHistory {
    private File history;
    private BufferedWriter writer;

    public ChatHistory(){
        try {
            history = File.createTempFile("Temp", "tmp");   //create a temporary file to store chat history
            history.deleteOnExit();                                      //delete the file when the server is closed
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer = new BufferedWriter(new FileWriter(history));
        } catch (IOException e) {
            System.out.println("Can't open history file for reading.");
        }
        System.out.println("History=\n"+readHistory());
    }

    public void addLine(String str) {               //add message into chat history
        try {
            writer.append(str);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Can't add line to history file.");
        }
    }

    public String readHistory() {                   //read from the temporary file
        try {
            return new String(Files.readAllBytes(history.toPath()));
        } catch (IOException e) {
            System.out.println("Couldn't read temp history file");
            return "";
        }
    }

    public void close() {        //this object is not really necessary. it exists only in case we want to stop writing into the file
        try {
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't close file writer.");
        }
    }
}
