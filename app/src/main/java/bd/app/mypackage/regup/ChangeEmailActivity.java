package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeEmailActivity extends AppCompatActivity implements LoginDialog.LoginDialogListener{

    EditText mail, pass;
    Button changemail;
    private TextInputLayout etpass1;

    String dialog_mail, dialog_pass, value = null;;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        mail = (EditText) findViewById(R.id.newmail);
        pass = (EditText) findViewById(R.id.pass);
        changemail = (Button) findViewById(R.id.change_mail);
        etpass1 = (TextInputLayout) findViewById(R.id.etPasswordLayout);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        try
        {
            value = getIntent().getExtras().getString("from");
        }
        catch (Exception e)
        {
            //
        }


        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 8){
                    etpass1.setError("Password must contain 8 characters.");
                }
                else if(s.length() >= 8){
                    etpass1.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        changemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(ChangeEmailActivity.this, R.anim.blink_anim);
                changemail.startAnimation(animation);

                final FirebaseUser user = firebaseAuth.getCurrentUser();
                final String usermail = user.getEmail();
                final String newmail = mail.getText().toString();
                final String password = pass.getText().toString();


                if(TextUtils.isEmpty(newmail) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(), "Fill up missing information.", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setMessage("You are about to change your RegUp email!")
                            .setCancelable(false)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    progressDialog.setMessage("Please wait..");
                                    progressDialog.show();
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.setCancelable(false);

                                    if (user != null)
                                    {
                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential(usermail,password);

                                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    user.updateEmail(newmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful())
                                                            {
                                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            progressDialog.dismiss();
                                                                            Intent intent = new Intent(ChangeEmailActivity.this, NotifyAboutVerificationActivity.class);
                                                                            Bundle b = new Bundle();
                                                                            b.putInt("value", 1);
                                                                            intent.putExtras(b);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(intent);
                                                                            finishAffinity();
                                                                        }
                                                                        else
                                                                        {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
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
                                                    Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }

                                    else if (user == null)
                                    {
                                        progressDialog.dismiss();
                                        opendialog();
                                    }
                                }
                            })

                            .setNegativeButton("Cancel", null)
                            .show();

                    Dialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);

                }
            }
        });
    }


    public void opendialog()
    {
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.show(getSupportFragmentManager(), "logindialog");
        loginDialog.setCancelable(false);
    }

    @Override
    public void applytexts(String email, String password) {

        dialog_mail = email;
        dialog_pass = password;

        if (TextUtils.isEmpty(dialog_mail) || TextUtils.isEmpty(dialog_pass))
        {
            opendialog();
        }
        else
        {
            firebaseAuth.signInWithEmailAndPassword(dialog_mail, dialog_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(), "You are logged in.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {

        if(TextUtils.equals(value, "home"))
        {
            Intent intent = new Intent(ChangeEmailActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }
        else
        {
            this.finish();
        }
    }
}
