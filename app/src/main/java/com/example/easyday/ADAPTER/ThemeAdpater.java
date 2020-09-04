package com.example.easyday.ADAPTER;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.easyday.CONTROL.SendTheme;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.FRAGMENT.ThemesFragment;
import com.example.easyday.R;

import java.util.List;

public class ThemeAdpater extends BaseAdapter {
    List<String> listTheme;
    Context context;
    Activity activity;
    public ThemeAdpater(List<String> listTheme, Context context,Activity activity) {
        this.listTheme = listTheme;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return listTheme.size();
    }

    @Override
    public Object getItem(int position) {
        return listTheme.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        ImageView cardView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View view = convertView;
        if(view==null)
        {
        view = LayoutInflater.from(context).inflate(R.layout.layout_card_themes,null);
        viewHolder = new ViewHolder();
        viewHolder.cardView =(ImageView) view.findViewById(R.id.card_theme);
        view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();
        if(!listTheme.get(position).isEmpty()&&listTheme.size()>=1)
        viewHolder.cardView.setImageBitmap(TOOL.convertStringToBitmap(listTheme.get(position)));
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TOOL.showToastCustomDone(context);
                SendTheme viewModel = ViewModelProviders.of((FragmentActivity) activity).get(SendTheme.class);
                viewModel.getUri().postValue(listTheme.get(position));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Theme already update~~", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);

            }
        });
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ThemesFragment.setOnLongClickItem(position,context);
                return true;
            }
        });
        return view;
    }
}
