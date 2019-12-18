package com.meet.timetable.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meet.timetable.R;


import java.util.ArrayList;
import java.util.List;

public class LabListAdapter extends RecyclerView.Adapter<LabListAdapter.ViewHolder> {
    private List<String> labList = new ArrayList<>();

    public LabListAdapter(List<String> labList) {
        this.labList = labList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lab_list_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String labName = labList.get(i);
        viewHolder.textView.setText(labName);
    }

    @Override
    public int getItemCount() {
        return labList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.lab_name_text);
        }
    }
}
