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

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class ChatClient {

    // The client socket
    private Socket clientSocket = null;
    // The output stream
    private PrintStream os = null;
    // The input stream
    private DataInputStream is = null;

    private final String host;
    private final int portNumber;
    private boolean closed = false;

    private Application app;

    public ChatClient (String host, int portNumber, Application app){     //Application is for an object in this class
        this.host = host;                                                 //and that object will close the application when it is called
        this.portNumber = portNumber;
        this.app = app;
    }

    public DataInputStream getInputStream() {
        return is;
    }

    public void close() {     //this object will close all the connection as well as the application when it is called
        try {
            os.close();
            is.close();
            clientSocket.close();
            Platform.exit();
        } catch (IOException e) {
            System.out.println("IOException : ");
            e.printStackTrace();
        }
    }

    public void sendMessage (String inputLine){     //this object is to send input message to server
        if(inputLine.trim().equals("")){            //check if the users enter nothing or just space in the line
            return;                                 //if it is true then the object will send nothing
        }
        if (clientSocket != null && os != null && is != null)       //checking if the connection is still online
            os.println(inputLine.trim());                           //sending message to server
    }

    public boolean connect(){    //perform connection from client to server
        try {
            this.clientSocket = new Socket(host, portNumber);
            this.os = new PrintStream(clientSocket.getOutputStream());
            this.is = new DataInputStream(clientSocket.getInputStream());
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + host);
        }
        return false;
    }
}