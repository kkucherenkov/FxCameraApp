package com.af.experiments.FxCameraApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.af.experiments.FxCameraApp.shaders.GlShader;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends ArrayAdapter<GlShader> {
    private final Context context;
    private final ArrayList<GlShader> values;

    static class ViewHolder {
        public TextView text;
    }

    public FilterAdapter(Context context, int resource, List<GlShader> objects) {
        super(context, resource, objects);
        this.context = context;
        values = (ArrayList<GlShader>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.label);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = values.get(position).getName();
        holder.text.setText(s);

        return rowView;
    }
}
