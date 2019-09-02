package bd.app.mypackage.regup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FacultyVerificationActivity extends AppCompatActivity {

    EditText shortcode, passcode;
    Button verify;

    public static String sh_code, codeword, name, dept, post;
    public static String[] day;
    public static String[] eve;
    boolean registered;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_verification);

        shortcode = (EditText) findViewById(R.id.short_code);
        passcode = (EditText) findViewById(R.id.pass_code);
        verify = (Button) findViewById(R.id.verify);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(FacultyVerificationActivity.this, R.anim.blink_anim);
                verify.startAnimation(animation);

                sh_code = shortcode.getText().toString();
                final String passs_code = passcode.getText().toString();

                if(TextUtils.isEmpty(sh_code) || TextUtils.isEmpty(passs_code))
                {
                    Toast.makeText(getApplicationContext(),"Fill up missing information.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setMessage("Please wait..");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);

                    firestore.collection("Faculty").document(sh_code).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot = task.getResult();

                                try
                                {
                                    codeword = documentSnapshot.getString("codeword");
                                    registered = documentSnapshot.getBoolean("registered");

                                    if(!registered)
                                    {
                                        if(TextUtils.equals(passs_code, codeword))
                                        {
                                            name = documentSnapshot.getString("name");
                                            dept = documentSnapshot.getString("dept");
                                            post = documentSnapshot.getString("post");

                                            List<String> arr = (List<String>) documentSnapshot.get("day");
                                            day = arr.toArray(new String[arr.size()]);

                                            List<String> arr1 = (List<String>) documentSnapshot.get("eve");
                                            eve = arr1.toArray(new String[arr1.size()]);

                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Verified as " +name+ ".", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(FacultyVerificationActivity.this, FacultyRegistrationActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Invalid Short Code/Pass Code!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else if(registered)
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Faculty is already registered!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
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
        //goes to Registration User Type activity
        this.finish();
    }
}
