package kr.ac.gachon.www.GachonGroup.Entity;

public class Requirement {
    String content, email, title, userID;
    int id;

    public Requirement(String content, String email, String title, String userID, int id) {
        this.content=content;
        this.email=email;
        this. userID=userID;
        this.title=title;
        this.id=id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
