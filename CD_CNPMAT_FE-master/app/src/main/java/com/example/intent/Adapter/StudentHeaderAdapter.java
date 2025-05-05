package com.example.intent.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Student;
import com.example.intent.R;

import java.util.List;

public class StudentHeaderAdapter extends RecyclerView.Adapter<StudentHeaderAdapter.StudentViewHolder> {
    private List<Student> studentList;
    private OnStudentClickListener listener;
    private int selectedPosition = -1; // Để theo dõi vị trí được chọn

    public interface OnStudentClickListener {
        void onStudentClick(Student student, int position);
    }

    public StudentHeaderAdapter(List<Student> studentList, OnStudentClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        if (student != null) {
            holder.bind(student, position);
        }
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    public void updateList(List<Student> newStudents) {
        this.studentList = newStudents;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;

        // Cập nhật giao diện cho item trước đó và item mới được chọn
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
        notifyItemChanged(selectedPosition);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudentName, txtStudentClass, txtStudentId;
        View itemView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            txtStudentName = itemView.findViewById(R.id.txtStudentName);
            txtStudentClass = itemView.findViewById(R.id.txtStudentClass);
            txtStudentId = itemView.findViewById(R.id.txtStudentId);
        }

        public void bind(Student student, int position) {
            txtStudentName.setText(student.getName());
            txtStudentClass.setText(student.getClass_name());
            txtStudentId.setText(String.valueOf(student.getStudentId()));

            // Hiệu ứng khi được chọn
            if (position == selectedPosition) {
                itemView.setBackgroundResource(R.drawable.selected_student_background);
            } else {
                itemView.setBackgroundResource(0); // Xóa background
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    setSelectedPosition(position);
                    listener.onStudentClick(student, position);
                }
            });
        }
    }
}