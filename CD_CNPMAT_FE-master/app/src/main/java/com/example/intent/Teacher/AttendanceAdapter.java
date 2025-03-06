package com.example.intent.Teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Schedule;
import com.example.intent.R;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private List<Schedule> scheduleList;

    public AttendanceAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        if (schedule != null) {
            holder.tvActivity.setText(schedule.getActivity());
            holder.tvDate.setText(schedule.getScheduleDate());
            holder.tvStatus.setText((schedule.getStatus() != null ? schedule.getStatus() : "Chưa cập nhật"));
        }
    }

    public void updateData(List<Schedule> filteredList) {
        this.scheduleList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivity, tvDate, tvStatus;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvActivity = itemView.findViewById(R.id.tvActivity);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
