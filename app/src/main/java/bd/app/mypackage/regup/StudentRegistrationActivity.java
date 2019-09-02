package bd.app.mypackage.regup;

//Registration activity. User puts required information for registration.

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentRegistrationActivity extends AppCompatActivity {

    private EditText mail, pass, contact;
    private Spinner sp;
    private CheckBox checkBox;
    private Button signup;
    private TextInputLayout etpass;

    private String email, password, phone, blood_group;
    public ArrayList<String> infolist = new ArrayList<>();

    private String[] type = {"SELECT BLOOD GROUP  - - -", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        mail = (EditText) findViewById(R.id.mail);
        pass = (EditText) findViewById(R.id.password);
        contact = (EditText) findViewById(R.id.contact);
        sp = (Spinner) findViewById(R.id.spinner_blood);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        etpass = (TextInputLayout) findViewById(R.id.etPasswordLayout);
        signup = (Button) findViewById(R.id.signup);

        //getting student information from StudentVerificationActivity
        //StudentVerificationActivity ob = new StudentVerificationActivity();
        infolist = StudentVerificationActivity.info;
        checkBox.setChecked(true);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    contact.setEnabled(true);
                    sp.setEnabled(true);
                }
                else
                {
                    contact.setEnabled(false);
                    sp.setEnabled(false);
                }
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
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


        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,type){

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Disable the first item of Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position == 0)
                {
                    //Set the disabled item text color
                    tv.setTextColor(Color.GRAY);
                }
                else
                {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };

        ar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(ar);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(StudentRegistrationActivity.this, R.anim.blink_anim);
                signup.startAnimation(animation);

                email = mail.getText().toString();
                password = pass.getText().toString();

                if(checkBox.isChecked())
                {
                    phone = contact.getText().toString();
                    blood_group = sp.getSelectedItem().toString();

                    if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone) || TextUtils.equals(blood_group,"SELECT BLOOD GROUP  - - -"))
                    {
                        Toast.makeText(getApplicationContext(), "Fill up missing information.", Toast.LENGTH_SHORT).show();
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
                                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if(task.isSuccessful())
                                                    {
                                                        //save student data to firestore
                                                        final Map<String, Object> studentdata = new HashMap<>();
                                                        studentdata.put("name",infolist.get(4));

                                                        final Map<String, Object> dummydata = new HashMap<>();
                                                        dummydata.put("dummy","data");
                                                        //adding dummy data for every document.
                                                        //Unless database will send no data on fetching.

                                                        firestore.collection("StudentData").document(infolist.get(0))
                                                                .set(dummydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    firestore.collection("StudentData").document(infolist.get(0))
                                                                            .collection("intake").document(infolist.get(1))
                                                                            .set(dummydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful())
                                                                            {
                                                                                firestore.collection("StudentData").document(infolist.get(0))
                                                                                        .collection("intake").document(infolist.get(1))
                                                                                        .collection("section").document(infolist.get(2))
                                                                                        .set(dummydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            firestore.collection("StudentData").document(infolist.get(0))
                                                                                                    .collection("intake").document(infolist.get(1))
                                                                                                    .collection("section").document(infolist.get(2))
                                                                                                    .collection("id").document(infolist.get(3))
                                                                                                    .set(studentdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    if(task.isSuccessful())
                                                                                                    {
                                                                                                        //save blood data to firestore
                                                                                                        firestore.collection("BloodVault").document("bloodtype")
                                                                                                                .collection(blood_group).document(infolist.get(4)+ "_" +phone)
                                                                                                                .set(dummydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                if(task.isSuccessful())
                                                                                                                {
                                                                                                                    //get userID and save user data
                                                                                                                    final Map<String, Object> userdata = new HashMap<>();
                                                                                                                    userdata.put("name",infolist.get(4));
                                                                                                                    userdata.put("id",infolist.get(3));
                                                                                                                    userdata.put("dept",infolist.get(0));
                                                                                                                    userdata.put("intake",infolist.get(1));
                                                                                                                    userdata.put("section",infolist.get(2));
                                                                                                                    userdata.put("mail",email);
                                                                                                                    userdata.put("type","student");

                                                                                                                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                                                                                                                    String userid = user.getUid();

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
                                                                                                                                            Intent intent = new Intent(StudentRegistrationActivity.this, NotifyAboutVerificationActivity.class);
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

                else if(!checkBox.isChecked())
                {
                    if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                    {
                        Toast.makeText(getApplicationContext(), "Fill up missing information.", Toast.LENGTH_SHORT).show();
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
                                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if(task.isSuccessful())
                                                    {
                                                        //save student data to firestore
                                                        final Map<String, Object> studentdata = new HashMap<>();
                                                        studentdata.put("name",infolist.get(4));

                                                        final Map<String, Object> dummydata = new HashMap<>();
                                                        dummydata.put("dummy","data");
                                                        //adding dummy data for every document.
                                                        //Unless database will send no data on fetching.

                                                        firestore.collection("StudentData").document(infolist.get(0))
                                                                .set(dummydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    firestore.collection("StudentData").document(infolist.get(0))
                                                                            .collection("intake").document(infolist.get(1))
                                                                            .set(dummydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful())
                                                                            {
                                                                                firestore.collection("StudentData").document(infolist.get(0))
                                                                                        .collection("intake").document(infolist.get(1))
                                                                                        .collection("section").document(infolist.get(2))
                                                                                        .set(dummydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            firestore.collection("StudentData").document(infolist.get(0))
                                                                                                    .collection("intake").document(infolist.get(1))
                                                                                                    .collection("section").document(infolist.get(2))
                                                                                                    .collection("id").document(infolist.get(3))
                                                                                                    .set(studentdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    if(task.isSuccessful())
                                                                                                    {
                                                                                                        //get userID and save user data
                                                                                                        final Map<String, Object> userdata = new HashMap<>();
                                                                                                        userdata.put("name",infolist.get(4));
                                                                                                        userdata.put("id",infolist.get(3));
                                                                                                        userdata.put("dept",infolist.get(0));
                                                                                                        userdata.put("intake",infolist.get(1));
                                                                                                        userdata.put("section",infolist.get(2));
                                                                                                        userdata.put("mail",email);
                                                                                                        userdata.put("type","student");

                                                                                                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                                                                                                        String userid = user.getUid();

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
                                                                                                                                Intent intent = new Intent(StudentRegistrationActivity.this, NotifyAboutVerificationActivity.class);
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
            }
        });
    }


    public void onBackPressed(){
        //
        Intent intent = new Intent(StudentRegistrationActivity.this, StudentVerificationActivity.class);
        startActivity(intent);
        this.finish();
    }
}
