package kr.ac.gachon.www.GachonGroup.Entity;


public class RequestListViewItem { //가입 리스트로 표시될 객체
    private String name;
    private String major;
    private boolean IsChecked=false;

    public void setName(String name){
        this.name=name;
    }

    public void setMajor(String major) {
        this.major=major;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public boolean isChecked() {
        return IsChecked;
    }

    public void setChecked(boolean checked) {
        IsChecked = checked;
    }
}
