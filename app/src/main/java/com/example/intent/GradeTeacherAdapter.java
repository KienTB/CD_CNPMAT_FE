package com.example.intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Grade;
import com.example.intent.Model.Schedule;
import com.example.intent.Parent.GradeAdapter;

import java.util.List;

public class GradeTeacherAdapter extends RecyclerView.Adapter<GradeTeacherAdapter.GradeViewHolder> {

    private List<Grade> gradeList;
    private GradeTeacherAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Grade grade);
    }

    public GradeTeacherAdapter(List<Grade> gradeList, GradeTeacherAdapter.OnItemClickListener listener) {
        this.gradeList = gradeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GradeTeacherAdapter.GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeTeacherAdapter.GradeViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeTeacherAdapter.GradeViewHolder holder, int position) {
        Grade grade = gradeList.get(position);
        if (grade != null) {
            holder.bind(grade);
        }
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public void updateData(List<Grade> newGrades) {
        this.gradeList = newGrades;
        notifyDataSetChanged();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject, tvScore, tvTerm;
        private Grade currentGrade;

        public GradeViewHolder(@NonNull View itemView, GradeTeacherAdapter.OnItemClickListener listener) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvTerm = itemView.findViewById(R.id.tvTerm);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentGrade != null) {
                    listener.onItemClick(currentGrade);
                }
            });
        }

        public void bind(Grade grade) {
            currentGrade = grade;
            tvSubject.setText(grade.getSubject());
            tvScore.setText(String.valueOf(grade.getScore()));
            tvTerm.setText(grade.getTerm());
        }
    }
}
