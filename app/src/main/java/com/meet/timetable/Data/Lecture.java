package com.meet.timetable.Data;

import java.util.Date;
import java.util.List;

public class Lecture {
    private String startTime;
    private String endTime;
    private String courseCode;
    private String courseTitle;
    private List<String> labsList;

    public Lecture(String startTime, String endTime, String courseCode, String courseTitle, List<String> labsList) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.labsList = labsList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public List<String> getLabsList() {
        return labsList;
    }

    public void setLabsList(List<String> labsList) {
        this.labsList = labsList;
    }
}
