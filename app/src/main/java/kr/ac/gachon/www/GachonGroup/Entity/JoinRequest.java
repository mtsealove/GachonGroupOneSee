package kr.ac.gachon.www.GachonGroup.Entity;

public class JoinRequest {
    public String name;
    public String contact;
    public int StudentNumber;
    public String major;
    public int age;
    public String SelfIntroduce;
    public String ID;
    public String group;
    public String AbleTime;
    public int requestID;


    public JoinRequest(String name, String contact, int StudentNumber, String major, int age, String SelfIntroduce, String ID, String group, String AbleTime) {
        this.name=name;
        this.contact=contact;
        this.StudentNumber=StudentNumber;
        this.major=major;
        this.age=age;
        this.SelfIntroduce=SelfIntroduce;
        this.ID=ID;
        this.group=group;
        this.AbleTime=AbleTime;
    }
}
