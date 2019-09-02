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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CheckPasscodeValidityActivity extends AppCompatActivity {

    EditText pass_code;
    Button check;

    int flag = 0;

    ArrayList<String> codelist = new ArrayList<>();

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_passcode_validity);

        pass_code = (EditText) findViewById(R.id.p_code);
        check = (Button) findViewById(R.id.check);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Admin").document("Passcodes")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    List<String> arr1 = (List<String>) documentSnapshot.get("codelist");
                    codelist.addAll(arr1);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(CheckPasscodeValidityActivity.this, R.anim.blink_anim);
                check.startAnimation(animation);

                String passcode = pass_code.getText().toString();

                if(TextUtils.isEmpty(passcode))
                {
                    Toast.makeText(getApplicationContext(),"Enter passcode.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setMessage("Please wait..");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);

                    if(passcode.startsWith("CSE-") && passcode.endsWith("-30"))
                    {
                        int p = passcode.indexOf("-");
                        String s = passcode.substring(p+1, passcode.length());
                        p = s.indexOf("-");
                        s = s.substring(0, p);

                        if(s.length() == 3)
                        {
                            if(TextUtils.isDigitsOnly(s))
                            {
                                flag = 1;
                            }
                            else
                            {
                                flag = 0;
                            }
                        }
                        else
                        {
                            flag = 0;
                        }
                    }
                    else
                    {
                        flag = 0;
                    }


                    if (flag == 1)
                    {
                        if(codelist.contains(passcode))
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"This passcode is already used.",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"This passcode is available.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (flag == 0)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Passcode format is not right.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onBackPressed(){

        this.finish();
    }
}
