package kr.ac.gachon.www.GachonGroup;

public class Account {
    public String name;
    public String ID;
    public String email;
    public String major;
    public int StudentNumber;
    public String group;
    public String password;
    public String phone;
    public boolean is_manager;

    public  Account(String name, String ID, String email, String major, int StudentNumber, String group, String password, String phone, boolean is_manager) {
        this.name=name;
        this.ID=ID;
        this.email=email;
        this.major=major;
        this.StudentNumber=StudentNumber;
        this.group=group;
        this.password=password;
        this.phone=phone;
        this.is_manager=is_manager;
    }
}
