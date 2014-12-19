package pe.sbk.alwaysontop;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter{

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public CustomAdapter ( Context ctx, int resourceId, List apps) {

        super( ctx, resourceId, apps );
        resource = resourceId;
        inflater = LayoutInflater.from( ctx );
        context=ctx;
    }

    @Override
    public View getView ( int position, View convertView, ViewGroup parent ) {

        convertView = (LinearLayout) inflater.inflate( resource, null );

        //ArrayList<String> app = (ArrayList<String>) getItem(position);

        String app = (String)getItem(position);
        TextView txtName = (TextView) convertView.findViewById(R.id.textView1);
        txtName.setText(app);

        ImageView imageCity = (ImageView) convertView.findViewById(R.id.imageView1);
        imageCity.setImageResource(R.drawable.q_icon);
        return convertView;
    }
}