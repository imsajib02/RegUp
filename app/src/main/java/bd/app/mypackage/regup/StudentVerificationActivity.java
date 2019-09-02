package bd.app.mypackage.regup;

/*Registration verification activity.
  Check if the user is a valid student of BUBT
 */

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentVerificationActivity extends AppCompatActivity {

    private EditText var_id, ser_num;
    private Button verify;

    public String ID, S_num, result, get_serial, get_name, get_dept, get_int, get_sec;
    private String[] deptnames = {"BBA","MBA DAY","MBA EVE","EXE MBA","CSIT","CSE DAY","ENGLISH (BA)",
                                    "ENGLISH (MA)", "LLB (4 YR)", "LLB (1 YR)", "MATH (MSc 1 YR)", "LLB (2 YR)",
                                    "ECO (MSc)", "LLM (1 YR)", "LLM (2 YR)", "ELT (MA 1 YR)", "MBM", "MATH (MSc 2 YR)",
                                    "CSE EVE", "ECO", "EEE DAY", "TEXTILE DAY", "EEE EVE", "TEXTILE EVE", "EDE", "ARCHITECHTURE"};

    public static ArrayList<String> info = new ArrayList<>();

    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_verification);

        var_id = (EditText) findViewById(R.id.v_id);
        ser_num = (EditText) findViewById(R.id.ser_num);
        verify = (Button) findViewById(R.id.verify);

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(StudentVerificationActivity.this, R.anim.blink_anim);
                verify.startAnimation(animation);

                ID = var_id.getText().toString();
                S_num = ser_num.getText().toString();

                //using a fake API URL
                //TODO: Replace the whole URL with your own database API URL
                String server_url = "https://www.yourweblink.com/verify.php?rand='." +ID;

                if(TextUtils.isEmpty(ID) || TextUtils.isEmpty(S_num)){

                    Toast.makeText(getApplicationContext(),"Fill up missing information.",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    progressDialog.setMessage("Please wait..");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);

                    /*using volley library function to send a request to the
                      php file with the users ID number.
                     */
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            /*
                               Using try-catch block to handle stringIndexOutOfBound exception.
                                For invalid ID and Serial Number php file returns a null string.
                                This string generates stringIndexOutOfBound exception.
                             */
                            try {

                                if (!response.isEmpty()) {
                                    result = response;

                                    //filtering section from the string
                                    int a = result.lastIndexOf(',');
                                    String temp = result;
                                    get_sec = result.substring(a + 1, result.length());
                                    result = temp.substring(0, a);
                                    a = get_sec.lastIndexOf('"');
                                    get_sec = get_sec.substring(0, a);
                                    a = get_sec.lastIndexOf('"');
                                    get_sec = get_sec.substring(a + 1, get_sec.length());

                                    //filtering intake from the string
                                    a = result.lastIndexOf(',');
                                    temp = result;
                                    get_int = result.substring(a + 1, result.length());
                                    result = temp.substring(0, a);
                                    a = get_int.lastIndexOf('"');
                                    get_int = get_int.substring(0, a);
                                    a = get_int.lastIndexOf('"');
                                    get_int = get_int.substring(a + 1, get_int.length());

                                    //filtering department from the string
                                    a = result.lastIndexOf(',');
                                    temp = result;
                                    get_dept = result.substring(a + 1, result.length());
                                    result = temp.substring(0, a);
                                    a = get_dept.lastIndexOf('"');
                                    get_dept = get_dept.substring(0, a);
                                    a = get_dept.lastIndexOf('"');
                                    get_dept = get_dept.substring(a + 1, get_dept.length());
                                    get_dept = deptnames[Integer.parseInt(get_dept) - 1];

                                    //filtering name from the string
                                    a = result.lastIndexOf(',');
                                    temp = result;
                                    get_name = result.substring(a + 1, result.length());
                                    result = temp.substring(0, a);
                                    a = get_name.lastIndexOf('"');
                                    get_name = get_name.substring(0, a);
                                    a = get_name.lastIndexOf('"');
                                    get_name = get_name.substring(a + 1, get_name.length());

                                    //filtering serial number from the string
                                    a = result.lastIndexOf(',');
                                    get_serial = result.substring(a + 1, result.length());
                                    a = get_serial.lastIndexOf('"');
                                    get_serial = get_serial.substring(0, a);
                                    a = get_serial.lastIndexOf('"');
                                    get_serial = get_serial.substring(a + 1, get_serial.length());

                                    //check if the ID is already registered
                                    firestore.collection("StudentData").document(get_dept)
                                            .collection("intake").document(get_int)
                                            .collection("section").document(get_sec)
                                            .collection("id").document(ID)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if(task.isSuccessful())
                                            {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                String name = documentSnapshot.getString("name");

                                                if(TextUtils.isEmpty(name))
                                                {
                                                    //checking if serial number matches the user input
                                                    if (TextUtils.equals(get_serial, S_num)) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Verified as " +get_name+ ".", Toast.LENGTH_SHORT).show();

                                                        info.add(get_dept);
                                                        info.add(get_int);
                                                        info.add(get_sec);
                                                        info.add(ID);
                                                        info.add(get_name);

                                                        //send user to StudentRegistrationActivity
                                                        Intent intent = new Intent(StudentVerificationActivity.this, StudentRegistrationActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    //if serial number does not match
                                                    else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Invalid Serial Number!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                                else
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"ID is already registered!",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Connectivity problem! Try again!",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Invalid ID/Serial Number! Try again!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Connectivity problem! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("rand", ID); //TODO: pass the ID as a parameter
                            params.put("username", "your_username_here"); //TODO: pass the username for your API as a parameter (if any)
                            params.put("password", "your_password_here"); //TODO: pass the password for your API as a parameter (if any)
                            params.put("systemType", "your_projectname_here"); //TODO: pass the project name for your API as a parameter (if any)
                            return params;
                        }
                    };


                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);

                }

            }
        });
    }

    public void onBackPressed(){
        //goes to Registration User Type activity
        this.finish();
    }
}
