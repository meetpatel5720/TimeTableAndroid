package com.meet.timetable.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.meet.timetable.Data.DayData;
import com.meet.timetable.Data.Lecture;
import com.meet.timetable.R;
import com.meet.timetable.TimeTableActivity;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.meet.timetable.TimeTableActivity.getCurrentDay;
import static com.meet.timetable.TimeTableActivity.getCurrentTime;

public class MyJobService extends JobService {
    public static final int NOTIFICATION_ID = 100;
    private static final String TAG = "MyJobService";
    private boolean isJobCancelled = false; //used to check is job is cancelled before completion of not. If job is cancelled then only new job is created.

    private ArrayList<DayData> dayScheduleArrayList;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG, "Job started");

        if (!isJobCancelled) {
            showNextLectureNotification(params);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "Job cancelled before completion");
        isJobCancelled = true;
        return false;
    }

    private void showNextLectureNotification(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                readTimeTableFromFile();
                DayData curDayData = null;
                for (DayData dayData : dayScheduleArrayList) {
                    if (dayData.getDayName().equals(getCurrentDay())) {
                        curDayData = dayData;
                        break;
                    }
                }

                if (curDayData != null) {
                    int counter = 0;
                    Lecture nextLecture = null;
                    for (Lecture lecture : curDayData.getLectureList()) {
                        if (counter == 0) {
                            if (timeDifference(lecture.getStartTime(), getCurrentTime())) {
                                showNotification(lecture.getCourseCode());
                                break;
                            }
                        }
                        counter++;
                        if (timeDifference(lecture.getEndTime(), getCurrentTime())) {
                            if (counter < curDayData.getLectureList().size()) {
                                nextLecture = curDayData.getLectureList().get(counter);
                            }
                            if (nextLecture != null) {
                                showNotification(nextLecture.getCourseCode());
                                break;
                            }
                            if(counter == curDayData.getLectureList().size()){
                                showNotification("College Over");
                                break;
                            }
                        }
                        Log.e(TAG, "Counter = " + String.valueOf(counter));
                    }
                }

                Log.e(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    //show notification in notification panel
    private void showNotification(String courseCode) {
        Intent intent = new Intent(getApplicationContext(), TimeTableActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        buildNotification(courseCode, intent);
        Log.e("From job schedule", "Current lecture is: " + courseCode);
    }

    //read time table from JSON file
    private void readTimeTableFromFile() {
        TimeTableReader timeTableReader = new TimeTableReader();
        InputStream inputStream = null;
        try {
            String fileName = "schedule_div_3.json";
            inputStream = getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dayScheduleArrayList = new ArrayList<DayData>(timeTableReader.parseTimetable(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this method builds notification to show in notification panel
    private void buildNotification(String courseCode, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext(), "channel1");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(getMessageForNotification(courseCode));

        Notification notification;
        notification = mBuilder.setTicker("Attention").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Attention")
                .setContentIntent(resultPendingIntent)
                .setStyle(inboxStyle)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_logo_timetable_fg)
                .setContentText(getMessageForNotification(courseCode))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("channel1", "Channel 1", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    //check if next lecture is staring in next 16 min or not
    public boolean timeDifference(String endTime, String currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");

        try {
            Date end = sdf.parse(endTime);
            Date current = sdf.parse(currentTime);
            long mills = end.getTime() - current.getTime();
            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) ((mills / (1000 * 60)) % 60);
            Log.e(TAG, "timeDiff: min=" + mins + " " + "hours=" + hours);
            if (hours == 0 && (mins >= 0 && mins <= 16))
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private String getMessageForNotification(String courseCode) {
        switch (courseCode) {
            case "Lab":
                return "Your Lab will start soon";
            case "Lab/Tutorial":
                return "Your Lab/Tutorial will start soon";
            case "Break":
                return "Get ready! It's time for relaxing";
            case "College Over":
                return "Wrap Up! It's time to leave";
            default:
                return "Your " + courseCode + " lecture will start soon";
        }
    }
}
