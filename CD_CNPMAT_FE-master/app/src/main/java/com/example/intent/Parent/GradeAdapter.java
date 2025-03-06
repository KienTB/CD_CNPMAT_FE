package com.example.intent.Parent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Grade;
import com.example.intent.R;

import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {
    private List<Grade> gradeList;

    public GradeAdapter(List<Grade> gradeList) {
        this.gradeList = gradeList;
    }

    @NonNull
    @Override
    public GradeAdapter.GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeAdapter.GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeAdapter.GradeViewHolder holder, int position) {
        Grade grade = gradeList.get(position);
        if (grade != null) {
            holder.tvSubject.setText(grade.getSubject());
            holder.tvScore.setText(String.valueOf(grade.getScore()));
            holder.tvTerm.setText(grade.getTerm() != null ? grade.getTerm() : "Chưa cập nhật");
        }
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvScore, tvTerm;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvTerm = itemView.findViewById(R.id.tvTerm);
        }
    }

    public void updateData(List<Grade> newGrades) {
        this.gradeList = newGrades;
        notifyDataSetChanged();
    }
}
