package com.ssdiscusskiny.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.adapters.LessonsAdapter;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.data.Lesson;
import com.ssdiscusskiny.tools.SimpleDividerItemDecoration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TitleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TitleFragment extends Fragment {

    private String TAG = getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private TextView noContentTv;
    private List<Lesson> lessonList;
    private LessonsAdapter lessonsAdapter;
    private MenuItem itemRefresh;
    private DatabaseReference reference;


    public TitleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TitleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TitleFragment newInstance(String param1, String param2, String param3, String param4) {
        TitleFragment fragment = new TitleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        lessonList = new ArrayList<>();
        lessonsAdapter = new LessonsAdapter(getContext(), lessonList);

        if (getArguments() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);

            Log.v(TAG, mParam1);
            Log.v(TAG, mParam2);
            Log.v(TAG, mParam3);
            Log.v(TAG, (mParam4!=null) ? mParam4 : "");

            reference = database.getReference()
                    .child(Variables.content)
                    .child("lessons")
                    .child(mParam1)
                    .child(mParam2)
                    .child(mParam3);
        }else{
            noContentTv.setText(getContext().getString(R.string.error_loading));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mParam4);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(Variables.formatTitle(mParam1, mParam2));
        }else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.library));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.library, menu);
        itemRefresh = menu.findItem(R.id.refresh);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.refresh:
                itemRefresh.setVisible(false);
                lessonList.clear();
                lessonsAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                reference.addListenerForSingleValueEvent(dataListener);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        mRecyclerView = view.findViewById(R.id.m_library_recyclerview);
        noContentTv = view.findViewById(R.id.no_library_tv);
        progressBar = view.findViewById(R.id.library_progress);
        mRecyclerView.setAdapter(lessonsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        reference.addListenerForSingleValueEvent(dataListener);
        return view;
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            snapshot.getRef().removeEventListener(this);
            lessonList.clear();
            for(DataSnapshot s : snapshot.getChildren()){
                final String key = s.getKey();
                if (key.matches("\\d{1,2}[\\_]\\d{1,2}[\\_]\\d{4}")){

                    String title = s.child("point").getValue(String.class);
                    lessonList.add(new Lesson(key, title));
                }
            }

            //Create adapter since parent list is filled
            Collections.sort(lessonList, new Comparator<Lesson>() {
                final DateFormat f = new SimpleDateFormat("dd_MM_yyyy");
                @Override
                public int compare(Lesson o1, Lesson o2) {
                    try {
                        return f.parse(o1.getDateKey()).compareTo(f.parse(o2.getDateKey()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });

            lessonsAdapter.notifyDataSetChanged();

            progressBar.clearAnimation();
            progressBar.setVisibility(View.GONE);

            if (lessonsAdapter.getItemCount()<=0){
                noContentTv.setText(getContext().getString(R.string.error_loading));
                mRecyclerView.setVisibility(View.GONE);
                noContentTv.setVisibility(View.VISIBLE);
            }else{
                mRecyclerView.setVisibility(View.VISIBLE);
                noContentTv.setVisibility(View.GONE);
            }

            if (itemRefresh!=null){
                if (!itemRefresh.isVisible())itemRefresh.setVisible(true);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}