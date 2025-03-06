package com.example.intent;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Notification;
import com.example.intent.Model.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private NotificationAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notificationList, OnItemClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        if (notification != null) {
            holder.bind(notification);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateData(List<Notification> newNotifications) {
        this.notificationList = newNotifications;
        notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle, notificationContent, notificationTime;
        Notification currentNotification;

        public NotificationViewHolder(@NonNull View itemView, NotificationAdapter.OnItemClickListener listener) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationContent = itemView.findViewById(R.id.notificationContent);
            notificationTime = itemView.findViewById(R.id.notificationTime);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentNotification != null) {
                    listener.onItemClick(currentNotification);
                }
            });
        }
        public void bind(Notification notification) {
            currentNotification = notification;
            notificationTitle.setText(notification.getTitle());
            notificationContent.setText(notification.getContent());

            String createdAt = notification.getCreatedAt();
            String timeAgo = getTimeAgo(createdAt);
            notificationTime.setText(timeAgo);
        }
        private String getTimeAgo(String createdAt) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(createdAt);
                long timestamp = date.getTime();
                long now = System.currentTimeMillis();
                long diff = now - timestamp;

                if (diff < DateUtils.MINUTE_IN_MILLIS) {
                    return "Vừa xong";
                } else if (diff < DateUtils.HOUR_IN_MILLIS) {
                    return (diff / DateUtils.MINUTE_IN_MILLIS) + " phút trước";
                } else if (diff < DateUtils.DAY_IN_MILLIS) {
                    return (diff / DateUtils.HOUR_IN_MILLIS) + " giờ trước";
                } else {
                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    return sdfDate.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return "Ngày không hợp lệ";
            }
        }
    }
}
