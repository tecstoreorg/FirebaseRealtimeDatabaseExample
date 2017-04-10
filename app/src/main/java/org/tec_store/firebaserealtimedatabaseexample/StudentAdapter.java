package org.tec_store.firebaserealtimedatabaseexample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Devlomi on 05/04/2017.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentHolder> {
    private Context context;
    private List<Student> studentList;

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public StudentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_row, parent, false);
        return new StudentHolder(row);
    }

    @Override
    public void onBindViewHolder(StudentHolder holder, int position) {
        final Student student = studentList.get(position);
        holder.studentNameTv.setText(student.getName());
        holder.studentAvgTv.setText(String.valueOf(student.getAverage()));

        holder.editStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditStudentActivity.class);
                intent.putExtra("name", student.getName());
                context.startActivity(intent);
            }
        });


        holder.deleteStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();
                Query query = ref.child("students").orderByChild("name").equalTo(student.getName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentHolder extends RecyclerView.ViewHolder {
        TextView studentNameTv, studentAvgTv;
        Button deleteStudentBtn, editStudentBtn;

        public StudentHolder(View itemView) {
            super(itemView);
            studentNameTv = (TextView) itemView.findViewById(R.id.student_name);
            studentAvgTv = (TextView) itemView.findViewById(R.id.student_avg);
            deleteStudentBtn = (Button) itemView.findViewById(R.id.delete_student);
            editStudentBtn = (Button) itemView.findViewById(R.id.edit_student);


        }
    }
}
