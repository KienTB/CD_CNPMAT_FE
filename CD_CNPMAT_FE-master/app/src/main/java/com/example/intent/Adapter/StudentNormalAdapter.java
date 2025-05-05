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

public class StudentNormalAdapter extends RecyclerView.Adapter<StudentNormalAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public StudentNormalAdapter(List<Student> studentList, OnItemClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        if (student != null) {
            holder.bind(student);
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
        Student currentStudent;

        public StudentViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            txtStudentName = itemView.findViewById(R.id.txtStudentName);
            txtStudentClass = itemView.findViewById(R.id.txtStudentClass);
            txtStudentId = itemView.findViewById(R.id.txtStudentId);

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
