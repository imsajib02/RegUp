package bd.app.mypackage.regup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class LoginDialog extends AppCompatDialogFragment {

    EditText email, password;
    LoginDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.login_dialog_layout, null);

        builder.setView(view)
                .setTitle("Session out! Please login.")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mail = email.getText().toString();
                        String pass = password.getText().toString();

                        listener.applytexts(mail, pass);
                    }
                });


        email = view.findViewById(R.id.mail);
        password = view.findViewById(R.id.pass);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try
        {
            listener = (LoginDialogListener) context;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public interface LoginDialogListener{

        void applytexts(String email, String password);
    }
}
