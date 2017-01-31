package com.allnew.facebookgraphapi;


import java.util.ArrayList;

public class Model {
    private String descreption;
    private String imgUrl;
    private String videoUrl;
    private String duration;
    private String totalLikes;
    private ArrayList<CommentModel> commentModelArrayList;

    public Model(String descreption, String imgUrl, String videoUrl, String duration,String totalLikes, ArrayList<CommentModel> commentModelArrayList) {
        this.descreption = descreption;
        this.totalLikes = totalLikes;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.commentModelArrayList = commentModelArrayList;
    }

    public String getDescreption() {
        return descreption;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getDuration() {
        return duration;
    }

    public ArrayList<CommentModel> getCommentModelArrayList() {
        return commentModelArrayList;
    }
}
