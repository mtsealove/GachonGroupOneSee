package kr.ac.gachon.www.GachonGroup.modules;

public class JoinRequest {
    public String name;
    public String contact;
    public int StudentNumber;
    public String major;
    public int age;
    public String SelfIntroduce;
    public String ID;
    public String group;


    public JoinRequest(String name, String contact, int StudentNumber, String major, int age, String SelfIntroduce, String ID, String group) {
        this.name=name;
        this.contact=contact;
        this.StudentNumber=StudentNumber;
        this.major=major;
        this.age=age;
        this.SelfIntroduce=SelfIntroduce;
        this.ID=ID;
        this.group=group;
    }
}
