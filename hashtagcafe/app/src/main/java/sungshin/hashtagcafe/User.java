// jenny

package sungshin.hashtagcafe;

// store the whole data from the firestore
public class User {

    // retrieve firestore data
    String cafeName, cafeIntroduce, checkList;

    // empty constructer for firestore
    public User(){}

    // constructer
    public User(String cafeName, String cafeIntroduce, String checkList) {
        this.cafeName = cafeName;
        this.cafeIntroduce = cafeIntroduce;
        this.checkList = checkList;
    }

    // getter and setter
    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public String getCafeIntroduce() {
        return cafeIntroduce;
    }

    public void setCafeIntroduce(String cafeIntroduce) {
        this.cafeIntroduce = cafeIntroduce;
    }

    public String getCheckList() { return checkList; }

    public void setCheckList(String checkList) { this.checkList = checkList; }
}
