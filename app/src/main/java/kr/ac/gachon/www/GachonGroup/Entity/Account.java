package kr.ac.gachon.www.GachonGroup.Entity;

public class Account { //계정 객체
    public String name; //이름
    public String ID; //ID
    public String email; //이메일
    public String major; //전공
    public int StudentNumber; //학번
    public String group; //동아리
    public String password; //비밀번호
    public String phone; //전화번호
    public boolean is_manager; //관리자 여부

    //모든 정보를 알 때 사용하는 생성자
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
    public Account() { //정보를 얻기 위해 생성자 없이 사용
        //모든 정보를 null 이나 0으로 설정하며 복사하여 사용
        name=null;
        ID=null;
        email=null;
        major=null;
        StudentNumber=0;
        group=null;
        password=null;
        phone=null;
        is_manager=false;
    }

    public void CopyAccount(Account account) { //계정 복사
        this.name=account.name;
        this.ID=account.ID;
        this.email=account.email;
        this.major=account.major;
        this.StudentNumber=account.StudentNumber;
        this.group=account.group;
        this.password=account.password;
        this.phone=account.phone;
        this.is_manager=account.is_manager;
    }
}
