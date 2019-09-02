package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class UpdateSupervisionActivity extends AppCompatActivity {

    EditText fac_code, s_intake, s_section;
    TextView f_name, f_post, f_dept;
    static ListView listView;
    Button search, addlist, update;
    Spinner shift;

    String fetched_name;

    static ArrayList<String> daylist = new ArrayList<>();
    static ArrayList<String> evelist = new ArrayList<>();

    static ArrayList<UpdateSupervisionItem> adapterlist = new ArrayList<>();

    private String[] shifttypes = {"SHIFT", "DAY", "EVE"};

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_supervision);

        fac_code = (EditText) findViewById(R.id.sh_code);
        f_name = (TextView) findViewById(R.id.name);
        f_post = (TextView) findViewById(R.id.post);
        f_dept = (TextView) findViewById(R.id.dept);
        s_intake = (EditText) findViewById(R.id.intake);
        s_section = (EditText) findViewById(R.id.section);
        listView = (ListView) findViewById(R.id.faclistview);
        shift = (Spinner) findViewById(R.id.shift);
        addlist = (Button) findViewById(R.id.addlist);
        search = (Button) findViewById(R.id.search);
        update = (Button) findViewById(R.id.update);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        s_intake.setEnabled(false);
        s_section.setEnabled(false);
        shift.setEnabled(false);
        addlist.setEnabled(false);
        update.setEnabled(false);
        listView.setEnabled(false);


        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        final ArrayAdapter<String> ar2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shifttypes){

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

        ar2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shift.setAdapter(ar2);


        fac_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                f_name.setText("");
                f_post.setText("");
                f_dept.setText("");

                s_intake.setText("");
                s_section.setText("");
                shift.setSelection(0);

                daylist.clear();
                evelist.clear();
                CallAdapter(getApplicationContext());

                s_intake.setEnabled(false);
                s_section.setEnabled(false);
                shift.setEnabled(false);
                addlist.setEnabled(false);
                update.setEnabled(false);
                listView.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(UpdateSupervisionActivity.this, R.anim.blink_anim);
                search.startAnimation(animation);

                String shcode = fac_code.getText().toString();

                if(TextUtils.isEmpty(shcode))
                {
                    Toast.makeText(getApplicationContext(), "Fill up required information.", Toast.LENGTH_SHORT).show();
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
                                daylist.clear();
                                evelist.clear();

                                try
                                {
                                    fetched_name = documentSnapshot.getString("name");
                                    String fetched_post = documentSnapshot.getString("post");
                                    String fetched_dept = documentSnapshot.getString("dept");

                                    if(!TextUtils.isEmpty(fetched_name))
                                    {
                                        progressDialog.dismiss();

                                        s_intake.setText("");
                                        s_section.setText("");
                                        shift.setSelection(0);

                                        s_intake.setEnabled(true);
                                        s_section.setEnabled(true);
                                        shift.setEnabled(true);
                                        addlist.setEnabled(true);
                                        update.setEnabled(true);
                                        listView.setEnabled(true);

                                        f_name.setText("Name: " +fetched_name);
                                        f_post.setText("Designation: " +fetched_post);

                                        if(TextUtils.equals(fetched_dept,"CSE"))
                                        {
                                            f_dept.setText("Department: Computer Science & Engineering");
                                        }

                                        List<String> arr1 = (List<String>) documentSnapshot.get("day");
                                        daylist.addAll(arr1);

                                        List<String> arr2 = (List<String>) documentSnapshot.get("eve");
                                        evelist.addAll(arr2);


                                        //using 1.5 second delay
                                        //because it takes some time to fetch data from the server
                                        new CountDownTimer(1500, 1000) {
                                            public void onFinish() {

                                                CallAdapter(getApplicationContext());
                                            }

                                            public void onTick(long millisUntilFinished) {
                                                // millisUntilFinished    The amount of time until finished.
                                            }
                                        }.start();
                                    }
                                    else
                                    {
                                        f_name.setText("");
                                        f_post.setText("");
                                        f_dept.setText("");

                                        s_intake.setText("");
                                        s_section.setText("");
                                        shift.setSelection(0);

                                        daylist.clear();
                                        evelist.clear();
                                        CallAdapter(getApplicationContext());

                                        s_intake.setEnabled(false);
                                        s_section.setEnabled(false);
                                        shift.setEnabled(false);
                                        addlist.setEnabled(false);
                                        update.setEnabled(false);
                                        listView.setEnabled(false);

                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Faculty with this short code does not exist.",Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch(Exception e)
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Error occurred! Try again!",Toast.LENGTH_SHORT).show();
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


        addlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(UpdateSupervisionActivity.this, R.anim.blink_anim);
                addlist.startAnimation(animation);

                String intake = s_intake.getText().toString();
                String section = s_section.getText().toString();

                if (TextUtils.isEmpty(intake))
                {
                    if (TextUtils.isEmpty(section))
                    {
                        Toast.makeText(getApplicationContext(), "Fill up intake and section.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (TextUtils.isDigitsOnly(section))
                        {
                            Toast.makeText(getApplicationContext(), "Fill up intake.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Fill up intake and only numbers are allowed for section.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    if (TextUtils.isEmpty(section))
                    {
                        if (!TextUtils.isDigitsOnly(intake))
                        {
                            Toast.makeText(getApplicationContext(), "Fill up section and only numbers are allowed for intake.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Fill up section.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        if (!TextUtils.isDigitsOnly(intake) && TextUtils.isDigitsOnly(section))
                        {
                            Toast.makeText(getApplicationContext(), "Only numbers are allowed for intake.", Toast.LENGTH_SHORT).show();
                        }

                        else if (TextUtils.isDigitsOnly(intake) && !TextUtils.isDigitsOnly(section))
                        {
                            Toast.makeText(getApplicationContext(), "Only numbers are allowed for section.", Toast.LENGTH_SHORT).show();
                        }

                        else if (!TextUtils.isDigitsOnly(intake) && !TextUtils.isDigitsOnly(section))
                        {
                            Toast.makeText(getApplicationContext(), "Only numbers are allowed for intake and section.", Toast.LENGTH_SHORT).show();
                        }

                        else if (TextUtils.isDigitsOnly(intake) && TextUtils.isDigitsOnly(section))
                        {
                            if(TextUtils.equals(shift.getSelectedItem().toString(), "DAY"))
                            {
                                if(daylist.contains(intake+ "-" +section))
                                {
                                    //already in list
                                }
                                else
                                {
                                    daylist.add(intake+ "-" +section);
                                    CallAdapter(getApplicationContext());
                                }
                            }
                            else if(TextUtils.equals(shift.getSelectedItem().toString(), "EVE"))
                            {
                                if(evelist.contains(intake+ "-" +section))
                                {
                                    //already in list
                                }
                                else
                                {
                                    evelist.add(intake+ "-" +section);
                                    CallAdapter(getApplicationContext());
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Select shift!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(UpdateSupervisionActivity.this, R.anim.blink_anim);
                update.startAnimation(animation);

                final String shcode = fac_code.getText().toString();

                if(TextUtils.isEmpty(shcode))
                {
                    Toast.makeText(getApplicationContext(), "Enter faculty short code.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setMessage("Update supervision list of " +fetched_name+ "?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    progressDialog.setMessage("Please wait..");
                                    progressDialog.show();
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.setCancelable(false);

                                    final Map<String, Object> facultydata = new HashMap<>();
                                    facultydata.put("day", daylist);
                                    facultydata.put("eve", evelist);

                                    firestore.collection("Faculty").document(shcode)
                                            .update(facultydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Successfully updated.", Toast.LENGTH_LONG).show();
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
            }
        });
    }


    public static void CallAdapter(Context context)
    {
        adapterlist.clear();

        if(daylist.size() == 0)
        {
            //no intake list for day shift

            if(evelist.size() == 0)
            {
                //no intake list for evening shift
            }
            else if(evelist.size() > 0)
            {
                for (int i=0;i<evelist.size();i++)
                {
                    String s = evelist.get(i);
                    int p = s.indexOf("-");

                    String intake = s.substring(0,p);
                    String section = s.substring(p+1,s.length());

                    adapterlist.add(new UpdateSupervisionItem(intake, section, "Eve"));
                }
            }
        }
        else if(daylist.size() > 0)
        {
            for (int i=0;i<daylist.size();i++)
            {
                String s = daylist.get(i);
                int p = s.indexOf("-");

                String intake = s.substring(0,p);
                String section = s.substring(p+1,s.length());

                adapterlist.add(new UpdateSupervisionItem(intake, section, "Day"));
            }

            if(evelist.size() == 0)
            {
                //no intake list for evening shift
            }
            else if(evelist.size() > 0)
            {
                for (int i=0;i<evelist.size();i++)
                {
                    String s = evelist.get(i);
                    int p = s.indexOf("-");

                    String intake = s.substring(0,p);
                    String section = s.substring(p+1,s.length());

                    adapterlist.add(new UpdateSupervisionItem(intake, section, "Eve"));
                }
            }
        }

        UpdateSupervisionAdapter updateSupervisionAdapter = new UpdateSupervisionAdapter(context, R.layout.update_supervision_list_view_layout, adapterlist);
        listView.setAdapter(updateSupervisionAdapter);
    }

    public void onBackPressed(){

        daylist.clear();
        evelist.clear();
        adapterlist.clear();
        this.finish();
    }
}
