package com.ssdiscusskiny.adapters;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhprojects.bibleprojectkiny.SimpleFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.chat.Chat;
import com.ssdiscusskiny.database.DatabaseConnector;
import com.ssdiscusskiny.downloaders.FileDownloader;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.utils.Parser;

import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.text.ParseException;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final String TAG = ChatAdapter.class.getSimpleName();
    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;
    private List<Chat> chatList;
    private final Context mContext;
    final PanelHandler panelHandler;
    public DatabaseConnector db;
    private SimpleFormatter simpleFormatter;
    private Dialog textDialog;
    private final String accountName;
    private RecyclerView recyclerView;
    private int highlightedPos = -1;

    public ChatAdapter(Context mContext,@NonNull String accountName, List<Chat> chats) {
        chatList = chats;
        this.mContext = mContext;
        this.accountName = accountName;
        panelHandler = new PanelHandler(mContext);
        db = new DatabaseConnector(mContext);

        textDialog = new Dialog(mContext, R.style.MyDialogStyle);

        textDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        textDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        textDialog.setContentView(R.layout.text_display_dialog);
        textDialog.setCanceledOnTouchOutside(true);

        Window window = textDialog.getWindow();
        window.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        Spinner version = textDialog.findViewById(R.id.versionSpinner);
        TextView title = textDialog.findViewById(R.id.tvTitle);
        TextView content = textDialog.findViewById(R.id.tvContent);

        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, Variables.display_font_size);

        View dismissBtn = textDialog.findViewById(R.id.dismiss);
        dismissBtn.setOnClickListener(v -> textDialog.dismiss());

        simpleFormatter = new SimpleFormatter(mContext, panelHandler.checkFiLeInWrapper(FileDownloader.bibleFName), title, content, version, textDialog);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        this.recyclerView.addOnScrollListener(new CustomScrollListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == CHAT_END) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_end, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_start, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {

        if (chatList.get(position).getId().equals(accountName)) {

            String r = "";
            for (char c : chatList.get(position).getId().toCharArray()) {
                if (Character.isLetter(c))
                    r += c;
            }

            return CHAT_END;
        } else {
            return CHAT_START;
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Chat chat = chatList.get(position);

        Log.v(TAG, chat.getZone()+" "+chat.getMessageId());

        holder.bubbleHolder.setTag(position);
        holder.mTxtViewUser.setTag(position);
        holder.mTimeStamp.setTag(position);
        holder.mTxtViewTime.setTag(position);
        holder.replyTv.setTag(position);
        holder.replyHolder.setTag(position);

        if (db.isCommentNotSent(chat.getMessageId())){
            listen(chat.getMessageId());
        }

        if (chat.getReplyId()!=null) Log.v(TAG, "Reply: "+chat.getReplyId());

        if (position>=1){

            if (chatList.get(position).getId().equals(chatList.get(position-1).getId())){

                if (getItemViewType(position)==CHAT_END){
                    holder.bubbleHolder.setBackgroundResource(R.drawable.bg_speech_bubble_outgoing_1);
                }else{
                    holder.bubbleHolder.setBackgroundResource(R.drawable.bg_speech_bubble_incoming_1);
                    holder.mTxtViewUser.setVisibility(View.GONE);
                }
            }else{
                if (getItemViewType(position)==CHAT_END){
                    holder.bubbleHolder.setBackgroundResource(R.drawable.bg_speech_bubble_outgoing);
                }else{
                    holder.bubbleHolder.setBackgroundResource(R.drawable.bg_speech_bubble_incoming);
                    holder.mTxtViewUser.setVisibility(View.VISIBLE);
                }
            }

        }else{
            if (getItemViewType(position)==CHAT_END){
                holder.bubbleHolder.setBackgroundResource(R.drawable.bg_speech_bubble_outgoing);
                holder.sentStatus.setVisibility(View.VISIBLE);
            }else{
                holder.bubbleHolder.setBackgroundResource(R.drawable.bg_speech_bubble_incoming);
                holder.mTxtViewUser.setVisibility(View.VISIBLE);
                holder.sentStatus.setVisibility(View.GONE);
            }
        }
        holder.bubbleHolder.setPadding(dpAsPixel(13), dpAsPixel(7), dpAsPixel(20), dpAsPixel(5));

        if (position>=1){
            if (chat.getCommentCal().compareTo(chatList.get(position-1).getCommentCal())==0){
                holder.mTimeStamp.setVisibility(View.GONE);
            }else{
                holder.mTimeStamp.setVisibility(View.VISIBLE);
            }
        }else{
            holder.mTimeStamp.setVisibility(View.VISIBLE);
        }

        String dateTime = chat.getDate()+" "+chat.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat(getFormat(dateTime));

        dateTime = dateFormat.format(chat.getCommentCal().getTime());

        Calendar calNow = Calendar.getInstance();
        calNow.set(Calendar.HOUR_OF_DAY, 0);
        calNow.set(Calendar.MINUTE, 0);
        calNow.set(Calendar.SECOND, 0);
        calNow.set(Calendar.MILLISECOND, 0);

        Calendar calYest = Calendar.getInstance();
        calYest.set(Calendar.HOUR_OF_DAY, 0);
        calYest.set(Calendar.MINUTE, 0);
        calYest.set(Calendar.SECOND, 0);
        calYest.set(Calendar.MILLISECOND, 0);
        calYest.add(Calendar.DATE, -1);

        if (calNow.compareTo(chat.getCommentCal())==0){
            holder.mTimeStamp.setText(mContext.getString(R.string.today));
        }else{
            if (calYest.compareTo(chat.getCommentCal())==0){
                holder.mTimeStamp.setText(mContext.getString(R.string.yesterday));
            }else{
                holder.mTimeStamp.setText(Parser.regexDate(dateTime));
            }
        }

        holder.mTxtViewTime.setText(chat.getTime()); //Parser.regexTime(dateTime)
        holder.mTextView.setText(simpleFormatter.formatLine(chat.getMessage(), holder.mTextView, false));
        holder.mTxtViewUser.setText(chat.getId());

        if (chat.getReplyId()!=null){
            holder.replyHolder.setVisibility(View.VISIBLE);
            holder.senderTv.setText(chat.getSender());
            holder.replyTv.setText(chat.getReplyHintText());
            holder.rowLayout.requestLayout();
            holder.replyHolder.setOnClickListener(view -> {
                //Get reply location
                Chat c = new Chat();
                c.setMessageId(chat.getReplyId());
                int pos = chatList.indexOf(c);
                Log.v(TAG, "Reply founded at "+pos);

                if (recyclerView!=null) {
                    //recyclerView.
                    highlightedPos = pos;
                    Log.v(TAG, "highlighted Pos "+highlightedPos);

                    ViewHolder vHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
                    if (vHolder!=null){
                        Handler handler = new Handler();
                        handler.postDelayed(()->{
                            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.shake);

                            vHolder.rowLayout.startAnimation(anim);
                            highlightedPos = -1;
                        }, 500);

                    }else{
                        Log.v(TAG, "V IS NULL");
                        recyclerView.scrollToPosition(pos);
                    }
                }
            });
        }else{
            holder.replyHolder.setVisibility(View.GONE);
        }


        if (getItemViewType(position)==CHAT_END){
            if (db.isCommentNotSent(chat.getMessageId())){
                holder.sentStatus.setImageResource(R.drawable.ic_pend);
            }else{
                holder.sentStatus.setImageResource(R.drawable.ic_sent);
            }
            holder.sentStatus.setVisibility(View.VISIBLE);
        }else{
            holder.sentStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    /**
     * Inner Class for a recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView, mTxtViewUser, mTxtViewTime, mTimeStamp, senderTv, replyTv;
        ImageView sentStatus;
        LinearLayout bubbleHolder;
        RelativeLayout replyHolder,rowLayout;


        ViewHolder(View v) {
            super(v);

            mTextView = itemView.findViewById(R.id.tvMessage);
            mTxtViewUser = itemView.findViewById(R.id.username);
            mTxtViewTime = itemView.findViewById(R.id.datetime);
            bubbleHolder = itemView.findViewById(R.id.bubble_holder);
            mTimeStamp = itemView.findViewById(R.id.dateStamp);
            sentStatus = itemView.findViewById(R.id.sent_status);
            senderTv = itemView.findViewById(R.id.reply_sender_tv);
            replyTv = itemView.findViewById(R.id.reply_tv);
            replyHolder = itemView.findViewById(R.id.reply_holder);
            rowLayout = itemView.findViewById(R.id.comment_row);

            sentStatus.setVisibility(View.GONE);
            replyHolder.setVisibility(View.GONE);
        }
    }

    private static Calendar getCal(String dateS, TimeZone timezone) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat(getFormat(dateS));
        sdf.setTimeZone(timezone);

        Date date = sdf.parse(dateS);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal;
    }

    private Date tryParse(String dateString){
        List<String> formatStrings = Arrays.asList("dd/MM/yyyy HH:mm", "dd/MM/yy HH:mm", "dd/MM HH:mm");
        for (String formatString : formatStrings){
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            }catch (ParseException e){

            }
        }
        return Calendar.getInstance().getTime();
    }

    private SimpleDateFormat tryParse2(String dateString){
        List<String> formatStrings = Arrays.asList("dd/MM/yyyy HH:mm", "dd/MM/yy HH:mm", "dd/MM HH:mm");
        for (String formatString : formatStrings){
            try {
                new SimpleDateFormat(formatString).parse(dateString);
                return new SimpleDateFormat(formatString);
            }catch (ParseException e){
                //Log.e("PARSEDATE", e.getMessage());
            }
        }
        return new SimpleDateFormat(formatStrings.get(0));
    }

    private static String getFormat(String valueDateTime) {
        String[] formats = {
                "\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{4} .*",
                "\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2} .*",
                "\\d{1,2}[/.-]\\d{1,2} .*"
        };

        for (String format : formats) {
            if (valueDateTime.matches(format)) {
                if (format.equals(formats[0])) {
                    return "dd/MM/yyyy HH:mm";
                }else if (format.equals(formats[1])){
                    return "dd/MM/yy HH:mm";
                }else if (format.equals(formats[2])){
                    return "dd/MM HH:mm";
                }
            }
        }

        return "dd/MM/yyyy HH:mm";
    }

    private static String getFormat2(String valueDate) {
        String[] formats = {
                "\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{4} .*",
                "\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2} .*",
                "\\d{1,2}[/.-]\\d{1,2} .*"
        };

        for (String format : formats) {
            if (valueDate.matches(format)) {
                if (format.equals(formats[0])) {
                    return "dd/MM/yyyy";
                }else if (format.equals(formats[1])){
                    return "dd/MM/yy";
                }else if (format.equals(formats[2])){
                    return "dd/MM";
                }
            }
        }

        return "dd/MM/yyyy HH:mm";
    }

    private int dpAsPixel(int sizeInDp){
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale+0.5f);
    }
    private void listen(final String msgId){
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        fd.getReference().child(Variables.content).child("comments").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                if (error!=null||!committed||currentData==null){
                    Log.d("ChatAdapter", "Failed to get snapshot");
                }else{
                   if (currentData.hasChild(msgId)){
                       db.clearNotSent(msgId);
                       notifyDataSetChanged();
                   }
                }

            }
        });
    }
    /*ValueEventListener commentStatusListener = new ValueEventListener(){

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if ()
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };*/

    /*private void fetchReplyMessage(final String replyKey, final TextView tvSender, final TextView tvMessage){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Variables.content)
                .child("comments").child(replyKey);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeEventListener(this);
                if (snapshot.exists()){
                    HashMap<String, String> comment = (HashMap<String,String>)snapshot.getValue();
                    String replyMessage = comment.get("message");
                    String replyUser = comment.get("user");

                    replyUser = replyUser.equals(accountName) ? mContext.getString(R.string.you) : replyUser;

                    tvSender.setText(replyUser);
                    tvMessage.setText(replyMessage);
                }else{
                    tvSender.setText("");
                    tvMessage.setText(R.string.reply_deleted);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    public class CustomScrollListener extends RecyclerView.OnScrollListener {
        public CustomScrollListener() {

        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    System.out.println("The RecyclerView is not scrolling");
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    System.out.println("Scrolling now");
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    System.out.println("Scroll Settling");
                    break;

            }

        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int pos = ((LinearLayoutManager)recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
            Log.v(TAG, "Scrolled to: "+pos);
            if (pos==highlightedPos){
                ViewHolder vHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
                if (vHolder!=null){
                    Handler handler = new Handler();
                    handler.postDelayed(()->{
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.shake);

                        vHolder.rowLayout.startAnimation(anim);
                    }, 500);

                }else{
                    Log.v(TAG, "V IS NULL");
                }
                highlightedPos = -1;
            }
            if (dx > 0) {
                System.out.println("Scrolled Right");
            } else if (dx < 0) {
                System.out.println("Scrolled Left");
            } else {
                System.out.println("No Horizontal Scrolled");
            }

            if (dy > 0) {
                System.out.println("Scrolled Downwards");
            } else if (dy < 0) {
                System.out.println("Scrolled Upwards");
            } else {
                System.out.println("No Vertical Scrolled");
            }
        }
    }

}