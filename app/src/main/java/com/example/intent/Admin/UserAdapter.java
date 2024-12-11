package com.example.intent.Admin;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Student;
import com.example.intent.Model.User;
import com.example.intent.R;
import com.example.intent.StudentAdapter;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public UserAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user != null) {
            holder.bind(user);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newUsers) {
        this.userList = newUsers;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserId, txtUserRole;
        User currentUser;

        public UserViewHolder(@NonNull View itemView, UserAdapter.OnItemClickListener listener) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserId = itemView.findViewById(R.id.txtUserId);
            txtUserRole = itemView.findViewById(R.id.txtUserRole);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentUser != null) {
                    listener.onItemClick(currentUser);
                }
            });
        }

        public void bind(User user) {
            currentUser = user;
            txtUserName.setText(user.getName());
            txtUserId.setText(String.valueOf(user.getUserId()));
            txtUserRole.setText(String.valueOf(user.getRole()));
        }
    }
}
