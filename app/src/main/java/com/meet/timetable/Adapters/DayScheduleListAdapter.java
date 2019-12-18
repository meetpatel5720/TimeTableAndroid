package com.meet.timetable.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meet.timetable.Data.DayData;
import com.meet.timetable.Data.Lecture;
import com.meet.timetable.R;

import java.util.ArrayList;

public class DayScheduleListAdapter extends RecyclerView.Adapter<DayScheduleListAdapter.ViewHolder> {
    private ArrayList<DayData> dayDataArrayList;
    private Context context;

    public DayScheduleListAdapter(ArrayList<DayData> dayDataArrayList, Context context) {
        this.dayDataArrayList = dayDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.day_list_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DayData dayData = dayDataArrayList.get(i);
        viewHolder.dayName.setText(dayData.getDayName());

        LectureListAdapter lectureListAdapter = new LectureListAdapter(new ArrayList<>(dayData.getLectureList()),context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        viewHolder.lectureList.setLayoutManager(linearLayoutManager);
        viewHolder.lectureList.setAdapter(lectureListAdapter);
    }

    @Override
    public int getItemCount() {
        return dayDataArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayName;
        RecyclerView lectureList;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayName = itemView.findViewById(R.id.day_name_text);
            lectureList = itemView.findViewById(R.id.lectures_list);
        }
    }
}
