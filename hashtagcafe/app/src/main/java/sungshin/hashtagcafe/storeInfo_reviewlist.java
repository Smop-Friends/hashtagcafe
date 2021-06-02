package sungshin.hashtagcafe;

import android.net.Uri;

public class storeInfo_reviewlist {
    private int cafeimage;
    private String hashtag;
    private int rating;
    private String review;
    String cafeName;
    public String userID;
    public int imgCount;

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCafeimage() {
        return cafeimage;
    }

    public void setCafeimage(int cafeimage) {
        this.cafeimage = cafeimage;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public storeInfo_reviewlist(int cafeimage, String hashtag, int rating, String review,String cafeName, String userID, int imgCount) {
        this.cafeimage = cafeimage;
        this.hashtag = hashtag;
        this.rating = rating;
        this.review = review;
        this.cafeName = cafeName;
        this.userID = userID;
        this.imgCount = imgCount;
    }
}
