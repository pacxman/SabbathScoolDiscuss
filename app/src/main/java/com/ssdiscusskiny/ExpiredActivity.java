package com.ssdiscusskiny;

import android.os.Bundle;
import android.os.Build;
import android.widget.TextView;
import android.content.Intent;

import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ExpiredActivity extends AppCompatActivity {
    private TextView msgTv;
	private FirebaseDatabase db;
    private DatabaseReference refmsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expired);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}

        msgTv = (TextView)findViewById(R.id.msgexp);
        //msg.setText(getString(R.string.exp_msg)+" "+Variables.dwnLink);
        db = FirebaseDatabase.getInstance();
        refmsg = db.getReference().child("admin").child("state_msg");
        refmsg.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(DataSnapshot dataSnashot) {
                    String msg = dataSnashot.getValue(String.class);
                    if (msg != null && !msg.equals("state_msg")) {
                        msgTv.setText(msg);
                    } else {
                        msgTv.setText("Application failed to get Access and no further information was provided, please contact support team " + getString(R.string.m_email));
                    }
                }

                @Override
                public void onCancelled(DatabaseError p1) {
                }


            });
        final DatabaseReference stateRef = db.getReference().child("admin").child("access");
        stateRef.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(DataSnapshot dSanp) {
                    String access = dSanp.getValue(String.class);
                    if (access.equals("on")) {
                        Intent i = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError dE) {
                }


            });


    }

    @Override
    protected void onResume() {
        // TODO: Implement this method
        super.onResume();

    }

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		super.onBackPressed();
	}



}
