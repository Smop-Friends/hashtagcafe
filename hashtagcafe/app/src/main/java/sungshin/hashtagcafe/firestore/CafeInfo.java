package sungshin.hashtagcafe.firestore;


import com.google.firebase.firestore.GeoPoint;

public class CafeInfo {
    private String cafeName;
    private GeoPoint cafeGeopoint;
    private String cafeIntroduce;
    private String cafeLocation;
    private String time;
    private String cafeMenu;
    private String checkList;


    public String getCafeName(){ return cafeName; }
    public GeoPoint getCafeGeopoint(){
        return cafeGeopoint;
    }
    public String getCafeIntroduce(){
        return cafeIntroduce;
    }
    public String getCafeLocation(){
        return cafeLocation;
    }
    public String getTime(){ return time; }
    public String getCafeMenu(){
        return cafeMenu;
    }
    public String getCheckList(){ return checkList; }

}
