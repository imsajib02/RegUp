package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveFacultyActivity extends AppCompatActivity {

    EditText sh_code;
    Button search, remove;
    TextView f_name, f_post, f_dept;

    String fac_userid, fetched_name, fetched_dept, fetched_codeword;
    Boolean registered;

    ArrayList<String> codelist = new ArrayList<>();

    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_faculty);

        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        sh_code = (EditText) findViewById(R.id.s_code);
        search = (Button) findViewById(R.id.search);
        remove = (Button) findViewById(R.id.remove);
        f_name = (TextView) findViewById(R.id.name);
        f_post = (TextView) findViewById(R.id.post);
        f_dept = (TextView) findViewById(R.id.dept);

        remove.setVisibility(View.INVISIBLE);

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

        sh_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                remove.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(RemoveFacultyActivity.this, R.anim.blink_anim);
                search.startAnimation(animation);

                String shcode = sh_code.getText().toString();

                if(TextUtils.isEmpty(shcode))
                {
                    Toast.makeText(getApplicationContext(), "Enter faculty short code.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setMessage("Please wait..");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);

                    firestore.collection("Faculty").document(shcode)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot = task.getResult();

                                try
                                {
                                    fetched_name = documentSnapshot.getString("name");
                                    String fetched_post = documentSnapshot.getString("post");
                                    fetched_codeword = documentSnapshot.getString("codeword");
                                    fetched_dept = documentSnapshot.getString("dept");
                                    fac_userid = documentSnapshot.getString("userid");
                                    registered = documentSnapshot.getBoolean("registered");

                                    if(!TextUtils.isEmpty(fetched_name))
                                    {
                                        f_name.setText("Name: " +fetched_name);
                                        f_post.setText("Designation: " +fetched_post);

                                        if(TextUtils.equals(fetched_dept,"CSE"))
                                        {
                                            f_dept.setText("Department: Computer Science & Engineering");
                                        }

                                        progressDialog.dismiss();
                                        remove.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        f_name.setText("");
                                        f_post.setText("");
                                        f_dept.setText("");
                                        remove.setVisibility(View.INVISIBLE);

                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Faculty with this short code does not exist.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch(Exception e)
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Error occurred! Try again!",Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(RemoveFacultyActivity.this, R.anim.blink_anim);
                remove.startAnimation(animation);

                final String shcode = sh_code.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setMessage("Remove " +fetched_name+ " as a faculty of " +fetched_dept+ " department?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                progressDialog.setMessage("Please wait..");
                                progressDialog.show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(false);

                                firestore.collection("Faculty").document(shcode)
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            if(!registered)
                                            {
                                                remove.setVisibility(View.INVISIBLE);
                                                fac_userid = "";

                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Faculty is successfully removed.", Toast.LENGTH_LONG).show();
                                            }
                                            else if(registered)
                                            {
                                                final Map<String, Object> facultydata = new HashMap<>();
                                                facultydata.put("disabled",true);

                                                firestore.collection("User").document(fac_userid)
                                                        .update(facultydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful())
                                                        {
                                                            codelist.remove(fetched_codeword);
                                                            remove.setVisibility(View.INVISIBLE);
                                                            fac_userid = "";

                                                            final Map<String, Object> data = new HashMap<>();
                                                            data.put("codelist", codelist);

                                                            firestore.collection("Admin").document("Passcodes")
                                                                    .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful())
                                                                    {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(), "Faculty is successfully removed.", Toast.LENGTH_LONG).show();
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

                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })

                        .setNegativeButton("No", null)
                        .show();

                Dialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
            }
        });
    }

    public void onBackPressed(){

        this.finish();
    }
}
