package sungshin.hashtagcafe.firestore;

public class ReviewInfo {
    private String cafeName;
    private String hashtags;
    private int rating;
    private String reviewTxt;
    private String userid;


    public String getCafeName(){ return cafeName; }
    public String getHashtags(){
        return hashtags;
    }
    public int getRating(){
        return rating;
    }
    public String getReviewTxt(){
        return reviewTxt;
    }
    public String getUserid(){ return userid; }


}
