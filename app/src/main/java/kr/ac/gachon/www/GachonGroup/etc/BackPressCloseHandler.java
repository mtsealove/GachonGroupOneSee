package kr.ac.gachon.www.GachonGroup.etc;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler { //'뒤로 가기 2번 눌러 종료' 기능 컨트롤러

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) { //이전 뒤로 가기 버튼을 누른지 2초가 지나지 않았을 경우
            backKeyPressedTime = System.currentTimeMillis();
            showGuide(); //메세지 출력
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) { //2초 동안 2번 뒤로 가기 버튼을 눌렀을 경울
            System.exit(0); //프로그램 종료
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
