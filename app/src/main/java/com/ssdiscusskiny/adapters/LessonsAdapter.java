package com.ssdiscusskiny.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssdiscusskiny.R;
import com.ssdiscusskiny.activities.LessonActivity;
import com.ssdiscusskiny.data.Lesson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder>{

    private List<Lesson> lessonList;
    private Context mContext;
    public LessonsAdapter(Context mContext, List<Lesson> lessonList){
        this.mContext = mContext;
        this.lessonList = lessonList;
    }

    @NonNull
    @Override
    public LessonsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessonList.get(position);
        holder.itemTitle.setText(lesson.getTitle());
        holder.dateMark.setText(lesson.getDateKey().replace("_", "/"));
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemTitle,dateMark;
        public View mView;

        ViewHolder(View view){
            super(view);
            itemTitle = view.findViewById(R.id.item_title);
            dateMark = view.findViewById(R.id.item_date_mark);
            mView = view.findViewById(R.id.list_holder);
            view.setOnClickListener(v->{
                final int adapterPosition = getAdapterPosition();
                Lesson lesson = lessonList.get(adapterPosition);
                int dayId = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

                Date date;
                try {
                    date = new SimpleDateFormat("dd_MM_yyyy").parse(lesson.getDateKey());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    dayId = cal.get(Calendar.DAY_OF_WEEK);
                }catch (ParseException e){

                }

                Intent intentCal = new Intent(mContext, LessonActivity.class);
                intentCal.putExtra("date_value", lesson.getDateKey());
                intentCal.putExtra("day_id", dayId);
                intentCal.putExtra("caller", "ADAPTER");

                mContext.startActivity(intentCal);
            });
        }
    }
}