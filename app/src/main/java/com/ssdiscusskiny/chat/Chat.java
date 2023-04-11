package com.ssdiscusskiny.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Chat implements Comparable<Chat>{
    private String message;
    private String id;
    private String time;
    private String date;
    private String messageId;
    private String zone;
    private String replyId;
    private String replyHintText;
    private String sender;
    private Date dateObject;
    private Calendar commentCal;
    private boolean isHighlighted = false;

    public Chat(@NonNull String message,@NonNull String messageId, @NonNull String id, @NonNull String time, @NonNull String date, @NonNull String zone) {
        this.message = message;
        this.messageId = messageId;
        this.id = id;
        this.date = date;
        this.time = time;
        this.zone = zone;

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone(zone));

            this.dateObject = sdf.parse(date+" "+time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateObject);
            Log.v("CHAT_MODEL", cal.getTime()+" "+cal.getTimeZone().getID());

            this.dateObject.setTime(cal.getTimeInMillis());

            commentCal = Calendar.getInstance();
            commentCal.setTime(dateObject);

            commentCal.set(Calendar.HOUR_OF_DAY, 0);
            commentCal.set(Calendar.MINUTE, 0);
            commentCal.set(Calendar.SECOND, 0);
            commentCal.set(Calendar.MILLISECOND, 0);

        }catch (ParseException ex){
            ex.printStackTrace();
        }
    }

    public Chat() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return new SimpleDateFormat("HH:mm").format(this.dateObject);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setZone(String zone){ this.zone = zone;}

    public String getZone(){return zone;}

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyHintText() {
        return replyHintText;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReplyHintText(String replyHintText) {
        this.replyHintText = replyHintText;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public Calendar getCommentCal() {
        return commentCal;
    }

    public void setCommentCal(Calendar commentCal) {
        this.commentCal = commentCal;
    }

    @Override
    public int compareTo(Chat chat) {
        return getDateObject().compareTo(chat.getDateObject());
    }

    @Override
    public String toString() {
        return messageId;
    }

    @Override
    public boolean equals(@Nullable Object chat) {
        Chat c = (Chat) chat;
        return this.messageId.equals(c.messageId);
    }

    public String chatSignature(){
        return getId()+" "+getMessage()+" "+dateObject.getTime();
    }
}
