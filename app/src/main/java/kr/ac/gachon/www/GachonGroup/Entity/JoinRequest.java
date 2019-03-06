package kr.ac.gachon.www.GachonGroup.Entity;

public class JoinRequest { //가입 신청 객체
    public String name; //사용자 이름
    public String contact; //연락처
    public int StudentNumber; //학번
    public String major; //전공
    public int age; //나이
    public String SelfIntroduce; //자기소개
    public String ID; //ID
    public String group; //동아리
    public String AbleTime; //가능한 시간
    public int requestID; //가입신청 ID


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
