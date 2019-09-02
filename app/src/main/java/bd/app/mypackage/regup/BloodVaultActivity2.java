package bd.app.mypackage.regup;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class BloodVaultActivity2 extends AppCompatActivity {

    Spinner sp;
    Button search;
    ListView listView;

    int REQUEST_CALL = 1, flag = 0;
    String number, value = null;;
    Context mcontext;

    ArrayList<BloodVaultItem> adapterlist = new ArrayList<>();

    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> contactlist = new ArrayList<>();

    String[] type = {"SELECT BLOOD GROUP  - - -", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_vault2);

        sp = (Spinner) findViewById(R.id.sp);
        search = (Button) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.listview);

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

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position > 0)
                {
                    flag = 1;
                }
                else
                {
                    flag = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(BloodVaultActivity2.this, R.anim.blink_anim);
                search.startAnimation(animation);

                final String blood_group = sp.getSelectedItem().toString();

                namelist.clear();
                contactlist.clear();

                if(flag == 0)
                {
                    Toast.makeText(getApplicationContext(), "Select blood group.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firestore.collection("BloodVault").document("bloodtype")
                            .collection(blood_group).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(task.isSuccessful())
                            {
                                for (DocumentSnapshot document : task.getResult()) {

                                    //donor name with contact number
                                    final String s = document.getId();

                                    int p = s.indexOf("_");

                                    String temp = s;
                                    String name = s.substring(0,p);
                                    String contact = temp.substring(p+1,s.length());

                                    namelist.add(name);
                                    contactlist.add(contact);
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //1.8 second delay
                    new CountDownTimer(1800, 1000) {
                        public void onFinish() {

                            adapterlist.clear();

                            for(int i=0;i<namelist.size();i++)
                            {
                                adapterlist.add(new BloodVaultItem(namelist.get(i), contactlist.get(i), blood_group));
                            }

                            BloodVaultAdapter bloodVaultAdapter = new BloodVaultAdapter(getApplicationContext(),R.layout.blood_vault_list_view_layout,adapterlist);
                            listView.setAdapter(bloodVaultAdapter);
                        }

                        public void onTick(long millisUntilFinished) {
                            // millisUntilFinished    The amount of time until finished.
                        }
                    }.start();
                }
            }
        });
    }



    public void makephonecall(Context context)
    {
        this.mcontext = context;

        try {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(BloodVaultActivity2.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CALL)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                makephonecall(mcontext);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Grant call permission in settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //creating the action bar with home button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //home button click listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.homebutton:

                Intent i = new Intent(BloodVaultActivity2.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finishAffinity();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Exit Blood Vault?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(TextUtils.equals(value, "home"))
                        {
                            Intent intent = new Intent(BloodVaultActivity2.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else
                        {
                            finish();
                        }
                    }
                })

                .setNegativeButton("No", null)
                .show();

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
    }

}
