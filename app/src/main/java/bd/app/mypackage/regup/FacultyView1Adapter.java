package bd.app.mypackage.regup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FacultyView1Adapter extends ArrayAdapter<FacultyView1Item> {

    ArrayList<FacultyView1Item> adapterlist = new ArrayList<>();

    public FacultyView1Adapter(Context context, int textViewResourceId, ArrayList<FacultyView1Item> objects){

        super(context, textViewResourceId, objects);
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
        v = inflater.inflate(R.layout.faculty_list_view1_layout, null);

        TextView intake = (TextView) v.findViewById(R.id.intake);
        TextView section = (TextView) v.findViewById(R.id.section);
        TextView shift = (TextView) v.findViewById(R.id.shift);
        final Button vieew = (Button) v.findViewById(R.id.view);

        vieew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                vieew.startAnimation(animation);

                FacultyView1Item facultyView1Item = getItem(position);
                String in_take = facultyView1Item.getIntake();
                String sec_tion = facultyView1Item.getSection();
                String shi_ft = facultyView1Item.getShift();

                Bundle b = new Bundle();

                b.putString("intake", in_take);
                b.putString("section", sec_tion);
                b.putString("shift", shi_ft);

                Intent intent = new Intent(getContext(),FacultyViewActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        intake.setText("Intake: " +adapterlist.get(position).getIntake());
        section.setText("Section: " +adapterlist.get(position).getSection());
        shift.setText("Shift: " +adapterlist.get(position).getShift());

        return v;
    }
}
