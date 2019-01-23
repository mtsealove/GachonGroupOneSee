package kr.ac.gachon.www.GachonGroup.modules;

public class JoinRequest {
    String name;
    String contact;
    int StudentNumber;
    String major;
    int age;
    String SelfIntroduce;
    String ID;


    public JoinRequest(String name, String contact, int StudentNumber, String major, int age, String SelfIntroduce, String ID) {
        this.name=name;
        this.contact=contact;
        this.StudentNumber=StudentNumber;
        this.major=major;
        this.age=age;
        this.SelfIntroduce=SelfIntroduce;
        this.ID=ID;
    }
}
