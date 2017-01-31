package com.allnew.facebookgraphapi;


public class CommentModel {
    private String userName;
    private String descreption;

    public CommentModel(String userName, String descreption) {
        this.userName = userName;
        this.descreption = descreption;
    }

    public String getUserName() {
        return userName;
    }

    public String getDescreption() {
        return descreption;
    }
}
