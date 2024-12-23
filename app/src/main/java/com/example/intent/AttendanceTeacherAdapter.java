package com.example.intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Schedule;

import java.util.List;

public class AttendanceTeacherAdapter extends RecyclerView.Adapter<AttendanceTeacherAdapter.AttendanceViewHolder> {

    private List<Schedule> scheduleList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Schedule schedule);
    }

    public AttendanceTeacherAdapter(List<Schedule> scheduleList, OnItemClickListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        if (schedule != null) {
            holder.bind(schedule);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void updateData(List<Schedule> newSchedules) {
        this.scheduleList = newSchedules;
        notifyDataSetChanged();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvActivity, tvDate, tvStatus;
        private Schedule currentSchedule;

        public AttendanceViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvActivity = itemView.findViewById(R.id.tvActivity);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentSchedule != null) {
                    listener.onItemClick(currentSchedule);
                }
            });
        }

        public void bind(Schedule schedule) {
            currentSchedule = schedule;
            tvActivity.setText(schedule.getActivity());
            tvDate.setText(schedule.getScheduleDate());
            tvStatus.setText(schedule.getStatus());
        }
    }
}
