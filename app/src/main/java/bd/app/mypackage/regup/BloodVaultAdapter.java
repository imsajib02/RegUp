package bd.app.mypackage.regup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BloodVaultAdapter extends ArrayAdapter<BloodVaultItem> {

    ArrayList<BloodVaultItem> adapterlist = new ArrayList<>();
    int REQUEST_CALL = 1;
    Context context;

    public BloodVaultAdapter(Context context, int textViewResourceId, ArrayList<BloodVaultItem> objects){

        super(context, textViewResourceId, objects);
        this.context = context;
        adapterlist = objects;
    }

    @Override
    public int getCount(){

        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.blood_vault_list_view_layout, null);

        TextView name = (TextView) v.findViewById(R.id.name);
        TextView group = (TextView) v.findViewById(R.id.group);
        final Button call = (Button) v.findViewById(R.id.call);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                call.startAnimation(animation);

                BloodVaultItem bloodVaultItem = getItem(position);

                String number = bloodVaultItem.getContact();

                BloodVaultActivity2 bloodVaultActivity2 = new BloodVaultActivity2();
                bloodVaultActivity2.number = number;
                bloodVaultActivity2.makephonecall(context);
                //(( BloodVaultActivity2)context).makephonecall();
            }
        });

        name.setText("Name: " +adapterlist.get(position).getName());
        group.setText("Blood Group: " +adapterlist.get(position).getGroup());

        return v;
    }
}
