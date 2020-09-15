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

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Date;


/*
 * A chat server that delivers public and private messages.
 */
public class ChatServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;
    private static ChatHistory history = new ChatHistory();
    private static UserList userList = new UserList();

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 2228;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
                    + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]);
        }

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads, history, userList)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
/*Create thread which manages all incoming connections, input and output*/

class clientThread extends Thread {

    private UserList userList;
    private String clientName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;
    private ChatHistory history;

    public clientThread(Socket clientSocket, clientThread[] threads, ChatHistory history, UserList userList) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
        this.history = history;
        this.userList = userList;

    }

    @Override
    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            String name;
            while (true) {
                //get name
                name = is.readLine().trim();
                if (name.indexOf('@') == -1) {
                    break;
                } else {
                    os.println("The name should not contain '@' character.");
                }
            }

            /* Welcome the new the client. */
            os.println("Welcome " + name + " to our chat room.");
            userList.addUser(name);    //adding new user to user list
            os.println(userList.getUserList());  //sending user list to new users
            os.println(history.readHistory());   //sending chat history of the chat to the new users
            synchronized (this) {
                login(maxClientsCount, threads, name);
            }
            /* Start the conversation. */
            startChat(maxClientsCount, threads, name);
            os.println("*** EXIT_APPLICATION_NOW " + name + " ***");  //sending this text to close the application of the user quitting

            //disconnect the connection of the users who is quiting server on this thread
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }

    private void login(int maxClientsCount, clientThread[] threads, String name) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i] == this) {
                clientName = "@" + name;
                break;
            }
        }
        // notify all other that a new client joins in the server
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i] != this) {
                threads[i].os.println(new Date() + ": " + "*** A new user " + name + " entered the chat room !!! ***");
            }
        }
        //updating the userlist on the user screen
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i] != this) {
                threads[i].os.println(userList.getUserList());
            }
        }
    }

    private void startChat(int maxClientsCount, clientThread[] threads, String name) throws IOException {
        while (true) {
            String line = is.readLine();
            if (line.startsWith("/quit")){  //when users enter /quit, that means he is going to quit
                userList.removeUser(name);  //remove user from the user lít
                for (int i = 0; i < maxClientsCount; i++){
                    if (threads[i] != null && threads[i] != this){
                        threads[i].os.println(userList.getUserList());  //updating the user list on the user screen
                    }
                }
                break;    // break the loop and then the server send *** EXIT_APPLICATION_NOW " + name of user + " ***  to
            }             //user who is quitting

            if (line.startsWith("@")) {   //line start with @ along the user name next to it presents a private message
                privateChat(maxClientsCount, threads, name, line);
            }
            else {
                /* The message is public, broadcast it to all other clients. */
                synchronized (this) {
                    publicChat(maxClientsCount, threads, name, line);
                }
            }
        }
        //notify all users when an user is leaving
        synchronized (this) {
            notifyUserLeaves(maxClientsCount, threads, name);
        }
    }

    private void privateChat(int maxClientsCount, clientThread[] threads, String name, String line) {
        String[] words = line.split("\\s", 2);
        if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this                          //finding the recipient in the thread
                                && threads[i].clientName != null                              //if the server cannot find the recipient. The chat will not show anything.
                                && threads[i].clientName.equals(words[0])) {
                            threads[i].os.println("<" + new Date() + "> <" + name + "> <private message> to you: " + words[1]); //let the receiver know where the private message from
                            this.os.println("<private massage> to " + threads[i].clientName + ":" + words[1]);                  //let the user who send the message know that the message is sent
                            break;    // break out of the for loop when the target user is found
                        }
                    }
                }
            }
        }
    }

    private void publicChat(int maxClientsCount, clientThread[] threads, String name, String line) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i].clientName != null) {
                threads[i].os.println("<" + new Date() + ">"  + "<" + name + ">" + ": " + line); //sending message to every user in the thread
                history.addLine("<" + new Date() + ">"  + "<" + name + ">" + ": " + line);  //adding to chat history
            }                                                                                   //because this is an open chat application so only public message is saved
        }
    }

    private void notifyUserLeaves(int maxClientsCount, clientThread[] threads, String name) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && threads[i] != this
                    && threads[i].clientName != null) {
                threads[i].os.println(new Date() + ": " + "*** The user " + name + " is leaving the chat room !!! ***");
            }
        }
    }
}