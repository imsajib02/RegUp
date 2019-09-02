package bd.app.mypackage.regup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FacultyView3Adapter extends ArrayAdapter<FacultyView3Item> {

    ArrayList<FacultyView3Item> adapterlist = new ArrayList<>();

    public FacultyView3Adapter(Context context, int textViewResourceId, ArrayList<FacultyView3Item> objects){

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
        v = inflater.inflate(R.layout.faculty_list_view3_layout, null);

        TextView code = (TextView) v.findViewById(R.id.ccode);
        TextView name = (TextView) v.findViewById(R.id.cname);
        TextView credit = (TextView) v.findViewById(R.id.ccredit);
        TextView type = (TextView) v.findViewById(R.id.ctype);

        code.setText("Code: " +adapterlist.get(position).getCode());
        name.setText("Name: " +adapterlist.get(position).getName());
        credit.setText("Credit: " +adapterlist.get(position).getCredit());
        type.setText("Type: " +adapterlist.get(position).getType());

        return v;
    }
}
