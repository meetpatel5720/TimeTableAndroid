package com.meet.timetable;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meet.timetable.Adapters.DayScheduleListAdapter;
import com.meet.timetable.Adapters.LabListAdapter;
import com.meet.timetable.Adapters.LectureListAdapter;
import com.meet.timetable.Data.DayData;
import com.meet.timetable.Data.Lecture;
import com.meet.timetable.Helper.MyJobService;
import com.meet.timetable.Helper.TimeTableReader;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TimeTableActivity extends AppCompatActivity {
    private static final String TAG = "TimeTableActivity";
    private static final int JOB_ID = 57;

    RecyclerView dayListRecyclerView;
    DayScheduleListAdapter dayScheduleListAdapter;
    ArrayList<DayData> dayScheduleArrayList;

    RelativeLayout currentLectureContainer;
    TextView currentCourseCode;
    TextView currentCourseName;
    RecyclerView currentLabList;

    @Override
    protected void onResume() {
        super.onResume();
        getOngoingLecture();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        getSupportActionBar().setTitle("TimeTable");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        currentLectureContainer = findViewById(R.id.current_lecture_details_container);
        currentCourseCode = findViewById(R.id.current_course_code);
        currentCourseName = findViewById(R.id.current_course_name);
        currentLabList = findViewById(R.id.current_lab_list);

        readTimeTable();

        dayListRecyclerView = findViewById(R.id.days_list);
        dayScheduleListAdapter = new DayScheduleListAdapter(dayScheduleArrayList, TimeTableActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dayListRecyclerView.setLayoutManager(linearLayoutManager);
        dayListRecyclerView.setAdapter(dayScheduleListAdapter);

        getOngoingLecture();
        if (!isJobIdRunning(JOB_ID)) {
            scheduleJob();
        }
    }

    public void readTimeTable() {
        TimeTableReader timeTableReader = new TimeTableReader();
        InputStream inputStream = null;
        try {
            String fileName = "schedule_div_3.json";
            inputStream = TimeTableActivity.this.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dayScheduleArrayList = new ArrayList<DayData>(timeTableReader.parseTimetable(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOngoingLecture() {
        DayData curDayData = null;
        for (DayData dayData : dayScheduleArrayList) {
            if (dayData.getDayName().equals(getCurrentDay())) {
                curDayData = dayData;
                break;
            }
        }

        if (curDayData != null) {
            for (Lecture lecture : curDayData.getLectureList()) {
                if (compareTime(lecture.getStartTime(), lecture.getEndTime(), getCurrentTime())) {
                    setCurrentLectureDetails(lecture.getCourseCode(), lecture.getCourseTitle(), lecture.getLabsList());
                    Log.e("Time comparision ", String.valueOf(compareTime(lecture.getStartTime(), lecture.getEndTime(), getCurrentTime())));
                    System.out.println("Current lecture is: " + lecture.getCourseCode());
                    break;
                } else {
                    currentLectureContainer.setVisibility(View.GONE);
                }
            }
        }
        if (curDayData == null) {
            currentLectureContainer.setVisibility(View.GONE);
        }
    }

    void setCurrentLectureDetails(String courseCode, String courseName, List<String> labList) {
        currentLectureContainer.setVisibility(View.VISIBLE);
        if (courseCode.isEmpty()) {
            currentCourseCode.setVisibility(View.GONE);
        } else {
            currentCourseCode.setVisibility(View.VISIBLE);
            currentCourseCode.setText(courseCode);
        }

        if (courseName.isEmpty()) {
            currentCourseName.setVisibility(View.GONE);
        } else {
            currentCourseName.setVisibility(View.VISIBLE);
            currentCourseName.setText(courseName);
        }
        if (!labList.isEmpty()) {
            currentLabList.setVisibility(View.VISIBLE);
            LabListAdapter labListAdapter = new LabListAdapter(labList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            currentLabList.setLayoutManager(linearLayoutManager);
            currentLabList.setAdapter(labListAdapter);
        } else {
            currentLabList.setVisibility(View.GONE);
        }

        LectureListAdapter.setLectureListColors(currentLectureContainer, courseCode);

    }

    public boolean compareTime(String startTime, String endTime, String currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");

        try {
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            Date current = sdf.parse(currentTime);
            return current.after(start) && current.before(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCurrentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        String time = new SimpleDateFormat("hh:mm aa").format(currentTime);
        Log.e("Current time is ", Calendar.getInstance().getTime().toString());
        return time;
    }

    public static String getCurrentDay() {
        String dayName = null;
        Calendar calendar = Calendar.getInstance();
        dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        Log.e("Name of day is ", dayName);
        return dayName;
    }

    private void scheduleJob() {
        ComponentName componentName = new ComponentName(this, MyJobService.class);

        JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID,componentName).setPersisted(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobBuilder.setPeriodic(1000 * 15 * 60, 15 * 60 * 1000);
        } else {
            jobBuilder.setPeriodic(1000 * 15 * 60);
        }

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobBuilder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.e(TAG, "Job scheduled");
        } else {
            Log.e(TAG, "Job scheduling failed");
        }
    }

    public boolean isJobIdRunning(int jobId) {
        final JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        List<JobInfo> jb= jobScheduler.getAllPendingJobs();
        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == jobId) {
                return true;
            }
        }
        Log.e(TAG, String.valueOf(jb.size()));
        return false;
    }
}
