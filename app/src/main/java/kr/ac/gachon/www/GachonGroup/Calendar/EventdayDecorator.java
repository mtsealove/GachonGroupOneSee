package kr.ac.gachon.www.GachonGroup.Calendar;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

public class EventdayDecorator implements DayViewDecorator { //일정이 있는 날에 표시할 장식
    private final Calendar calendar= Calendar.getInstance();
    private final HashSet<CalendarDay> days;
    public EventdayDecorator(Collection<CalendarDay> days) {
        this.days=new HashSet<>(days);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        return days.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, Color.RED));
    } //빨간 점 표시
}
