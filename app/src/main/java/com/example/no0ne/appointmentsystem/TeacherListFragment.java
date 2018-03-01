package com.example.no0ne.appointmentsystem;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherListFragment extends Fragment {

    private View mMainView;

    private RecyclerView mTeachersRecyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    private FirebaseRecyclerAdapter mRecyclerAdapter;

    public TeacherListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_teacher_list, container, false);

        mTeachersRecyclerView = mMainView.findViewById(R.id.recycler_view_teacher_list);

        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserReference.keepSynced(true);

        mTeachersRecyclerView.setHasFixedSize(true);
        mTeachersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addAdapter();

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRecyclerAdapter.stopListening();
    }

    private void addAdapter() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Teachers");

        FirebaseRecyclerOptions<Teacher> options = new FirebaseRecyclerOptions.Builder<Teacher>()
                .setQuery(query, Teacher.class)
                .build();

        mRecyclerAdapter = new FirebaseRecyclerAdapter<Teacher, TeacherViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TeacherViewHolder holder, int position, @NonNull final Teacher model) {
                Log.e("ON_BIND_VIEW_HOLDER", "onBindViewHolder() is called");

                holder.setName(model.getName());
                holder.setDepartment(model.getDepartment());
                holder.setImage(model.getImage());
            }

            @Override
            public TeacherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.e("ON_CREATE_VIEW", "onCreateViewHolder() is called");

                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.single_row_layout, parent, false);

                return new TeacherViewHolder(view);
            }
        };

//        Log.e("ON_START", "onStart() is called");
//        mRecyclerAdapter.notifyDataSetChanged();
        mTeachersRecyclerView.setAdapter(mRecyclerAdapter);
    }
}
