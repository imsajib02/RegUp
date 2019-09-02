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


public class FacultyView2Adapter extends ArrayAdapter<FacultyView2Item> {

    ArrayList<FacultyView2Item> adapterlist = new ArrayList<>();

    public FacultyView2Adapter(Context context, int textViewResourceId, ArrayList<FacultyView2Item> objects){

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
        v = inflater.inflate(R.layout.faculty_list_view2_layout, null);

        TextView name = (TextView) v.findViewById(R.id.name);
        TextView id = (TextView) v.findViewById(R.id.stid);
        final Button vieew = (Button) v.findViewById(R.id.view);

        vieew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                vieew.startAnimation(animation);

                FacultyView2Item facultyView2Item = getItem(position);

                String student_id = facultyView2Item.getId();
                String intake = FacultyViewActivity2.in_take;
                String section = FacultyViewActivity2.sec_tion;
                String dept = FacultyViewActivity2.de_pt;

                Bundle b = new Bundle();

                b.putString("id", student_id);
                b.putString("intake", intake);
                b.putString("section", section);
                b.putString("dept", dept);

                Intent intent = new Intent(getContext(),FacultyViewActivity3.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        name.setText("Name: " +adapterlist.get(position).getName());
        id.setText("ID: " +adapterlist.get(position).getId());

        return v;
    }
}
