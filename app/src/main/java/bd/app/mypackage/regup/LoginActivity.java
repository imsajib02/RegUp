package bd.app.mypackage.regup;

//login activity

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private Button login;
    private TextView forgot_pass, registration;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    final int REQUEST_ALL = 1;
    boolean CALL_PERMISSION_DENIED, STORAGE_PERMISSION_DENIED;

    private String mail;
    private String password;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        forgot_pass = (TextView) findViewById(R.id.forgot_pass);
        registration = (TextView) findViewById(R.id.registration);
        login = (Button) findViewById(R.id.login);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("general");

        requestpermission();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.blink_anim);
                login.startAnimation(animation);

                mail = email.getText().toString().trim();
                password = pass.getText().toString().trim();

                if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)){

                    Toast.makeText(getApplicationContext(),"Fill up missing information.",Toast.LENGTH_SHORT).show();
                    return;
                }

                else
                {
                    progressDialog.setMessage("Please wait..");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);

                    final String version_name = BuildConfig.VERSION_NAME;

                    firestore.collection("Admin").document("appinfo")
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot = task.getResult();

                                String version = documentSnapshot.getString("version");
                                final String link = documentSnapshot.getString("link");

                                if(!TextUtils.equals(version, version_name))
                                {
                                    //update app
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                                    builder.setMessage("Update app to latest version?")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(browserIntent);
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    //close app
                                                    finishAffinity();
                                                    System.exit(0);
                                                }
                                            })
                                            .show();

                                    Dialog dialog = builder.create();
                                    dialog.setCanceledOnTouchOutside(false);
                                }
                                else
                                {
                                    //latest version installed
                                    //login process

                                    firebaseAuth.signInWithEmailAndPassword(mail, password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    //if sign in is successful, user is sent to the corresponding activity
                                                    if(task.isSuccessful()){

                                                        //admin login
                                                        //TODO: Use your own admin email
                                                        if(TextUtils.equals(mail,"admin_mail@mail_address.com"))
                                                        {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(),"Logged in as ADMIN!",Toast.LENGTH_LONG).show();

                                                            flag = 1;
                                                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }


                                                        if (flag == 1)
                                                        {
                                                            //user is identified as admin
                                                        }
                                                        else
                                                        {
                                                            //user is identified as faculty or student
                                                            //check if the user mail is verified
                                                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                                                            final String userid = user.getUid();

                                                            if(user.isEmailVerified())
                                                            {
                                                                firestore.collection("User").document(userid)
                                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            DocumentSnapshot documentSnapshot1 = task.getResult();
                                                                            String type = documentSnapshot1.getString("type");

                                                                            if(TextUtils.equals(type, "student"))
                                                                            {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(getApplicationContext(),"Login successful!",Toast.LENGTH_LONG).show();

                                                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            }
                                                                            else if(TextUtils.equals(type, "faculty"))
                                                                            {
                                                                                boolean disabled = documentSnapshot1.getBoolean("disabled");

                                                                                if(disabled)
                                                                                {
                                                                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                firestore.collection("User").document(userid)
                                                                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if (task.isSuccessful())
                                                                                                        {
                                                                                                            progressDialog.dismiss();
                                                                                                            Toast.makeText(getApplicationContext(), "Account is being disabled.", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                        else
                                                                                                        {
                                                                                                            progressDialog.dismiss();
                                                                                                            Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                                progressDialog.dismiss();
                                                                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                                else
                                                                                {
                                                                                    progressDialog.dismiss();
                                                                                    Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();

                                                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                }
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            else
                                                            {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(),"Please verify your email.",Toast.LENGTH_LONG).show();
                                                            }
                                                        }

                                                    }

                                                    //if sign in is unsuccessful, user is notified
                                                    else
                                                    {

                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Connectivity problem. Please check your internet connection.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });


        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.blink_anim);
                forgot_pass.startAnimation(animation);

                //go to password recovery activity
                Intent intent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
                startActivity(intent);
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.blink_anim);
                registration.startAnimation(animation);

                //user is sent to the registration activity
                Intent intent = new Intent(LoginActivity.this, RegistrationUserTypeActivity.class);
                startActivity(intent);
            }
        });

    }


    public void onBackPressed(){
        //closing the application
        this.finishAffinity();
        System.exit(0);
    }

    /*public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }*/

    public void requestpermission()
    {
        try {
            if (ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ALL);
            } else {

                //
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_ALL)
        {
            if(grantResults.length > 0)
            {
                for(int i=0;i<permissions.length;i++)
                {
                    if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            Log.e("msg", "storage granted");
                            STORAGE_PERMISSION_DENIED = false;
                        }
                        else
                        {
                            STORAGE_PERMISSION_DENIED = true;
                        }
                    }

                    else if(permissions[i].equals(Manifest.permission.CALL_PHONE))
                    {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            Log.e("msg", "call granted");
                            CALL_PERMISSION_DENIED = false;
                        }
                        else
                        {
                            CALL_PERMISSION_DENIED = true;
                        }
                    }
                }

                showalertandrequest();
            }
        }
    }

    public void showalertandrequest()
    {
        if(CALL_PERMISSION_DENIED && STORAGE_PERMISSION_DENIED)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Without call and storage permission some of the features of this app won't work. Allow permission?")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            requestpermission();
                        }
                    })

                    .setNegativeButton("Cancel", null)
                    .show();

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

        }
        else if(CALL_PERMISSION_DENIED && !STORAGE_PERMISSION_DENIED)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Call permission is required to make phone calls to blood donors. Allow permission?")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this,
                                            new String[]{Manifest.permission.CALL_PHONE}, REQUEST_ALL);
                                } else {

                                    //
                                }
                            }
                            catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    })

                    .setNegativeButton("Cancel", null)
                    .show();

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
        }
        else if(!CALL_PERMISSION_DENIED && STORAGE_PERMISSION_DENIED)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Storage permission is required to store submitted course data. Allow permission?")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ALL);
                                } else {

                                    //
                                }
                            }
                            catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    })

                    .setNegativeButton("Cancel", null)
                    .show();

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
        }
        else
        {
            //both permission granted
        }
    }
}
