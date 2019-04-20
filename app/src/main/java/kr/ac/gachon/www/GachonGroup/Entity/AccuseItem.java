package kr.ac.gachon.www.GachonGroup.Entity;

public class AccuseItem {

    String Reason;
    String userID;
    boolean Read;

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }
}
