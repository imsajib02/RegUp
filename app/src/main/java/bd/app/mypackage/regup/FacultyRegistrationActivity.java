package bd.app.mypackage.regup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FacultyRegistrationActivity extends AppCompatActivity {

    EditText mail, password, contact;
    Button registration;
    private TextInputLayout etpass;

    String short_code, codeword, name, dept, post;
    String email, pass_word, con_tact;

    LinkedList<String> daylist;
    LinkedList<String> evelist;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_regiatration);

        mail = (EditText) findViewById(R.id.mail);
        password = (EditText) findViewById(R.id.password);
        etpass = (TextInputLayout) findViewById(R.id.etPasswordLayout);
        contact = (EditText) findViewById(R.id.contact);
        registration = (Button) findViewById(R.id.registration);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        short_code = FacultyVerificationActivity.sh_code;
        codeword = FacultyVerificationActivity.codeword;
        name = FacultyVerificationActivity.name;
        dept = FacultyVerificationActivity.dept;
        post = FacultyVerificationActivity.post;
        daylist = new LinkedList<>(Arrays.asList(FacultyVerificationActivity.day));
        evelist = new LinkedList<>(Arrays.asList(FacultyVerificationActivity.eve));

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 8){
                    etpass.setError("Password must contain 8 characters.");
                }
                else if(s.length() >= 8){
                    etpass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(FacultyRegistrationActivity.this, R.anim.blink_anim);
                registration.startAnimation(animation);

                email = mail.getText().toString();
                pass_word = password.getText().toString();
                con_tact = contact.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass_word) || TextUtils.isEmpty(con_tact))
                {
                    Toast.makeText(getApplicationContext(),"Fill up missing information.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setMessage("Please wait..");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);

                    //check if email is already used
                    firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            boolean check = !task.getResult().getSignInMethods().isEmpty();

                            if(check)
                            {
                                //email already used
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Email is already registered.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //register user
                                firebaseAuth.createUserWithEmailAndPassword(email, pass_word).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful())
                                        {
                                            //save faculty data to firestore
                                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                                            final String userid = user.getUid();

                                            final Map<String, Object> facultydata = new HashMap<>();
                                            facultydata.put("registered",true);
                                            facultydata.put("disabled",false);
                                            facultydata.put("contact",con_tact);
                                            facultydata.put("userid",userid);
                                            facultydata.put("mail",email);

                                            firestore.collection("Faculty").document(short_code)
                                                    .update(facultydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful())
                                                    {
                                                        //get userID and save user data
                                                        final Map<String, Object> userdata = new HashMap<>();
                                                        userdata.put("name",name);
                                                        userdata.put("dept",dept);
                                                        userdata.put("post",post);
                                                        userdata.put("shortcode",short_code);
                                                        userdata.put("codeword",codeword);
                                                        userdata.put("mail",email);
                                                        userdata.put("contact",con_tact);
                                                        userdata.put("day",daylist);
                                                        userdata.put("eve",evelist);
                                                        userdata.put("type","faculty");
                                                        userdata.put("disabled",false);

                                                        firestore.collection("User").document(userid)
                                                                .set(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful())
                                                                            {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(getApplicationContext(),"Registration complete!",Toast.LENGTH_SHORT).show();

                                                                                //send student to homepage
                                                                                Intent intent = new Intent(FacultyRegistrationActivity.this, NotifyAboutVerificationActivity.class);
                                                                                Bundle b = new Bundle();
                                                                                b.putInt("value", 0);
                                                                                intent.putExtras(b);
                                                                                startActivity(intent);
                                                                                finish();
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
                                                                    Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
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
                                            Toast.makeText(getApplicationContext(),"Error! Please try again!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }

    public void onBackPressed(){
        //
        Intent intent = new Intent(FacultyRegistrationActivity.this, FacultyVerificationActivity.class);
        startActivity(intent);
        this.finish();
    }
}
