package bd.app.mypackage.regup;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AddFacultyAdapter extends ArrayAdapter<UpdateSupervisionItem> {

    ArrayList<UpdateSupervisionItem> adapterlist = new ArrayList<>();
    Context context;

    public AddFacultyAdapter(Context context, int textViewResourceId, ArrayList<UpdateSupervisionItem> objects) {

        super(context, textViewResourceId, objects);
        this.context = context;
        adapterlist = objects;
    }

    @Override
    public int getCount() {

        return super.getCount();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.update_supervision_list_view_layout, null);

        TextView intake = (TextView) v.findViewById(R.id.intake);
        TextView section = (TextView) v.findViewById(R.id.section);
        TextView shift = (TextView) v.findViewById(R.id.shift);
        final Button remove = (Button) v.findViewById(R.id.remove);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                remove.startAnimation(animation);

                UpdateSupervisionItem updateSupervisionItem = getItem(position);

                String in_take = updateSupervisionItem.getIntake();
                String sec_tion = updateSupervisionItem.getSection();
                String shi_ft = updateSupervisionItem.getShift();

                if(TextUtils.equals(shi_ft,"Day"))
                {
                    AddFacultyActivity.daylist.remove(in_take+ "-" +sec_tion);
                    AddFacultyActivity.CallAdapter(context);
                }
                else if(TextUtils.equals(shi_ft,"Eve"))
                {
                    AddFacultyActivity.evelist.remove(in_take+ "-" +sec_tion);
                    AddFacultyActivity.CallAdapter(context);
                }
            }
        });

        intake.setText("Intake: " +adapterlist.get(position).getIntake());
        section.setText("Section: " +adapterlist.get(position).getSection());
        shift.setText("Shift: " +adapterlist.get(position).getShift());

        return v;
    }

}