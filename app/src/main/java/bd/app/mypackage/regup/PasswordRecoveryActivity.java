package bd.app.mypackage.regup;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordRecoveryActivity extends AppCompatActivity {

    EditText email;
    Button recover;
    TextView showmsg;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        email = (EditText) findViewById(R.id.email);
        recover = (Button) findViewById(R.id.recover);
        showmsg = (TextView) findViewById(R.id.show_msg);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();


        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(PasswordRecoveryActivity.this, R.anim.blink_anim);
                recover.startAnimation(animation);

                final String mail = email.getText().toString();

                if(TextUtils.isEmpty(mail))
                {
                    Toast.makeText(getApplicationContext(), "Enter your RegUp email address.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setMessage("Please wait..");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);

                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                showmsg.setText("A mail with a password reset link has been sent to " +mail+ ". " +
                                        "Please check your email.");
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void onBackPressed(){
        //goes to Login activity
        this.finish();
    }
}
