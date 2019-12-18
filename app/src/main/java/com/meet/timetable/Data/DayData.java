package com.meet.timetable.Data;

import java.util.List;

public class DayData {
    private List<Lecture> lectureList;
    private String dayName;

    public DayData(List<Lecture> lectureList, String dayName) {
        this.lectureList = lectureList;
        this.dayName = dayName;
    }

    public List<Lecture> getLectureList() {
        return lectureList;
    }

    public void setLectureList(List<Lecture> lectureList) {
        this.lectureList = lectureList;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }
}
