package com.example.easyday.ADAPTER;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.easyday.CONTROL.HelpersServiceThemes;
import com.example.easyday.CONTROL.SendTheme;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.FRAGMENT.home.ThemesFragment;
import com.example.easyday.R;

import java.util.List;

public class ThemeAdapter extends BaseAdapter {
    List<String> listTheme;
    Context context;
    Activity activity;
    public ThemeAdapter(List<String> listTheme, Context context,Activity activity) {
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
                final AlertDialog.Builder deleteTheme = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_DayNight_Dialog));
                deleteTheme.setTitle("Question?");
                deleteTheme.setMessage("Do you want remove theme?");
                deleteTheme.setIcon(R.drawable.ic_themes_me);
                deleteTheme.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        listTheme.remove(position);
                        notifyDataSetChanged();
                        for (String uri : listTheme)
                            stringBuilder.append(uri + ",");
                        HelpersServiceThemes.updateData(ThemesFragment.getUser().getUid(), stringBuilder.toString(), HelpersServiceThemes.getUrlWebserviceUpdate(),context);
                    }

                });
                deleteTheme.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                deleteTheme.show();
                return true;
            }
        });
        return view;
    }

}
