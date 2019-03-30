package kr.ac.gachon.www.GachonGroup.Entity;

public class Accuse {
    String BoardID;
    String BoardName;
    String Reason;
    String ReplyID;
    String GroupName;
    int AccuseID;

    public Accuse(String BoardID, String BoardName, String Reason, String ReplyID, int AccuseID, String GroupName) {
        this.BoardID=BoardID;
        this.BoardName=BoardName;
        this.Reason=Reason;
        this.ReplyID=ReplyID;
        this.AccuseID=AccuseID;
        this.GroupName=GroupName;
    }

    public String getBoardID() {
        return BoardID;
    }

    public void setBoardID(String boardID) {
        BoardID = boardID;
    }

    public String getBoardName() {
        return BoardName;
    }

    public void setBoardName(String boardName) {
        BoardName = boardName;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getReplyID() {
        return ReplyID;
    }

    public void setReplyID(String replyID) {
        ReplyID = replyID;
    }

    public int getAccuseID() {
        return AccuseID;
    }

    public void setAccuseID(int accuseID) {
        AccuseID = accuseID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }
}
