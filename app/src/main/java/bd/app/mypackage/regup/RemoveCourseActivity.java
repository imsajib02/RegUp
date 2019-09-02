package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RemoveCourseActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    Button remove;

    Spinner sp_dept;

    int i = 0, j = 0;
    String deptname;

    private String[] dept = {"SELECT DEPARTMENT  - - -", "EEE", "CSE", "TEXTILE", "LAW", "ENGLISH", "ECONOMICS", "BBA"};
    private String[] major = {"SELECT MAJOR  - - -", "Artificial Intelligence", "Software", "Networking"};

    ArrayList<String> allcodelist = new ArrayList<>();
    ArrayList<String> corr_sem_list = new ArrayList<>();
    ArrayList<String> majorlist = new ArrayList<>();
    ArrayList<String> doublelist = new ArrayList<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_course);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        sp_dept = (Spinner) findViewById(R.id.sp_dept);
        remove = (Button) findViewById(R.id.remove);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        autoCompleteTextView.setEnabled(false);
        remove.setEnabled(false);

        ArrayAdapter<String> ar1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dept){

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

        ar1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_dept.setAdapter(ar1);


        sp_dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position > 0 && TextUtils.equals(sp_dept.getItemAtPosition(position).toString(), "CSE"))
                {
                    autoCompleteTextView.setEnabled(true);
                    deptname = sp_dept.getSelectedItem().toString();
                    remove.setEnabled(true);
                }
                else
                {
                    autoCompleteTextView.setEnabled(false);
                    deptname = "";
                    remove.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //get all course code with corresponding semester
        for(i=1;i<=12;i++)
        {
            //1 to 10th semester
            if(i<=10)
            {
                final String num = String.valueOf(i);

                firestore.collection("CourseList").document("department")
                        .collection("CSE").document("semester")
                        .collection(num).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot document : task.getResult()) {

                                final String s = document.getId();

                                //code is already in the arraylist
                                if(allcodelist.indexOf(s) != -1)
                                {
                                    doublelist.add(s);
                                }
                                //code is not in the arraylist
                                else
                                {
                                    allcodelist.add(s);
                                    corr_sem_list.add(num);
                                    majorlist.add("0");
                                }
                            }

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            //11th to 12th semester (major)
            else if(i>10)
            {
                final String num1 = String.valueOf(i);

                for(j=1;j<=3;j++)
                {
                    final String num2 = String.valueOf(j);

                    firestore.collection("CourseList").document("department")
                            .collection("CSE").document("semester")
                            .collection(num1).document("major")
                            .collection(num2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful())
                            {
                                for (DocumentSnapshot document : task.getResult()) {

                                    final String s = document.getId();

                                    //code is already in the arraylist
                                    if(allcodelist.indexOf(s) != -1)
                                    {
                                        doublelist.add(s);
                                    }
                                    //code is not in the arraylist
                                    else
                                    {
                                        allcodelist.add(s);
                                        corr_sem_list.add(num1);
                                        majorlist.add(num2);
                                    }
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }


        AutocompleteMethod();


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(RemoveCourseActivity.this, R.anim.blink_anim);
                remove.startAnimation(animation);

                final String code = autoCompleteTextView.getText().toString().toUpperCase();

                if(TextUtils.isEmpty(code))
                {
                    Toast.makeText(getApplicationContext(), "Enter course code.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(allcodelist.contains(code))
                    {
                        final int p = allcodelist.indexOf(code);

                        if(TextUtils.equals(majorlist.get(p), "0"))
                        {
                            //semester between 1 to 10
                            final String sem_pos = corr_sem_list.get(p);

                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                            builder.setMessage("Remove '" +code+ "' from semester " +sem_pos+ " of " +deptname+ " department?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            progressDialog.setMessage("Please wait..");
                                            progressDialog.show();
                                            progressDialog.setCanceledOnTouchOutside(false);
                                            progressDialog.setCancelable(false);

                                            firestore.collection("CourseList").document("department")
                                                    .collection(deptname).document("semester")
                                                    .collection(sem_pos).document(code)
                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful())
                                                    {
                                                        allcodelist.remove(p);
                                                        corr_sem_list.remove(p);
                                                        majorlist.remove(p);
                                                        AutocompleteMethod();

                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Course removed successfully.", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        else
                        {
                            //semester between 10 to 12
                            final String sem_pos = corr_sem_list.get(p);
                            final String maj_pos = majorlist.get(p);
                            String msg;

                            if(doublelist.contains(code))
                            {
                                msg = "Remove '" +code+ "' from semester " +sem_pos+ " of " +deptname+ " department?";
                            }
                            else
                            {
                                msg = "Remove '" +code+ "' from " +major[Integer.parseInt(maj_pos)]+ " in semester " +sem_pos+ " of " +deptname+ " department?";
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                            builder.setMessage(msg)
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            progressDialog.setMessage("Please wait..");
                                            progressDialog.show();
                                            progressDialog.setCanceledOnTouchOutside(false);
                                            progressDialog.setCancelable(false);

                                            firestore.collection("CourseList").document("department")
                                                    .collection(deptname).document("semester")
                                                    .collection(sem_pos).document("major")
                                                    .collection(maj_pos).document(code)
                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful())
                                                    {
                                                        allcodelist.remove(p);
                                                        corr_sem_list.remove(p);
                                                        majorlist.remove(p);
                                                        AutocompleteMethod();

                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Course removed successfully.", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Select a valid course from suggestion.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    public void AutocompleteMethod()
    {
        //using 1.5 second delay
        //because it takes some time to fetch data from the server
        new CountDownTimer(1500, 1000) {
            public void onFinish() {

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, allcodelist);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                autoCompleteTextView.setAdapter(adapter);
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();
    }

    @Override
    public void onBackPressed() {

        allcodelist.clear();
        corr_sem_list.clear();
        majorlist.clear();
        this.finish();
    }
}
