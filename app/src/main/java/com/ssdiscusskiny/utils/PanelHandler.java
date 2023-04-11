package com.ssdiscusskiny.utils;

import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.view.LayoutInflater;
import android.view.Window;

import android.view.Gravity;
import android.app.Dialog;

import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.widget.DatePicker;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridLayout.LayoutParams;

import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bhprojects.bibleprojectkiny.SimpleFormatter;
import com.bhprojects.bibleprojectkiny.UiSetters;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;

import com.ssdiscusskiny.ExpiredActivity;
import com.ssdiscusskiny.LoginActivity;

import com.ssdiscusskiny.activities.IntroActivity;
import com.ssdiscusskiny.activities.LessonActivity;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.downloaders.FileDownloader;
import com.ssdiscusskiny.generator.Randoms;

import android.content.ContextWrapper;

import com.ssdiscusskiny.R;

import java.io.File;

public class PanelHandler {
    private Context mContext;
    public Handler handler = new Handler();
    public WaitPendAlert waitRun;
    public AlertDialog mAlertDialog;
    public boolean cancelNtf = false;
    public Dialog mDialog;
    public TextView titleTv,contentTv;
    public Spinner versionSpn;
    private View dialogClose;
    public View pBarView;
    public AlertDialog alertProgressBar;
    private final String TAG = PanelHandler.class.getSimpleName();
    public AlertDialog exitAlertDialog;

    public PanelHandler(Context mContext) {
        this.mContext = mContext;
        pBarView = View.inflate(mContext, R.layout.pbar_alertdialog, null);
        alertProgressBar = new AlertDialog.Builder(mContext).create();
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);
        initializeTextDialog();
    }

    public Dialog showNotificationDialog(final String head, final String message, final int extra) {

        final View dialogView = View.inflate(mContext, R.layout.message_dialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        ImageButton closeBtn = dialogView.findViewById(R.id.closeBtn);
        TextView title = dialogView.findViewById(R.id.ntf_hd);
        TextView text = dialogView.findViewById(R.id.ntf_msg);
        text.setText(message);
        title.setText(head);

        closeBtn.setOnClickListener(view -> {

            if (extra == 1) {
                dialog.dismiss();
            }
            if (extra == 2) {
                Variables.ntfShowed = false;
                ((AppCompatActivity) mContext).finish();
            }
        });

        if (!cancelNtf) {
            dialog.show();
        }

        return dialog;
    }

    private void getPendAlert(final boolean showNot, final FirebaseDatabase database) {
        if (showNot) {
            final DatabaseReference ntfRef = database.getReference().child(Variables.content).child("alerts").child("main").child("content");
            final DatabaseReference titleRef = database.getReference().child(Variables.content).child("alerts").child("main").child("title");

            titleRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String title = dataSnapshot.getValue(String.class);
                    if (title != null) {
                        ntfRef.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String msg = dataSnapshot.getValue(String.class);
                                if (msg != null) {
                                    if (!msg.isEmpty() && !Variables.ntfShowed) {
                                        if (Variables.showAlert) {
                                            showNotificationDialog(title, msg, 1);
                                            Variables.ntfShowed = true;
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError dError) {
                            }


                        });
                    } else {
                        ntfRef.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String msg = dataSnapshot.getValue(String.class);
                                if (msg != null) {
                                    if (!msg.isEmpty() && !Variables.ntfShowed) {
                                        showNotificationDialog("", msg, 1);
                                        Variables.ntfShowed = true;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError dError) {
                            }

                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError dError) {
                }


            });
        }
    }

    private class WaitPendAlert implements Runnable {
        boolean showNot;
        FirebaseDatabase database;

        public WaitPendAlert(final boolean showNot, final FirebaseDatabase database) {
            this.showNot = showNot;
            this.database = database;
        }

        @Override
        public void run() {
            getPendAlert(showNot, database);
        }


    };

    public void loadPend(final boolean showNot, final FirebaseDatabase database) {
        waitRun = new WaitPendAlert(showNot, database);
        handler.postDelayed(waitRun, 15000);
    }

    public int getVersionCode() {
        try {
            return mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public void getAppState(final FirebaseDatabase database) {
        final DatabaseReference stateRef = database.getReference().child("admin").child("access");

        final DatabaseReference ref = database.getReference();

        stateRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnaShot) {
                String appState = dataSnaShot.getValue(String.class);
                stateRef.removeEventListener(this);
                if (appState != null && !appState.isEmpty()) {
                    if (appState.equals("off") || appState.equals("expired")) {
                        Variables.ntfShowed = false;
                        mContext.startActivity(new Intent(mContext, ExpiredActivity.class));
                        ((AppCompatActivity) mContext).finish();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError dbError) {
            }


        });

        ref.child("admin").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dSnap) {
                // TODO: Implement this method
                ref.child("admin").removeEventListener(this);
                if (!dSnap.hasChild("state")) {
                    mContext.startActivity(new Intent(mContext, ExpiredActivity.class));
                    ((AppCompatActivity) mContext).finish();
                }
                if (dSnap.hasChild("state")) {

                }
                if (dSnap.hasChild("blocked")) {

                }
            }

            @Override
            public void onCancelled(DatabaseError dError) {
                // TODO: Implement this method
            }


        });
    }

    public void getAccessState(final FirebaseDatabase database, final String email) {
        final DatabaseReference blocked = database.getReference().child("admin").child("blocked");
        blocked.keepSynced(true);
        blocked.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snap) {
                // TODO: Implement this method
                blocked.removeEventListener(this);

                if (snap.hasChild(email)) {

                    showNotificationDialog(mContext.getString(R.string.denied_title), mContext.getString(R.string.denied_msg), 2);
                    cancelNtf = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError dbError) {
                // TODO: Implement this method
            }


        });
    }

    public void goToLesson() {
        final View dialogView = View.inflate(mContext, R.layout.date_picker, null);
        final Dialog dlg = new Dialog(mContext, R.style.MyDialogStyle);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(dialogView);

        Window window = dlg.getWindow();
        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        final DatePicker datePicker = dialogView.findViewById(R.id.picker);

        datePicker.setFirstDayOfWeek(Calendar.SUNDAY);

        final Button setExtra = dialogView.findViewById(R.id.sendExtra);
        setExtra.setOnClickListener(v -> {
            // TODO: Implement this method
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
            String dateId = sdf.format(getPickedDate(datePicker));
            Calendar cal = Calendar.getInstance();
            cal.setTime(getPickedDate(datePicker));
            int dayId = cal.get(Calendar.DAY_OF_WEEK);

            Log.v(TAG, "DAY ID: "+dayId);

            Intent lessonIntent = new Intent(mContext, LessonActivity.class);
            lessonIntent.putExtra("date_value", dateId);
            lessonIntent.putExtra("day_id", dayId);
            lessonIntent.putExtra("caller", "OPEN");
            lessonIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            lessonIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(lessonIntent);
            ((AppCompatActivity) mContext).overridePendingTransition(0, 0);
            dlg.dismiss();
        });
        dlg.show();

    }

    private Date getPickedDate(DatePicker dpk) {
        int day = dpk.getDayOfMonth();
        int month = dpk.getMonth();
        int year = dpk.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public void showExit() {
        final View dialogView = View.inflate(mContext, R.layout.action_dialog, null);
        exitAlertDialog = new AlertDialog.Builder(mContext).create();
        exitAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitAlertDialog.setView(dialogView);
        exitAlertDialog.setCancelable(false);

        Button closeBtn = dialogView.findViewById(R.id.accept);
        Button dismiss = dialogView.findViewById(R.id.cancel);
        TextView closeTxt = dialogView.findViewById(R.id.dialog_txt);
        TextView closeTitle = dialogView.findViewById(R.id.exit_dlg_title);

        closeTitle.setText(mContext.getString(R.string.closing_app));
        closeTxt.setText(R.string.close_request_hint);

        if (mContext.getClass().getSimpleName().equals("MainActivity")) {
            closeTxt.setText(Randoms.exitsMsgs(mContext));
        }

        closeBtn.setOnClickListener(v -> {
            // TODO: Implement this method
            Variables.ntfShowed = false;
            exitAlertDialog.dismiss();
            ((AppCompatActivity) mContext).finish();

        });
        dismiss.setOnClickListener(v -> {
            // TODO: Implement this method
            if (exitAlertDialog.isShowing()) {
                exitAlertDialog.dismiss();
            }

        });
        exitAlertDialog.show();
    }

    public void openActivityRequestDialog(String title, String txtMsg, String negMsg, String posMessage, String extra, Class activityClass){
        final View dialogView = View.inflate(mContext, R.layout.action_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);

        Button openBtn = dialogView.findViewById(R.id.accept);
        Button dismiss = dialogView.findViewById(R.id.cancel);

        TextView dialogTv = dialogView.findViewById(R.id.dialog_txt);
        TextView titleTv = dialogView.findViewById(R.id.exit_dlg_title);

        titleTv.setText(title);
        openBtn.setText(posMessage);
        dismiss.setText(negMsg);
        dialogTv.setText(txtMsg);

        dismiss.setOnClickListener(v -> {
            // TODO: Implement this method
            alertDialog.dismiss();

        });
        openBtn.setOnClickListener(v -> {
            // TODO: Implement this method
            Intent intent = new Intent(mContext, activityClass);

            if (extra!=null) intent.putExtra("id", extra);

            mContext.startActivity(intent);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }
    public void readRequest(final AppCompatActivity activity) {
        final View dialogView = View.inflate(mContext, R.layout.action_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);

        Button closeBtn = dialogView.findViewById(R.id.accept);
        Button dismiss = dialogView.findViewById(R.id.cancel);

        TextView closeTxt = dialogView.findViewById(R.id.dialog_txt);
        TextView closeTitle = dialogView.findViewById(R.id.exit_dlg_title);

        closeTitle.setText(mContext.getString(R.string.read_title));
        closeBtn.setText(mContext.getString(R.string.read));
        dismiss.setText(mContext.getString(R.string.later));
        closeTxt.setText(mContext.getString(R.string.read_intro));

        dismiss.setOnClickListener(v -> {
            // TODO: Implement this method
            alertDialog.dismiss();

        });
        closeBtn.setOnClickListener(v -> {
            // TODO: Implement this method
            activity.startActivity(new Intent(activity, IntroActivity.class));
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    public void downloadPlugin(final SharedPreferences appPrefs,String url, final boolean calledToUpdate) {
        final FileDownloader fileDownloader = new FileDownloader(mContext, this);

        final View dialogView = View.inflate(mContext, R.layout.download_request, null);
        final AlertDialog downloadAlertDialog = new AlertDialog.Builder(mContext).create();
        downloadAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        downloadAlertDialog.setView(dialogView);
        downloadAlertDialog.setCancelable(false);

        Button downloadBtn = dialogView.findViewById(R.id.downloadAccept);
        Button dismiss = dialogView.findViewById(R.id.downloadCancel);
        TextView dwnTxt = dialogView.findViewById(R.id.downloadTxt);

        boolean lastComplete = appPrefs.getBoolean("bibleCompleted", false);

        if (checkFiLeInWrapper(FileDownloader.bibleFName)&&lastComplete) {
            dwnTxt.setText(mContext.getString(R.string.update_plugin_msg));
        }
        downloadBtn.setOnClickListener(v -> {
            // TODO: Implement this method
            downloadAlertDialog.dismiss();
            File file = new File("/data/data/" +mContext.getPackageName() + "/" + "databases/");
            fileDownloader.downloadPlugin(file, url, appPrefs);
        });
        dismiss.setOnClickListener(v -> {
            // TODO: Implement this method
            if (downloadAlertDialog.isShowing()) {
                downloadAlertDialog.dismiss();
            }

        });

        if (!calledToUpdate) {
            downloadAlertDialog.show();
        }else{
            File file = new File("/data/data/" +mContext.getPackageName() + "/" + "databases/");
            fileDownloader.downloadPlugin(file, url, appPrefs);
        }
    }

    public static int flags() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        return flags;
    }

    public void showToast(String msg) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast, null);
        TextView text = view.findViewById(R.id.toasttext);
        text.setText(msg);
        Toast toast = new Toast(mContext);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public void createUsernameAlert() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.toast_create_name, null);
        final AlertDialog dlgCreateAlert = new AlertDialog.Builder(mContext).create();
        dlgCreateAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlgCreateAlert.setView(view);
        dlgCreateAlert.setCancelable(false);
        Button create = view.findViewById(R.id.createname);
        ImageButton close = view.findViewById(R.id.clsbtn);

        close.setOnClickListener(v -> {
            // TODO: Implement this method
            dlgCreateAlert.dismiss();
        });
        create.setOnClickListener(v -> {
            // TODO: Implement this method
            dlgCreateAlert.dismiss();
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra("login", "setName");
            mContext.startActivity(intent);
            ((AppCompatActivity) mContext).finish();
        });
        dlgCreateAlert.show();
    }

    public boolean canComment(View view, String key) {
        if (key.equals("[noUsername]")) {
            if (view != null) view.setVisibility(View.GONE);
            return false;
        } else {
            if (view != null) view.setVisibility(View.VISIBLE);
            return true;
        }

    }

    public Button showNetError(final int call, String netError) {

        final View dialogView = View.inflate(mContext, R.layout.action_dialog, null);
        mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialog.setView(dialogView);

        if (call==0) mAlertDialog.setCancelable(false);
        else mAlertDialog.setCancelable(true);

        Button closeBtn = dialogView.findViewById(R.id.accept);
        Button dismiss = dialogView.findViewById(R.id.cancel);

        TextView closeTxt = dialogView.findViewById(R.id.dialog_txt);
        TextView closeTitle = dialogView.findViewById(R.id.exit_dlg_title);

        closeTitle.setText(R.string.no_net_title);
        closeBtn.setText(R.string.retry);
        dismiss.setText(R.string.close);
        closeTxt.setText(netError);

        dismiss.setOnClickListener(v -> {
            // TODO: Implement this method
            mAlertDialog.dismiss();

            if (call == 0) {
                ((AppCompatActivity) mContext).finish();
            }

        });
        mAlertDialog.show();

        return closeBtn;
    }

    public Button buttonDialog(String detail, String title, String negBtn, String posBtn) {
        final View dialogView = View.inflate(mContext, R.layout.action_dialog, null);
        mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialog.setView(dialogView);
        mAlertDialog.setCancelable(false);

        Button closeBtn = dialogView.findViewById(R.id.accept);
        Button dismiss = dialogView.findViewById(R.id.cancel);

        TextView closeTxt = dialogView.findViewById(R.id.dialog_txt);
        TextView closeTitle = dialogView.findViewById(R.id.exit_dlg_title);
        closeTitle.setText(title);
        closeBtn.setText(posBtn);

        if (negBtn!=null&&!negBtn.isEmpty()) {
            dismiss.setText(negBtn);
        } else if(negBtn.isEmpty()){
            dismiss.setVisibility(View.GONE);
            mAlertDialog.setCancelable(false);
        } else {
            dismiss.setVisibility(View.GONE);
            mAlertDialog.setCancelable(true);
        }

        closeTxt.setText(detail);

        dismiss.setOnClickListener(v -> {
            // TODO: Implement this method
            Log.v(TAG, "DISMISSED");
            mAlertDialog.dismiss();
        });

        mAlertDialog.show();

        return closeBtn;
    }

    public void generateFeatureDescription(Parser parse, SharedPreferences appPrefs) {
        if (parse.getRunCount(appPrefs) == 3) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.fav_text), mContext.getString(R.string.highlight_msg), 1);
        }  else if (parse.getRunCount(appPrefs) == 4) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.app_name), mContext.getString(R.string.ads_reason), 1);
        }else if (parse.getRunCount(appPrefs) == 11) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.change_font), mContext.getString(R.string.c_font_msg).toString(), 1);
        } else if (parse.getRunCount(appPrefs) == 12) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.purpose), mContext.getString(R.string.sabbath_school).toString(), 1);
        }else if (parse.getRunCount(appPrefs) == 14) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.night_mode_title), mContext.getString(R.string.night_mode_msg), 1);
        }else if (parse.getRunCount(appPrefs) == 16) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.highlight_title), mContext.getString(R.string.highlight_msg), 1);
        }
        else if (parse.getRunCount(appPrefs) == 18) {
            Variables.ntfShowed = true;
            showNotificationDialog("", mContext.getString(R.string.hint_verses), 1);
        }
        else if (parse.getRunCount(appPrefs) == 20) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.pm_title), mContext.getString(R.string.pm_txt), 1);
        }else if (parse.getRunCount(appPrefs) == 22 || parse.getRunCount(appPrefs) == 52) {
            Variables.ntfShowed = true;
            showNotificationDialog(mContext.getString(R.string.about_ntf), mContext.getString(R.string.notify_msg), 1);
        }
        else if (parse.getRunCount(appPrefs) == 26 || parse.getRunCount(appPrefs) == 29 || parse.getRunCount(appPrefs) == 39) {
            Variables.ntfShowed = true;
            showNotificationDialog("Change font family", "You set font family to your favorite!\n\nGo in setting, and click on choose font type and select font you want", 1);
        }

    }

    public boolean checkFiLeInWrapper(String fileName) {
        ContextWrapper contextWrapper = new ContextWrapper(mContext.getApplicationContext());
        File directory = contextWrapper.getDatabasePath(fileName);

        if (directory.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public void readVerse(String reference){
        Dialog textDialog = new Dialog(mContext, R.style.MyDialogStyle);

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

        //Get Verse
        SimpleFormatter simpleFormatter = new SimpleFormatter(mContext, checkFiLeInWrapper(FileDownloader.bibleFName), title, content, version, textDialog);
        simpleFormatter.processTypedReference(reference);
    }

    public UiSetters uiSetters(){
        UiSetters setters;
        setters = new UiSetters(R.layout.text_display_dialog, R.id.tvContent, R.id.tvTitle, R.id.dismiss, R.id.versionSpinner);
        return setters;
    }

    private void initializeTextDialog(){
        mDialog = new Dialog(mContext, R.style.MyDialogStyle);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.text_display_dialog);
        mDialog.setCanceledOnTouchOutside(true);

        Window window = mDialog.getWindow();
        window.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        versionSpn = mDialog.findViewById(R.id.versionSpinner);
        titleTv = mDialog.findViewById(R.id.tvTitle);
        contentTv = mDialog.findViewById(R.id.tvContent);

        this.dialogClose = mDialog.findViewById(R.id.dismiss);
        this.dialogClose.setOnClickListener(v -> mDialog.dismiss());

    }

    public void setDisplayTextSize(){
        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Variables.display_font_size);
    }

    public void shareApplication() {
        ApplicationInfo app = mContext.getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location=
            File tempFile = new File(mContext.getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + mContext.getString(app.labelRes)+".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            System.out.println("File copied.");
            //Open share dialog
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            Uri photoURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", tempFile);
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            ((AppCompatActivity)mContext).startActivityForResult (Intent.createChooser(intent, mContext.getString(R.string.choose_share)), 10);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRateDialog(int runCount, String accountID, SharedPreferences prefs) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refRatings = database.getReference()
                .child("other").child("ratings");
        refRatings.keepSynced(true);
        if (runCount>=18){
            final View dialogView = View.inflate(mContext, R.layout.rating_dialog, null);
            final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(true);

            Window window = dialog.getWindow();
            window.setLayout(GridLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);

            dialog.setCancelable(false);

            Button rateBtn = dialogView.findViewById(R.id.rate_btn);
            Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);
            final RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);

            cancelBtn.setOnClickListener(v-> dialog.dismiss());

            rateBtn.setOnClickListener(v->{
                if (ratingBar.getRating()!=0){
                    dialog.dismiss();
                    prefs.edit().putBoolean(getVersionCode()+"_rated", true).apply();
                    if (ratingBar.getRating()>=4) showToast(mContext.getString(R.string.thank_you));
                    else showNotificationDialog(mContext.getString(R.string.rating), mContext.getString(R.string.low_rating_msg), 1);
                    refRatings
                            .child(accountID)
                            .child("rate")
                            .setValue(ratingBar.getRating());
                   refRatings
                            .child(accountID)
                            .child("version")
                            .setValue(getVersionCode());
                }else{
                    showToast(mContext.getString(R.string.no_rate_hint));
                }



            });

            boolean ratedVersion = prefs.getBoolean(getVersionCode()+"_rated", false);

            if (!ratedVersion){
                dialog.show();
            }


        }else{
            Log.v(TAG, "RUNS: "+runCount);
        }

    }

}
