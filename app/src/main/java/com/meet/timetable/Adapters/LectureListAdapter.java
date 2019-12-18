package com.meet.timetable.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meet.timetable.Data.Lecture;
import com.meet.timetable.R;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.SimpleTimeZone;

public class LectureListAdapter extends RecyclerView.Adapter<LectureListAdapter.LectureViewHolder> {
    private ArrayList<Lecture> lectureArrayList;
    private Context context;

    public LectureListAdapter(ArrayList<Lecture> lectureArrayList, Context context) {
        this.lectureArrayList = lectureArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lecture_item, viewGroup, false);
        return new LectureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder lectureViewHolder, int i) {
        Lecture lecture = lectureArrayList.get(i);
        lectureViewHolder.durationTime.setText(lecture.getStartTime() + " - " + lecture.getEndTime());
        if (lecture.getCourseCode().isEmpty()) {
            lectureViewHolder.courseCode.setVisibility(View.GONE);
        } else {
            lectureViewHolder.courseCode.setText(lecture.getCourseCode());
        }

        if (lecture.getCourseTitle().isEmpty()) {
            lectureViewHolder.courseTitle.setVisibility(View.GONE);
        } else {
            lectureViewHolder.courseTitle.setText(lecture.getCourseTitle());
        }
        setLabList(lecture, lectureViewHolder);
        setLectureListColors(lectureViewHolder.container, lecture.getCourseCode());
    }

    private void setLabList(final Lecture lecture, LectureViewHolder lectureViewHolder) {
        if (!lecture.getLabsList().isEmpty()) {
            lectureViewHolder.labList.setVisibility(View.VISIBLE);
            LabListAdapter labListAdapter = new LabListAdapter(lecture.getLabsList());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, lecture.getLabsList().size() == 1 ? 1 : 2);
            lectureViewHolder.labList.setLayoutManager(gridLayoutManager);
            lectureViewHolder.labList.setAdapter(labListAdapter);
        } else {
            lectureViewHolder.labList.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lectureArrayList.size();
    }

    class LectureViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        TextView durationTime;
        TextView courseCode;
        TextView courseTitle;
        RecyclerView labList;

        LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.lecture_details_container);
            durationTime = itemView.findViewById(R.id.time_duration);
            courseCode = itemView.findViewById(R.id.course_code);
            courseTitle = itemView.findViewById(R.id.course_name);
            labList = itemView.findViewById(R.id.lab_list);
        }
    }

    public static void setLectureListColors(RelativeLayout relativeLayout, String s) {
        GradientDrawable gradientDrawable = (GradientDrawable) relativeLayout.getBackground();

        switch (s) {
            case "2CP01":
                gradientDrawable.setColor(Color.parseColor("#f9a43e"));
                break;
            case "2CP02":
                gradientDrawable.setColor(Color.parseColor("#67bf74"));
                break;
            case "2CP03":
                gradientDrawable.setColor(Color.parseColor("#2093cd"));
                break;
            case "2CP04":
                gradientDrawable.setColor(Color.parseColor("#ad62a7"));
                break;
            case "2HS01":
                gradientDrawable.setColor(Color.parseColor("#00BCD4"));
                break;
            case "2HS02":
                gradientDrawable.setColor(Color.parseColor("#00BFA5"));
                break;
            case "Lab":
                gradientDrawable.setColor(Color.parseColor("#A59FF7"));
                break;
            case "Tutorial":
                gradientDrawable.setColor(Color.parseColor("#FF8A80"));
                break;
            case "Lab/Tutorial":
                gradientDrawable.setColor(Color.parseColor("#F19374"));
                break;
            case "Break":
                gradientDrawable.setColor(Color.parseColor("#CDDC39"));
                break;
            default:
                gradientDrawable.setColor(Color.parseColor("#8EAAB6"));
                break;
        }
    }
}
