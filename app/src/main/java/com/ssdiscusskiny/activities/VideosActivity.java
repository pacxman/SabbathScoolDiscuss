package com.ssdiscusskiny.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssdiscusskiny.MainActivity;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.data.Video;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.utils.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class VideosActivity extends AppCompatActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener {

    private String TAG = getClass().getSimpleName();
    private YouTubePlayerView videoView;
    private String videoUrl;
    private int counter = 0, videoLength;

    private ImageButton playerBtn,nextBtn,prevBtn;
    private YouTubePlayer player;
    List<Video> videos = new ArrayList<>();
    private Toolbar toolbar;

    private RecyclerView recyclerView;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean playerWasPlaying = false;
    private VideoAdapter videoAdapter;

    private YouTubePlayerSupportFragmentX playerFragment;
    private SeekBar playerSeekbar;
    private int seekProgress;
    private ImageButton refreshBtn;
    private String vidExtras;
    private TextView errorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        //videoUrl = "https://www.youtube.com/watch?v=FaGQDZZiiPw&list=RDFaGQDZZiiPw&start_radio=1";
        videoView = findViewById(R.id.youtube_fragment);
        playerBtn = findViewById(R.id.play);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.video_list);
        nextBtn = findViewById(R.id.next);
        prevBtn = findViewById(R.id.previous);
        playerSeekbar = findViewById(R.id.player_seek);
        refreshBtn = findViewById(R.id.refresh);
        errorTv = findViewById(R.id.error);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer);

        toolbar.setNavigationOnClickListener(v->{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.closeDrawer(GravityCompat.START);
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                if(playerWasPlaying) if (player!=null) player.play();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                playerWasPlaying = player != null && player.isPlaying();
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        /*MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri=Uri.parse("rtsp://r2---sn-a5m7zu76.c.");
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();*/

        //videoView.start();
        //new YourAsyncTask().execute();

        playerFragment = (YouTubePlayerSupportFragmentX)getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);


        videoAdapter = new VideoAdapter(videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(videoAdapter);

        playerBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);

        loadVideos();

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean initiatedByUser) {
                seekProgress = seekBar.getProgress();
                if (initiatedByUser) Log.v("VIDEOS", "TRACKING.... "+seekProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ct.cancel();
                Log.v("VIDEOS", "TRACKING: "+seekProgress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v("VIDEOS", "NEW PROGRESS: "+seekProgress);

                int millisProgress = seekBar.getProgress();
                player.seekToMillis(millisProgress);

                ct.start();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle!=null){
            if (!bundle.getString("id", "").isEmpty()) {
                vidExtras = bundle.getString("id");
            }else{
                vidExtras = null;
            }
        }else{
            vidExtras = null;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.video, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ct.cancel();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.about:
                new PanelHandler(this)
                        .showNotificationDialog(getString(R.string.about), getString(R.string.about_videos), 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int btnId = view.getId();
        switch (btnId){
            case R.id.play:
                if (player!=null){
                    if (player.isPlaying()) player.pause();
                    else player.play();
                }
                break;
            case R.id.next:
                if (counter+1<videos.size())counter += 1;
                else counter = 0;
                if (player!=null) {
                    ct.cancel();
                    player.loadVideo(videos.get(counter).getId());
                }
                break;
            case R.id.previous:
                if (counter-1>=0)counter -= 1;
                else counter = 0;
                if (player!=null) {
                    ct.cancel();
                    player.loadVideo(videos.get(counter).getId());
                }
                break;
            case R.id.refresh:
                loadVideos();
                break;

        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        player = youTubePlayer;
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

        if (vidExtras==null){
            if (videos.size()>=1){
                youTubePlayer.loadVideo(videos.get(0).getId());
                youTubePlayer.play();
            }else{
                //No video available
                new PanelHandler(VideosActivity.this)
                        .showNotificationDialog(getString(R.string.video),
                                getString(R.string.empty_video_list),
                                1);
            }

        }else{
            youTubePlayer.loadVideo(vidExtras);
            youTubePlayer.play();
            SharedPreferences preferences = getSharedPreferences("videos", MODE_PRIVATE);
            preferences.edit().putString(vidExtras, "watched").apply();
        }

        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                playerBtn.setImageResource(R.drawable.ic_pause);
                if (videoLength==0){
                    Log.v("VIDEOS", "Live streaming!");
                    toolbar.setTitle(getString(R.string.live));
                    final int current = youTubePlayer.getCurrentTimeMillis();
                    if (playerSeekbar.getMax()==0) {
                        playerSeekbar.setMax(current);
                    }

                }
                ct.start();
                //Log.d("VIDEOS", "SEEK TO "+(player!=null ? player.getCurrentTimeMillis() : 0));
            }

            @Override
            public void onPaused() {
                playerBtn.setImageResource(R.drawable.ic_play);
                ct.cancel();
            }

            @Override
            public void onStopped() {
                playerBtn.setImageResource(R.drawable.ic_play);
                ct.cancel();
            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {
                Log.d("VIDEOS", "SEEK TO "+i);
            }
        });

        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {
            }

            @Override
            public void onVideoStarted() {
                Log.v("VIDEOS", "MLS: "+youTubePlayer.getDurationMillis());
                //final int videoDuration = youTubePlayer.getDurationMillis();
                videoLength = youTubePlayer.getDurationMillis();
                if (videoLength!=0){
                    playerSeekbar.setMax(youTubePlayer.getDurationMillis());
                    Log.v("VIDEOS", "Not a live streaming!");
                    Log.v("VIDEOS", "DURATION "+videoLength);
                    toolbar.setTitle("");
                    playerSeekbar.setProgress(0);
                }


            }

            @Override
            public void onVideoEnded() {
                playerSeekbar.setProgress(0);
                ct.cancel();
                if (counter+1<videos.size()){
                    counter += 1;
                    youTubePlayer.loadVideo(videos.get(counter).getId());
                }
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.e("VIDEO", youTubeInitializationResult.toString());
        videoView.setVisibility(View.GONE);
        //If there was an extra mark it as watched
        if (vidExtras!=null){
            SharedPreferences preferences = getSharedPreferences("videos", MODE_PRIVATE);
            preferences.edit().putString(vidExtras, "watched").apply();
            Log.v("VIDEO", "Extra auto marked!");
        }
        //new PanelHandler(VideosActivity.this).showNotificationDialog(getString(R.string.video), getString(R.string.video_error_msg), 1);

        errorTv.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (errorTv.getVisibility()==View.VISIBLE) super.onBackPressed();
        else new PanelHandler(this).showExit();
    }

    /*private class YourAsyncTask extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(VideosActivity.this, "", "Loading Video wait...", true);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                String url = "http://www.youtube.com/watch?v=1FJHYqE0RDg";
                videoUrl = getUrlVideoRTSP(url);
                Log.e("Video", videoUrl);
            }
            catch (Exception e)
            {
                Log.e("Login So", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            progressDialog.dismiss();
            videoView.setVideoURI(Uri.parse(videoUrl));
            MediaController mc = new MediaController(VideosActivity.this);
            videoView.setMediaController(mc);
            videoView.requestFocus();
            videoView.start();
            mc.show();
        }

    }

    public static String getUrlVideoRTSP(String urlYoutube)
    {
        try
        {
            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String id = extractYoutubeId(urlYoutube);
            URL url = new URL(gdy + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Document doc = documentBuilder.parse(connection.getInputStream());
            Element el = doc.getDocumentElement();
            NodeList list = el.getElementsByTagName("media:content");///media:content
            String cursor = urlYoutube;
            for (int i = 0; i < list.getLength(); i++)
            {
                Node node = list.item(i);
                if (node != null)
                {
                    NamedNodeMap nodeMap = node.getAttributes();
                    HashMap<String, String> maps = new HashMap<String, String>();
                    for (int j = 0; j < nodeMap.getLength(); j++)
                    {
                        Attr att = (Attr) nodeMap.item(j);
                        maps.put(att.getName(), att.getValue());
                    }
                    if (maps.containsKey("yt:format"))
                    {
                        String f = maps.get("yt:format");
                        if (maps.containsKey("url"))
                        {
                            cursor = maps.get("url");
                        }
                        if (f.equals("1"))
                            return cursor;
                    }
                }
            }
            return cursor;
        }
        catch (Exception ex)
        {
            Log.e("VIDEO", ex.toString());
        }
        return urlYoutube;

    }

    protected static String extractYoutubeId(String url) throws MalformedURLException
    {
        String id = null;
        try
        {
            String query = new URL(url).getQuery();
            if (query != null)
            {
                String[] param = query.split("&");
                for (String row : param)
                {
                    String[] param1 = row.split("=");
                    if (param1[0].equals("v"))
                    {
                        id = param1[1];
                    }
                }
            }
            else
            {
                if (url.contains("embed"))
                {
                    id = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("Exception", ex.toString());
        }
        return id;
    }*/

    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

        List<Video> dataList;

        public VideoAdapter(List<Video> videoList){
            dataList = videoList;
            Log.v("VIDEO", "VIDEOS "+videoList.size());
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View listItem = inflater.inflate(R.layout.video_list_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Video video = dataList.get(position);
            new TitleHandler(video.getId(), holder.titleTv, 0).execute();

            holder.thumbnailView.initialize(getMetas(), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(video.getId());
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                        @Override
                        public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                            youTubeThumbnailLoader.release();
                        }

                        @Override
                        public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        }
                    });
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    Log.e("VIDEO", youTubeInitializationResult.toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            public YouTubeThumbnailView thumbnailView;
            public TextView titleTv;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                thumbnailView = itemView.findViewById(R.id.thumbnail);
                titleTv = itemView.findViewById(R.id.title);

                itemView.setOnClickListener(view -> {
                    Log.v("VIDEOS", "ITEM: "+getAdapterPosition());
                    drawerLayout.closeDrawer(GravityCompat.START);
                    playerSeekbar.setMax(0);
                    videoLength = 0;
                    final String vidId = dataList.get(getAdapterPosition()).getId();
                    ct.cancel();
                    counter = getAdapterPosition();
                    player.loadVideo(vidId);
                });
            }
        }

    }

    private void loadVideos(){
        videos.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference()
                .child("other")
                .child("videos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot s : snapshot.getChildren()){
                                String id = s.getKey();
                                if (id.length()==11){
                                    Long status = s.child("status").getValue(Long.class);
                                    if (status!=null&&status==1) videos.add(new Video(id));
                                    else if (status==null) videos.add(new Video(id));
                                }

                            }
                            if (videos.size()>0) videoAdapter.notifyDataSetChanged();
                            playerFragment.initialize(getMetas(), VideosActivity.this);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    class TitleHandler extends AsyncTask<String, String, String> {

        String id;
        TextView tvTitle;
        int caller;

        public TitleHandler(String id, @Nullable TextView tvTitle, int caller) {
            this.id = id;
            this.tvTitle = tvTitle;
            this.caller = caller;
        }

        @Override
        protected String doInBackground(String... voids) {
            Document document = null;
            try {
                document = Jsoup.connect("https://www.youtube.com/watch?v="+id).get();
            } catch (Exception e) {
                return null;
            }
            return document.title().replaceAll(" - YouTube$", "");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (tvTitle!=null) tvTitle.setText(R.string.fetching_title);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null){
                Log.v("VIDEO", s);
                if (caller==0) if (tvTitle!=null) tvTitle.setText(s);
                else if (caller==1) toolbar.setTitle(s);
            }else{
                //new PanelHandler(VideosActivity.this).showNotificationDialog(getString(R.string.loading_video), getString(R.string.vid_load_error), 1);
                if (tvTitle!=null) tvTitle.setText(R.string.unknown);
            }

        }
    }

    CountDownTimer ct = new CountDownTimer(1000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            Log.v("VIDEOS", "LENGTH: "+videoLength);
            if (videoLength!=0){
                if (player.isPlaying()){
                    final int current = player.getCurrentTimeMillis();

                    //final int videoProgressedPerCent = (current*100)/videoLength;
                    if (current<=videoLength){

                        playerSeekbar.setProgress(current);
                        Log.v("VIDEOS", "SEEKBAR TO "+current);
                        start();
                    }
                }else{
                    cancel();
                }
            }else{
                if (player.isPlaying()){

                    int current = player.getCurrentTimeMillis();

                    Log.v("VIDEOS", "CURRENT "+current);

                    if (current>playerSeekbar.getMax()) playerSeekbar.setMax(current);

                    if (current<=playerSeekbar.getMax()){
                        playerSeekbar.setProgress(current);
                    }else if (current>playerSeekbar.getMax()){
                        playerSeekbar.setMax(current);
                        playerSeekbar.setProgress(current);
                    }

                    start();
                }else{
                    cancel();
                }
            }

        }
    };


    private String getMetas(){

        try {
            MasterKey masterKey = new MasterKey.Builder(VideosActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    VideosActivity.this,
                    "account_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String m = sharedPreferences.getString("api", "no_value_applied");
            Log.v(TAG, "API "+m);
            return m;
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return "no_value_applied";
    }

}