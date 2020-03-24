package com.golan.amit.barbacksitrolltrace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WorkOutSessionAdapter extends ArrayAdapter<WorkOutSession> {

    Context context;
    List<WorkOutSession> objects;

    public WorkOutSessionAdapter(Context context, int resource, List<WorkOutSession> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public WorkOutSession getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        View view = convertView;
        Holder holder;

        if(view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.custom_row, parent, false);
            holder = new Holder();
            holder.tvTotalView = view.findViewById(R.id.tvTotalId);
            holder.tvGoodView = view.findViewById(R.id.tvGoodId);
            holder.tvDateView = view.findViewById(R.id.tvDateId);
            holder.imageView = view.findViewById(R.id.iv);
            view.setTag(holder);
        } else {
            holder = (Holder)view.getTag();
        }

        WorkOutSession temp = objects.get(position);
        holder.imageView.setOnClickListener(popUpListenetr);
        Integer rowPosition = position;
        holder.imageView.setTag(rowPosition);

        holder.tvTotalView.setText(String.valueOf(temp.getTotal()));
        holder.tvGoodView.setText(String.valueOf(temp.getGood()));
        holder.tvDateView.setText(String.valueOf(temp.getCurr_datetime()));
        if(temp.getGood() > 0) {
            holder.imageView.setImageResource(R.mipmap.thumbupgreen);
        } else {
            holder.imageView.setImageResource(R.mipmap.thumbdownred);
        }
        return view;
    }


    View.OnClickListener popUpListenetr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer viewPosition = (Integer)v.getTag();
            WorkOutSession wos = objects.get(viewPosition);
            if(wos != null) {
                int id = wos.getId();
            }
        }
    };



    public static class Holder {
        ImageView imageView;
        TextView tvTotalView;
        TextView tvGoodView;
        TextView tvDateView;
    }

}
