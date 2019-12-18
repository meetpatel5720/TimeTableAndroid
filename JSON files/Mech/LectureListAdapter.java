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
import java.util.ArrayList;
import java.util.Arrays;

public class LectureListAdapter extends RecyclerView.Adapter<LectureListAdapter.LectureViewHolder> {
    ArrayList<Lecture> lectureArrayList;
    Context context;

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
        setLectureListColors(lectureViewHolder.container,lecture);
    }

    private void setLabList(Lecture lecture, LectureViewHolder lectureViewHolder) {
        if (!lecture.getLabsList().isEmpty()) {
            lectureViewHolder.labList.setVisibility(View.VISIBLE);
            LabListAdapter labListAdapter = new LabListAdapter(lecture.getLabsList());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
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

    public class LectureViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        TextView durationTime;
        TextView courseCode;
        TextView courseTitle;
        RecyclerView labList;

        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.lecture_details_container);
            durationTime = itemView.findViewById(R.id.time_duration);
            courseCode = itemView.findViewById(R.id.course_code);
            courseTitle = itemView.findViewById(R.id.course_name);
            labList = itemView.findViewById(R.id.lab_list);
        }
    }

    private void setLectureListColors(RelativeLayout relativeLayout, Lecture lecture) {
        GradientDrawable gradientDrawable = (GradientDrawable) relativeLayout.getBackground();
        switch (lecture.getCourseCode()) {
            case "ME301":
                gradientDrawable.setColor(Color.parseColor("#f9a43e"));
                break;
            case "ME302":
                gradientDrawable.setColor(Color.parseColor("#67bf74"));
                break;
            case "ME303":
                gradientDrawable.setColor(Color.parseColor("#2093cd"));
                break;
            case "ME304":
                gradientDrawable.setColor(Color.parseColor("#ad62a7"));
                break;
            case "ME305":
                gradientDrawable.setColor(Color.parseColor("#00BCD4"));
                break;
            case "ME306":
                gradientDrawable.setColor(Color.parseColor("#FF8A80"));
                break;
            case "Break":
                gradientDrawable.setColor(Color.parseColor("#CDDC39"));
                break;
            case "Lab":
                gradientDrawable.setColor(Color.parseColor("#A59FF7"));
                break;
            default:
                break;
        }
    }
}
