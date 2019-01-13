package kr.ac.gachon.www.GachonGroup;

public class Account {
    public String name;
    public String ID;
    public String email;
    public String major;
    public int StudentNumber;
    public String group;
    public String password;

    Account(String name, String ID, String email, String major, int StudentNumber, String group, String password) {
        this.name=name;
        this.ID=ID;
        this.email=email;
        this.major=major;
        this.StudentNumber=StudentNumber;
        this.group=group;
        this.password=password;
    }
}
