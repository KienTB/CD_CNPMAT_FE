package com.example.intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnItemClickListener listener;
    private List<Long> selectedStudentIds = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public StudentAdapter(List<Student> studentList, OnItemClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    public List<Long> getSelectedStudentIds() {
        return selectedStudentIds;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        if (student != null) {
            holder.bind(student);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(selectedStudentIds.contains(student.getStudentId()));

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedStudentIds.add(student.getStudentId());
                } else {
                    selectedStudentIds.remove(student.getStudentId());
                }
            });
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

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudentName, txtStudentClass, txtStudentId;
        CheckBox checkBox;
        Student currentStudent;

        public StudentViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            txtStudentName = itemView.findViewById(R.id.txtStudentName);
            txtStudentClass = itemView.findViewById(R.id.txtStudentClass);
            txtStudentId = itemView.findViewById(R.id.txtStudentId);
            checkBox = itemView.findViewById(R.id.checkBoxSelectStudent);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentStudent != null) {
                    listener.onItemClick(currentStudent);
                }
            });
        }

        public void bind(Student student) {
            currentStudent = student;
            txtStudentName.setText(student.getName());
            txtStudentClass.setText(student.getClass_name());
            txtStudentId.setText(String.valueOf(student.getStudentId()));
        }
    }
}
