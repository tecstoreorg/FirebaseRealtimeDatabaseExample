package org.tec_store.firebaserealtimedatabaseexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Student> studentList = new ArrayList<>();
    StudentAdapter adapter;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        ref.child("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                studentList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Student student = snapshot.getValue(Student.class);
                    studentList.add(student);
                    adapter.notifyDataSetChanged();
                }

                Collections.reverse(studentList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        adapter = new StudentAdapter(MainActivity.this, studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);

        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query fireQuery = ref.child("students").orderByChild("name").equalTo(query);
                fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        } else {
                            List<Student> searchList = new ArrayList<Student>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Student student = snapshot.getValue(Student.class);
                                searchList.add(student);
                                adapter = new StudentAdapter(MainActivity.this, searchList);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter = new StudentAdapter(MainActivity.this, studentList);
                recyclerView.setAdapter(adapter);
                return false;
            }
        });
    }


}
