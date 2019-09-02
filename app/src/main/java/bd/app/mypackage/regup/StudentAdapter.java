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

public class StudentAdapter extends ArrayAdapter<CourseItem> {

    ArrayList<CourseItem> adapterlist = new ArrayList<>();

    public StudentAdapter(Context context, int textViewResourceId, ArrayList<CourseItem> objects){

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
        v = inflater.inflate(R.layout.course_submission_list_view_layout, null);

        TextView ccode = (TextView) v.findViewById(R.id.ccode);
        TextView cname = (TextView) v.findViewById(R.id.cname);
        TextView ccredit = (TextView) v.findViewById(R.id.ccredit);
        TextView ctype = (TextView) v.findViewById(R.id.ctype);
        final Button remove = (Button) v.findViewById(R.id.remove);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                remove.startAnimation(animation);

                CourseItem courseItem = getItem(position);
                String code = courseItem.getCode();
                CourseSubmissionActivity c = new CourseSubmissionActivity();

                int index = c.codelist.indexOf(code);
                c.codelist.remove(index);
                c.namelist.remove(index);
                c.creditlist.remove(index);
                c.typelist.remove(index);

                double total_credit = 0.0;
                adapterlist.clear();

                for(int i=0;i<c.codelist.size();i++)
                {
                    total_credit = total_credit + Double.parseDouble(c.creditlist.get(i));

                    adapterlist.add(new CourseItem(c.codelist.get(i), c.namelist.get(i), c.creditlist.get(i), c.typelist.get(i)));
                }

                StudentAdapter studentAdapter = new StudentAdapter(getContext(),R.layout.course_submission_list_view_layout,adapterlist);
                c.listView.setAdapter(studentAdapter);

                c.totalcourse.setText("Total Course: " +c.codelist.size());
                c.totalcredit.setText("Total Credit: " +total_credit);
            }
        });

        ccode.setText("Code: " +adapterlist.get(position).getCode());
        cname.setText("Name: " +adapterlist.get(position).getName());
        ccredit.setText("Credit: " +adapterlist.get(position).getCredit());
        ctype.setText("Type: " +adapterlist.get(position).getType());

        return v;
    }
}
