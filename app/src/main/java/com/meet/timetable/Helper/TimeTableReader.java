package com.meet.timetable.Helper;

import android.util.JsonReader;
import android.widget.ProgressBar;

import com.meet.timetable.Data.DayData;
import com.meet.timetable.Data.Lecture;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class TimeTableReader {
    public List<DayData> parseTimetable(InputStream inputStream) throws IOException {
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        return readTimetable(jsonReader);
    }

    private List<DayData> readTimetable(JsonReader jsonReader) throws IOException {
        List<DayData> timeTable = new ArrayList<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            if ("days".equals(key)) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    DayData dayData = readDaySchedule(jsonReader);
                    timeTable.add(dayData);
                }
                jsonReader.endArray();
            }
        }
        jsonReader.endObject();
        return timeTable;
    }

    private DayData readDaySchedule(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String dayName = null;
        List<Lecture> lectureList = new ArrayList<>();
        while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            switch (key) {
                case "day_name":
                    dayName = jsonReader.nextString();
                    break;
                case "lectures":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()){
                        Lecture lecture = readLecture(jsonReader);
                        lectureList.add(lecture);
                    }
                    jsonReader.endArray();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        return new DayData(lectureList, dayName);
    }

    private Lecture readLecture(JsonReader jsonReader) throws IOException {
        String startTime = null;
        String endTime = null;
        String courseCode = null;
        String courseTitle = null;
        List<String> labList = new ArrayList<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            final String key = jsonReader.nextName();
            switch (key) {
                case "start_time":
                    startTime = jsonReader.nextString();
                    break;
                case "end_time":
                    endTime = jsonReader.nextString();
                    break;
                case "course_code":
                    courseCode = jsonReader.nextString();
                    break;
                case "course_name":
                    courseTitle = jsonReader.nextString();
                    break;
                case "lab":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        String labName = jsonReader.nextString();
                        labList.add(labName);
                    }
                    jsonReader.endArray();
                    break;
                case "tutorial":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        String labName = jsonReader.nextString();
                        labList.add(labName);
                    }
                    jsonReader.endArray();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        return new Lecture(startTime,endTime,courseCode,courseTitle,labList);
    }
}
