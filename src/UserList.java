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

import java.util.ArrayList;

public class UserList {
    private ArrayList<String> userList;
    public UserList (){
        userList = new ArrayList<>();
    }

    public void addUser (String name){
        userList.add(name);
    }   //adding user names to an Array list

    public void removeUser (String name){
        userList.remove(name);
    }   //remove user from the Array list

    public String getUserList (){  //geting list of user
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/***@UsersList: ");
        for (int i = 0; i < userList.size(); i++){      //appending user name to list of user
            stringBuilder.append("@"+userList.get(i)+" ");
        }
        return stringBuilder.toString();
    }
}