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
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.adapters.ContentAdapter;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.data.ContentDataItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopicFragment extends Fragment {

    private String TAG = getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAM1 = "fKey";
    private static final String PARAM2 = "sKey";
    private static final String PARAM3 = "title";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private TextView noContentTv;
    private List<ContentDataItem> mContentData;
    private ContentAdapter contentAdapter;
    private MenuItem itemRefresh;
    private DatabaseReference reference;

    public TopicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopicFragment newInstance(String param1, String param2, String param3) {
        TopicFragment fragment = new TopicFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, param1);
        args.putString(PARAM2, param2);
        args.putString(PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContentData = new ArrayList<>();
        contentAdapter = new ContentAdapter(getContext(), (AppCompatActivity) getActivity(), mContentData);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(PARAM1);
            mParam2 = getArguments().getString(PARAM2);
            mParam3 = getArguments().getString(PARAM3, getContext().getString(R.string.library));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mParam3);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(Variables.formatTitle(getContext(), mParam1, mParam2));
        }else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.library));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        mRecyclerView = view.findViewById(R.id.m_library_recyclerview);


        noContentTv = view.findViewById(R.id.no_library_tv);
        progressBar = view.findViewById(R.id.library_progress);

        // Initialise the Linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference()
                .child(Variables.content)
                .child("lessons").child(mParam1).child(mParam2);


        mRecyclerView.setAdapter(contentAdapter);
        mRecyclerView.setLayoutManager(layoutManager);

        if (itemRefresh!=null) itemRefresh.setVisible(false);
        reference.addListenerForSingleValueEvent(dataListener);

        // Inflate the layout for this fragment
        return view;
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
                if (itemRefresh!=null) itemRefresh.setVisible(false);

                mRecyclerView.setVisibility(View.GONE);
                noContentTv.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                mContentData.clear();
                contentAdapter.notifyDataSetChanged();
                progressBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.clockwise_rotation));
                reference.addListenerForSingleValueEvent(dataListener);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            snapshot.getRef().removeEventListener(this);
            mContentData.clear();
            for(DataSnapshot s : snapshot.getChildren()){
                Log.v(TAG, s.getRef().toString());
                final String key = s.getKey();
                if (key.matches("^[a-zA-Z][0-9]+$")){
                    String title = s.child("title").getValue(String.class);
                    mContentData.add(new ContentDataItem(mParam1, mParam2, key, title));
                    Log.v(TAG, (title!=null) ? title : "");
                }
            }

            //Create adapter since parent list is filled
            Collections.sort(mContentData, new MComparator());

            contentAdapter.notifyDataSetChanged();

            progressBar.clearAnimation();
            progressBar.setVisibility(View.GONE);

            if (contentAdapter.getItemCount()>0){
                mRecyclerView.setVisibility(View.VISIBLE);
            }else{
                noContentTv.setText(getString(R.string.error_loading));
                mRecyclerView.setVisibility(View.GONE);
                noContentTv.setVisibility(View.VISIBLE);
            }

            if (itemRefresh!=null){
                if (!itemRefresh.isVisible())itemRefresh.setVisible(true);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    class MComparator implements Comparator<ContentDataItem> {
        public int compare(ContentDataItem o1, ContentDataItem o2) {
            return extractInt(o1.getThirdKey()) - extractInt(o2.getThirdKey());
        }

        int extractInt(String s) {
            String num = s.replaceAll("\\D", "");
            // return 0 if no digits found
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        }
    }
}