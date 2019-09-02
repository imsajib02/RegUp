package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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

public class AddFacultyActivity extends AppCompatActivity {

    EditText f_name, s_code, p_code, s_intake, s_section;
    Spinner post, dept, shift;
    Button addlist, addfac;
    static ListView listView;

    static ArrayList<UpdateSupervisionItem> adapterlist = new ArrayList<>();

    int post_flag = 0;

    static ArrayList<String> daylist = new ArrayList<>();
    static ArrayList<String> evelist = new ArrayList<>();
    ArrayList<String> codelist = new ArrayList<>();

    private String[] posttypes = {"SELECT DESIGNATION  - - -", "Lecturer", "Assistant Professor", "Chairman & Professor"};
    private String[] deptnames = {"SELECT DEPARTMENT  - - -", "EEE", "CSE", "TEXTILE", "ENGLISH", "LAW", "BBA", "ECONOMICS"};
    private String[] shifttypes = {"SHIFT", "DAY", "EVE"};

    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);

        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        f_name = (EditText) findViewById(R.id.name);
        s_code = (EditText) findViewById(R.id.s_code);
        p_code = (EditText) findViewById(R.id.p_code);
        s_intake = (EditText) findViewById(R.id.intake);
        s_section = (EditText) findViewById(R.id.section);
        post = (Spinner) findViewById(R.id.post);
        dept = (Spinner) findViewById(R.id.dept);
        shift = (Spinner) findViewById(R.id.shift);
        addlist = (Button) findViewById(R.id.addlist);
        addfac = (Button) findViewById(R.id.add_fac);
        listView = (ListView) findViewById(R.id.faclistview);

        addlist.setEnabled(false);
        addfac.setEnabled(false);

        daylist.clear();
        evelist.clear();

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


        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, posttypes){

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
        post.setAdapter(ar);


        final ArrayAdapter<String> ar1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deptnames){

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
        dept.setAdapter(ar1);


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


        post.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position > 0)
                {
                    post_flag = 1;
                }
                else
                {
                    post_flag = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String sel_dept = dept.getItemAtPosition(position).toString();

                if(TextUtils.equals(sel_dept, "CSE"))
                {
                    addlist.setEnabled(true);
                    addfac.setEnabled(true);
                }
                else
                {
                    addlist.setEnabled(false);
                    addfac.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AddFacultyActivity.this, R.anim.blink_anim);
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


        addfac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AddFacultyActivity.this, R.anim.blink_anim);
                addfac.startAnimation(animation);

                final String name = f_name.getText().toString();
                final String sh_code = s_code.getText().toString();
                final String pa_code = p_code.getText().toString();
                final String f_post = post.getSelectedItem().toString();
                final String f_dept = dept.getSelectedItem().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(sh_code) || TextUtils.isEmpty(pa_code) || post_flag == 0)
                {
                    Toast.makeText(getApplicationContext(), "Fill up missing information.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String regex = ".*[a-z].*";

                    if (sh_code.matches(regex))
                    {
                        Toast.makeText(getApplicationContext(), "Use capital letters for short code.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                        builder.setMessage("Add " +name+ " as a " +f_post+ " of " +f_dept+ " department?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        progressDialog.setMessage("Please wait..");
                                        progressDialog.show();
                                        progressDialog.setCanceledOnTouchOutside(false);
                                        progressDialog.setCancelable(false);

                                        firestore.collection("Faculty").document(sh_code)
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if(task.isSuccessful())
                                                {
                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                    try
                                                    {
                                                        String fetched_name = documentSnapshot.getString("name");

                                                        if(TextUtils.isEmpty(fetched_name))
                                                        {
                                                            if(codelist.contains(pa_code))
                                                            {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "This passcode is already used.", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else
                                                            {
                                                                final Map<String, Object> facultydata = new HashMap<>();
                                                                facultydata.put("name", name);
                                                                facultydata.put("codeword", pa_code);
                                                                facultydata.put("post", f_post);
                                                                facultydata.put("dept", f_dept);
                                                                facultydata.put("registered", false);
                                                                facultydata.put("day", daylist);
                                                                facultydata.put("eve", evelist);

                                                                firestore.collection("Faculty").document(sh_code)
                                                                        .set(facultydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            daylist.clear();
                                                                            evelist.clear();
                                                                            codelist.add(pa_code);

                                                                            final Map<String, Object> data = new HashMap<>();
                                                                            data.put("codelist", codelist);

                                                                            firestore.collection("Admin").document("Passcodes")
                                                                                    .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getApplicationContext(), "Faculty is successfully added.", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });

                                                                            CallAdapter(getApplicationContext());
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
                                                            Toast.makeText(getApplicationContext(),"Faculty with this short code already exists.",Toast.LENGTH_LONG).show();
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
                                })

                                .setNegativeButton("No", null)
                                .show();

                        Dialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                    }

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

        AddFacultyAdapter addFacultyAdapter = new AddFacultyAdapter(context, R.layout.update_supervision_list_view_layout, adapterlist);
        listView.setAdapter(addFacultyAdapter);
    }

    public void onBackPressed(){

        daylist.clear();
        evelist.clear();
        adapterlist.clear();
        this.finish();
    }
}
