package com.ssdiscusskiny.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ssdiscusskiny.R;

import com.ssdiscusskiny.adapters.ParentAdapter;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.data.ChildItem;
import com.ssdiscusskiny.data.ParentItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BooksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private RecyclerView mRecyclerView;

    private FirebaseDatabase database;

    private DatabaseReference mLessonRef;

    private ParentAdapter parentAdapter;

    private List<String> lists = new ArrayList<>();

    private List<ParentItem> parentList;
    private ProgressBar progressBar;
    private TextView noLibraryTv;
    private MenuItem itemRefresh;

    public BooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BooksFragment newInstance() {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        // Inflate the layout for this fragment
        mRecyclerView = view.findViewById(R.id.m_library_recyclerview);
        noLibraryTv = view.findViewById(R.id.no_library_tv);
        progressBar = view.findViewById(R.id.library_progress);

        database = FirebaseDatabase.getInstance();
        mLessonRef = database.getReference()
                .child(Variables.content)
                .child("lessons");

        // Initialise the Linear layout manager
        LinearLayoutManager
                layoutManager
                = new LinearLayoutManager(
                getContext());

        parentList = new ArrayList<>();
        parentAdapter = new ParentAdapter(getContext(), ((AppCompatActivity)getActivity()), parentList);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(parentAdapter);

        if (itemRefresh!=null) itemRefresh.setVisible(false);
        mLessonRef.addListenerForSingleValueEvent(loadLessonPage);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.library));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("");
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
                getActivity().finish();
                break;
            case R.id.refresh:
                if (itemRefresh!=null) itemRefresh.setVisible(false);

                mRecyclerView.setVisibility(View.GONE);
                noLibraryTv.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                parentList.clear();

                parentAdapter.notifyDataSetChanged();

                progressBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.clockwise_rotation));
                mLessonRef.addListenerForSingleValueEvent(loadLessonPage);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    ValueEventListener loadLessonPage = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            snapshot.getRef().removeEventListener(this);
            for(DataSnapshot s : snapshot.getChildren()){
                final String key = s.getKey();
                if (key.matches("^\\d{4}$")){
                    lists.add(key);
                    //Keep this key before add in parent list and wait for child list to get filled

                    List<ChildItem> childItems = new ArrayList<>();
                    for (DataSnapshot s2 : s.getChildren()){

                        //Get image url
                        String subject = "";
                        //String imageUrl = "no_url";

                        String tempSub = s2.child("subject").getValue(String.class);
                        //String tempUrl = s2.child("image_url").getValue(String.class);

                        if (tempSub!=null) subject = tempSub;
                        //if (tempUrl!=null) imageUrl = tempUrl;

                        childItems.add(new ChildItem(key, s2.getKey(), subject));

                    }
                    Collections.sort(childItems, new M2Comparator());
                    parentList.add(new ParentItem(key, childItems));
                }
            }

            //Create adapter since parent list is filled
            Collections.sort(parentList, new MComparator());

            parentAdapter.notifyDataSetChanged();

            progressBar.clearAnimation();
            progressBar.setVisibility(View.GONE);

            if (parentAdapter.getItemCount()>0){
                mRecyclerView.setVisibility(View.VISIBLE);
            }else{
                noLibraryTv.setText(getString(R.string.empty_library));
                mRecyclerView.setVisibility(View.GONE);
                noLibraryTv.setVisibility(View.VISIBLE);
            }

            if (itemRefresh!=null){
                if (!itemRefresh.isVisible())itemRefresh.setVisible(true);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    class MComparator implements Comparator<ParentItem> {
        @Override
        public int compare(ParentItem o1, ParentItem o2) {
            if (o1.getParentKey() > o2.getParentKey()) {
                return -1;
            } else if (o1.getParentKey() < o2.getParentKey()) {
                return 1;
            }
            return 0;
        }}
    class M2Comparator implements Comparator<ChildItem> {
        @Override
        public int compare(ChildItem o1, ChildItem o2) {
            if (o1.getSecondKey().compareTo(o2.getSecondKey())>=1) {
                return -1;
            } else if (o1.getSecondKey().compareTo(o2.getSecondKey())<0) {
                return 1;
            }
            return 0;
        }}
}