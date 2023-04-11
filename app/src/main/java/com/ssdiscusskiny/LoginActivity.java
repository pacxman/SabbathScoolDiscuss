package com.ssdiscusskiny;

import android.Manifest;
import android.accounts.Account;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Pattern;

import android.accounts.AccountManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.utils.Parser;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;


public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private TextView login_title, later_tv_click, tvbar;
    private LinearLayout skip_layout, login_card;
    private Button confirmBtn;
    private SharedPreferences sharedPreference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference refCreds;
    private PanelHandler panelHandle;
    private AlertDialog alertProgressBar;
    private TextInputLayout emailLayout, nameLayout;
    private TextInputEditText emailInput, usernameInput;
    private String accountId = "";
    private String accountName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        }

        emailInput = findViewById(R.id.email);
        usernameInput = findViewById(R.id.username);

        emailLayout = findViewById(R.id.email_area);
        nameLayout = findViewById(R.id.username_area);

        login_title = findViewById(R.id.login_text);
        skip_layout = findViewById(R.id.skip_username);
        login_card = findViewById(R.id.login_card);

        confirmBtn = findViewById(R.id.login_button);
        later_tv_click = findViewById(R.id.later);

        alertProgressBar = new AlertDialog.Builder(this).create();
        View pBarView = View.inflate(this, R.layout.pbar_alertdialog, null);
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);
        tvbar = pBarView.findViewById(R.id.pbartext);
        alertProgressBar.setCancelable(false);

        try {
            MasterKey masterKey = new MasterKey.Builder(LoginActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreference = EncryptedSharedPreferences.create(
                    LoginActivity.this,
                    "account_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }
        getAccount();

        firebaseDatabase = FirebaseDatabase.getInstance();

        refCreds = firebaseDatabase.getReference().child("registry").child("users").child("creds");

        panelHandle = new PanelHandler(this);

        Animation field_name_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.field_name_anim);

        login_title.startAnimation(field_name_anim);

        Animation center_reveal_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_reveal_anim);
        login_card.startAnimation(center_reveal_anim);

        Animation new_user_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_top);
        skip_layout.startAnimation(new_user_anim);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("change_name")) {
                login_title.setText(getString(R.string.change_username));
                String oldName = extras.getString("change_name");
                changeUsername(confirmBtn, oldName);
            } else if (extras.containsKey("login")) {
                if (extras.getString("login").equals("setEmail")) {
                    checkAccountPermission();
                    setEmail(confirmBtn);
                } else if (extras.getString("login").equals("setName")) {
                    createUsername(confirmBtn);
                }
            }
        }


    }

    /*protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/


    /*private void requestAccount() {
        Intent intent =
                AccountPicker.newChooseAccountIntent(
                        new AccountPicker.AccountChooserOptions.Builder()
                                .setAllowableAccountsTypes(Arrays.asList("com.google"))
                                .build());


        //startActivityForResult(intent, 100);
        //Use new way of calling activity with expectations
        activityResultLauncher.launch(intent);

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String possibleEmail = null;

            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(this).getAccounts();

            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    possibleEmail = account.name;
                    break;
                }
            }

            Log.e("TAGII", "possibleEmail: " + possibleEmail);

            possibleEmail = possibleEmail != null ? possibleEmail : "";

            emailInput.setText(possibleEmail);
        }
    }

    private void setEmail(Button button) {
        emailInput.setVisibility(View.VISIBLE);

        nameLayout.setVisibility(View.GONE);
        skip_layout.setVisibility(View.VISIBLE);

        button.setText("Continue");

        button.setOnClickListener(v -> {
            // TODO: Implement this method
            hideKeyboardFrom(LoginActivity.this, emailInput);
            new AccountCreator().execute();

        });


    }

    private void checkAccountPermission() {

        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            new AlertDialog.Builder(this)
                    .setNeutralButton(getString(R.string.dismiss), null)
                    .setPositiveButton(getString(R.string.continue_hint), ((dialogInterface, i) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1)))
                    .setTitle(R.string.login)
                    .setMessage(R.string.account_permission_request)
                    .setCancelable(false)
                    .show();
        } else {
            String possibleEmail = null;

            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(this).getAccounts();

            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    possibleEmail = account.name;
                    //Log.e("TAGGI","possibleEmail"+possibleEmail);
                }
            }

            Log.e("TAGII", "possibleEmail: " + possibleEmail);

            possibleEmail = possibleEmail != null ? possibleEmail : "";

            emailInput.setText(possibleEmail);
        }


    }

    private void createUsername(Button loginButton) {

        emailLayout.setVisibility(View.GONE);

        nameLayout.setVisibility(View.VISIBLE);
        skip_layout.setVisibility(View.VISIBLE);

        confirmBtn.setText(R.string.set_username);
        later_tv_click.setText(R.string.later_hint_tv);

        later_tv_click.setOnClickListener(v -> {
            // TODO: Implement this method

            sharedPreference.edit().putString("username", "[noUsername]").commit();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });


        loginButton.setOnClickListener(v -> {
            // TODO: Implement this method
            hideKeyboardFrom(LoginActivity.this, usernameInput);
            new UsernameCreator().execute();
        });
    }

    private void getAccount() {
        accountId = sharedPreference.getString("account_id", "");
        accountName = sharedPreference.getString("account_name", "");
    }

    private void hideKeyboardFrom(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private boolean emailValidator(String email) {
        EmailValidator validator = EmailValidator.getInstance();

        if (validator.isValid(email)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean phoneValidator(String s) {
        String patterns = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
        Pattern p = Pattern.compile(patterns);
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    class UsernameCreator extends AsyncTask<Void, Void, Boolean> {


        public UsernameCreator() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!alertProgressBar.isShowing()) {
                alertProgressBar.setCancelable(false);
                alertProgressBar.show();
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {


            if (!result) {
                if (alertProgressBar.isShowing()) {
                    alertProgressBar.dismiss();
                }
                panelHandle.showNetError(1, getString(R.string.no_internet_hint))
                        .setOnClickListener(v -> {
                            if (panelHandle.mAlertDialog != null) {
                                panelHandle.mAlertDialog.dismiss();
                            }
                            new UsernameCreator().execute();
                        });
            } else {
                final String name = usernameInput.getText().toString();

                if (StringUtils.length(name) >= 4 && !StringUtils.isNumeric(name) && !StringUtils.isWhitespace(name) && !name.contains(".")) {
                    tvbar.setText(R.string.checking_name);

                    if (!accountId.isEmpty()) {
                        final Query userQuery = FirebaseDatabase.getInstance().getReference()
                                .child("registry")
                                .child("users")
                                .child("creds")
                                .orderByChild("username")
                                .equalTo(name);
                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeEventListener(this);
                                Map<String, Object> account = (Map<String, Object>) dataSnapshot.getValue();
                                if (account != null) {
                                    String account_id = account.keySet().iterator().next();
                                    if (account_id.equals(accountId)) {
                                        sharedPreference.edit().putString("account_name", name).commit();
                                        //Close progress dialog
                                        alertProgressBar.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                        finish();
                                    } else {
                                        alertProgressBar.dismiss();
                                        panelHandle.showNotificationDialog(getString(R.string.username), getString(R.string.name_exist_hint), 1);
                                    }
                                } else {
                                    refCreds.child(accountId).child("username").setValue(name, (dE, dR) -> {
                                        // TODO: Implement this method
                                        sharedPreference.edit().putString("account_name", name).commit();

                                        alertProgressBar.dismiss();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                        finish();
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        alertProgressBar.dismiss();
                        setEmail(confirmBtn);
                    }


                } else {
                    alertProgressBar.dismiss();
                    usernameInput.setError(getString(R.string.blank_name));
                }
            }
        }
    }

    class AccountCreator extends AsyncTask<Void, Void, Boolean> {


        public AccountCreator() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setConnectTimeout(3000);
                httpConnection.connect();
                if (httpConnection.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!alertProgressBar.isShowing()) {
                alertProgressBar.setCancelable(false);
                alertProgressBar.show();
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {


            if (!result) {
                if (alertProgressBar.isShowing()) {
                    alertProgressBar.dismiss();
                }
                panelHandle.showNetError(1, getString(R.string.no_internet))
                        .setOnClickListener(v -> {
                            if (panelHandle.mAlertDialog != null) {
                                panelHandle.mAlertDialog.dismiss();
                            }
                            new AccountCreator().execute();
                        });
            } else {
                final String accountTxt = emailInput.getText().toString();

                if (!accountTxt.isEmpty()) {
                    if (emailValidator(accountTxt) || phoneValidator(accountTxt)) {

                        accountId = accountTxt.replace(".", "*");

                        alertProgressBar.show();

                        refCreds.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                // TODO: Implement this method
                                if (snapshot.hasChild(accountId)) {

                                    refCreds.child(accountId).addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot snapshot2) {
                                            // TODO: Implement this method
                                            String username = snapshot2.child("username").getValue(String.class);

                                            username = (username != null) ? username : "[noUsername]";

                                            sharedPreference.edit().putString("account_id", accountId).commit();
                                            sharedPreference.edit().putString("account_name", username).commit();

                                            alertProgressBar.dismiss();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(0, 0);
                                            finish();

                                            snapshot2.getRef().removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError dbError) {
                                            // TODO: Implement this method
                                            Log.e(TAG, dbError.getMessage());
                                        }


                                    });

                                } else {
                                    if (!accountName.isEmpty()) {
                                        refCreds.child(accountId).child("username").setValue(accountName, (error, ref) -> {
                                            // TODO: Implement this method
                                            if (error == null) {
                                                sharedPreference.edit().putString("account_id", accountId).commit();

                                                alertProgressBar.dismiss();
//
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                overridePendingTransition(0, 0);
                                                finish();

                                            }
                                        });

                                    } else {
                                        final String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
                                        refCreds.child(accountId).child("joined").setValue(date, (error, ref) -> {
                                            // TODO: Implement this method
                                            if (error == null) {
                                                //Register joined date
                                                sharedPreference.edit().putString("account_id", accountId).commit();
                                                alertProgressBar.dismiss();
                                                createUsername(confirmBtn);
                                            }
                                        });

                                    }


                                }
                                snapshot.getRef().removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // TODO: Implement this method
                                Log.e(TAG, error.getMessage());
                            }


                        });
                    } else {
                        alertProgressBar.dismiss();
                        emailInput.setError(getString(R.string.invalid_email_hint));
                    }
                } else {
                    alertProgressBar.dismiss();
                    emailInput.setError(getString(R.string.invalid_email_hint));
                }
            }
        }
    }

    class RenamingTask extends AsyncTask<Void, Void, Boolean> {

        String oldName;

        public RenamingTask(String oldName) {
            this.oldName = oldName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!alertProgressBar.isShowing()) {
                alertProgressBar.setCancelable(false);
                alertProgressBar.show();
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (!result) {
                if (alertProgressBar.isShowing()) {
                    alertProgressBar.dismiss();
                }
                panelHandle.showNetError(1, getString(R.string.no_internet_hint))
                        .setOnClickListener(v -> {
                            if (panelHandle.mAlertDialog != null) {
                                panelHandle.mAlertDialog.dismiss();
                            }
                            new RenamingTask(oldName).execute();
                        });
            } else {
                final String name = usernameInput.getText().toString();
                if (StringUtils.length(name) >= 4 && !StringUtils.isNumeric(name) && !StringUtils.isWhitespace(name)) {
                    tvbar.setText(R.string.confirm_txt);
                    if (oldName.equals(name)) {
                        alertProgressBar.dismiss();
                        panelHandle.showNotificationDialog(getString(R.string.change_username), getString(R.string.same_name_msg), 1)
                                .setOnDismissListener((dialogInterface -> {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }));
                        return;
                    }

                    //Check if provided username already exists
                    final Query userQuery = FirebaseDatabase.getInstance().getReference()
                            .child("registry")
                            .child("users")
                            .child("creds")
                            .orderByChild("username")
                            .equalTo(name);
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeEventListener(this);
                            Object account = dataSnapshot.getValue();
                            if (account != null) {
                                alertProgressBar.dismiss();
                                panelHandle.showNotificationDialog(getString(R.string.username), getString(R.string.name_exist_hint), 1);
                            } else {
                                refCreds.child(accountId).child("username").setValue(name, ((error, ref) -> {
                                    if (error==null){
                                        tvbar.setText(R.string.sync_data);
                                        firebaseDatabase.getReference().child(Variables.content)
                                                .child("comments")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        snapshot.getRef().removeEventListener(this);
                                                        for (DataSnapshot s : snapshot.getChildren()) {
                                                            firebaseDatabase.getReference()
                                                                    .child(Variables.content)
                                                                    .child("comments")
                                                                    .child(s.getKey())
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            snapshot.getRef().removeEventListener(this);

                                                                            //Log.v(TAG, "KEY "+snapshot.getKey());
                                                                            String existName = snapshot.child("user").getValue(String.class);
                                                                            String senderName = snapshot.child("sender").getValue(String.class);
                                                                            //Log.v(TAG, "exist name "+existName);
                                                                            Log.v(TAG, "KEY "+snapshot.getKey()+" user:"+existName+" oname: "+oldName);
                                                                            if (existName!=null&&oldName.equals(existName)) s.child("user").getRef().setValue(name);
                                                                            if (senderName!=null&&senderName.equals(oldName)) s.child("sender").getRef().setValue(name);

                                                                            /*String comment = snapshot.getValue(String.class);
                                                                            if (comment != null) {
                                                                                String info = comment.substring(comment.indexOf("#") + 1);
                                                                                String oldId = info.substring(0, info.indexOf(Parser.regexDate(info)));

                                                                                if (oldId.equals(oldName + " ") || oldId.equals(oldName)) {
                                                                                    comment = comment.replace(oldName, name);
                                                                                    snapshot.getRef().setValue(comment);
                                                                                }
                                                                            }*/
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                        }
                                                        //close dialog and start mainActivity
                                                        sharedPreference.edit().putString("account_name", name).commit();
                                                        alertProgressBar.dismiss();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        overridePendingTransition(0, 0);
                                                        panelHandle.showToast(getString(R.string.change_name_completed));
                                                        finish();

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }else{
                                        panelHandle.showNotificationDialog(getString(R.string.change_username), getString(R.string.changing_name_error), 1);
                                    }



                                }));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    usernameInput.setError(getString(R.string.blank_name));
                }
            }
        }

    }

    private void changeUsername(Button button, final String oldName) {
        nameLayout.setHint(getString(R.string.new_name_hint));
        emailLayout.setVisibility(View.GONE);
        nameLayout.setVisibility(View.VISIBLE);
        button.setOnClickListener(v -> {
            // TODO: Implement this method
            hideKeyboardFrom(LoginActivity.this, emailInput);
            new RenamingTask(oldName).execute();

        });
    }

    /*ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {


                    if (result.getResultCode() == Activity.RESULT_OK) {

                        accountName = result.getData().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        // Do what you need with email
                        emailInput.setText(accountName);
                        emailInput.setSelection(emailInput.getText().length());
                    }
                }
            });*/

}
