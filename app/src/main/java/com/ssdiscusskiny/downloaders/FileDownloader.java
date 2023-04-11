package com.ssdiscusskiny.downloaders;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssdiscusskiny.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.utils.PanelHandler;

public class FileDownloader {
    private final String TAG = FileDownloader.class.getSimpleName();
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public StorageReference bibleRef, refStartImg, refSkinImg;
    private ContextWrapper contextWrapper;
    private File directory;
    private Context mContext;
    private PanelHandler pHandler;
    public static final String plugOldName = "bible_kiny";
    public static final String bibleFName = "kiny_bible";
    public static final String themeName = "theme.png";
    public static final String skinName = "skin.png";
    public static final String folderOfFiles = "Files";
    private File wrapperDir;


    public FileDownloader(Context mContext, PanelHandler pHandler) {
        this.mContext = mContext;
        contextWrapper = new ContextWrapper(mContext.getApplicationContext());
        directory = contextWrapper.getDatabasePath(bibleFName);
        wrapperDir = contextWrapper.getFilesDir();
        Log.v(TAG, wrapperDir.getAbsolutePath());
        this.pHandler = pHandler;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final Calendar calendar = Calendar.getInstance();

        refStartImg = storageReference.child("images/" + Grab.year(calendar) + "/" + Grab.quarter(calendar) + "/" + themeName);
        refSkinImg = storageReference.child("images/" + Grab.year(calendar) + "/" + Grab.quarter(calendar) + "/" + skinName);
        bibleRef = storageReference.child("db/" + bibleFName);

    }

    public void downloadResources(StorageReference storageRef, final String name, final SharedPreferences appPrefs) throws IOException {
        Log.v(TAG, storageRef.toString());

        final AlertDialog alertProgressbar = new AlertDialog.Builder(mContext).create();
        View pBarView = View.inflate(mContext, R.layout.download_dialog, null);
        alertProgressbar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressbar.setView(pBarView);
        final TextView tvBar = pBarView.findViewById(R.id.pbartext);
        final ProgressBar proBar = pBarView.findViewById(R.id.progressBar);
        proBar.setMax(100);

        //------------------------------------------------------------
        directory = mContext.getFilesDir();
        if (name.equals(themeName)) {
            directory = new File(directory, themeName);
        } else if (name.equals(skinName)) {
            directory = new File(directory, skinName);
        } else {
            directory = mContext.getDatabasePath(bibleFName);
        }

        if (name.equals(bibleFName)) {
            pHandler.showToast(contextWrapper.getString(R.string.download_warn));
            alertProgressbar.setCancelable(false);
            alertProgressbar.show();
            appPrefs.edit().putBoolean("bibleCompleted", false).commit();
        }

        storageRef.getFile(directory).addOnSuccessListener(taskSnapshot -> {
                    // Local temp file has been created
                    if (name.equals(themeName)) {
                        appPrefs.edit().putString("startImgData", "Downloaded").commit();
                    }
                    if (name.equals(skinName)) {
                        appPrefs.edit().putString("skinData", "Downloaded").commit();
                    }
                    if (name.equals(bibleFName)) {
                        alertProgressbar.dismiss();
                        appPrefs.edit().putString("bibleData", "Downloaded").commit();
                        appPrefs.edit().putBoolean("bibleCompleted", true).commit();
                        pHandler.showNotificationDialog(contextWrapper.getString(R.string.plugin_installed), mContext.getString(R.string.plugin_installed_hint), 1);
                        DatabaseReference refVersion = FirebaseDatabase.getInstance()
                                .getReference().child("data")
                                .child("plg_vrs");

                        refVersion.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int version = snapshot.exists() ? snapshot.getValue(Integer.class) : pHandler.getVersionCode();
                                appPrefs.edit().putInt("plg_vrs", version).apply();
                                Log.v(TAG, "Plugin data saved!");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.v(TAG, "Database error: " + error.getMessage());
                            }
                        });
                    }

                    Log.v(TAG, name + " COMPLETED!");

                    //	Toast.makeText(MainActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();

                })
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    proBar.setProgress((int) progress);
                    tvBar.setText((int) progress + "%");
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.v(TAG, exception.getMessage());
                    alertProgressbar.dismiss();
                    //pHandler.showToast();
                    Log.e(TAG, "Updating resources failed");

                });

    }

    public void downloadPlugin(File folder,String url, SharedPreferences appPrefs){
        new CustomDownload(folder,bibleFName, appPrefs).execute(url);
    }

    public class CustomDownload extends AsyncTask<String, String, File> {

        String fileName;
        long finalFileSize = 0;
        final AlertDialog alertProgressbar;
        View pBarView;
        TextView tvBar;
        final ProgressBar proBar;
        final SharedPreferences appPrefs;
        File folder;


        public CustomDownload(File folder, String fileName, SharedPreferences appPrefs) {
            alertProgressbar = new AlertDialog.Builder(mContext).create();
            pBarView = View.inflate(mContext, R.layout.download_dialog, null);
            tvBar = pBarView.findViewById(R.id.pbartext);
            proBar = pBarView.findViewById(R.id.progressBar);
            proBar.setMax(100);
            Log.v(TAG, "--- "+folder.getAbsolutePath());

            alertProgressbar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertProgressbar.setView(pBarView);

            this.fileName = fileName;
            this.appPrefs = appPrefs;
            this.folder = folder;

            //File file = new File(f, fileName);
            /*if (this.folder!=null && this.folder.exists()) folder.delete();
            Log.v(TAG, "--- "+folder.getAbsolutePath());*/
        }

        /**
         * Before starting background thread Show Progress Bar Dialog
         */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!alertProgressbar.isShowing()) {
                alertProgressbar.show();
            }
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected File doInBackground(String... f_url) {
            int count;
            File file = null;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                finalFileSize = connection.getContentLength();

                Log.v(TAG, "Content length: "+finalFileSize);

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);



                Log.v(TAG, "DB: "+folder.getAbsolutePath());
                // Output stream
                OutputStream output = new FileOutputStream(folder.getAbsolutePath()+"/"+fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                file = new File(folder, "/"+fileName);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error: ", f_url[0]);
            }

            return file;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            proBar.setProgress(Integer.parseInt(progress[0]));
            tvBar.setText(progress[0]+ "%");
        }


        @Override
        protected void onPostExecute(File resultFile) {

            alertProgressbar.dismiss();


            if (resultFile!=null) Log.v(TAG, "Plugin length "+resultFile.length());
            boolean isFileDownloaded = resultFile != null && resultFile.exists() && finalFileSize == resultFile.length();

            if (isFileDownloaded) {
                //Downloaded
                if (fileName.equals(bibleFName)) {
                    alertProgressbar.dismiss();
                    boolean lastComplete = appPrefs.getBoolean("bibleCompleted", false);
                    //String lastData =  appPrefs.getString("bibleData", "");
                    if (!lastComplete){
                        appPrefs.edit().putString("bibleData", "Downloaded").commit();
                        appPrefs.edit().putBoolean("bibleCompleted", true).commit();
                        pHandler.showNotificationDialog(contextWrapper.getString(R.string.plugin_installed), mContext.getString(R.string.plugin_installed_hint), 1);
                    }else{
                        pHandler.showToast(mContext.getString(R.string.plugin_updated));
                    }
                    DatabaseReference refVersion = FirebaseDatabase.getInstance()
                            .getReference().child("data")
                            .child("plg_vrs");

                    refVersion.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int version = snapshot.exists() ? snapshot.getValue(Integer.class) : pHandler.getVersionCode();
                            appPrefs.edit().putInt("plg_vrs", version).apply();
                            Log.v(TAG, "Plugin data saved!");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.v(TAG, "Database error: " + error.getMessage());
                        }
                    });
                }
            } else {
                Log.e(TAG, "Updating resources failed");
                appPrefs.edit().remove("bibleData").commit();
                appPrefs.edit().remove("bibleCompleted").commit();
                pHandler.showNotificationDialog(mContext.getString(R.string.down_file), mContext.getString(R.string.down_failed_msg), 1);
            }
        }

    }
}
