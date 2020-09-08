package com.example.easyday.ADAPTER;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyday.CONTROL.HelpersService;
import com.example.easyday.ENTITY.Note;
import com.example.easyday.FRAGMENT.note.MainNoteFragmentDirections;
import com.example.easyday.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterMainNote extends RecyclerView.Adapter<RecyclerAdapterMainNote.ViewHolder>{
    private List<Note> listNote;
    private Context context;
    private List<Note> temp;

    public RecyclerAdapterMainNote(List<Note> listNote, Context context) {
        this.listNote = listNote;
        this.context = context;
        this.temp = new ArrayList<>(listNote);
    }

    @NonNull
    @Override
    public RecyclerAdapterMainNote.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.label_note, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterMainNote.ViewHolder holder, final int position) {
        holder.setComponents(listNote.get(position));
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_note);
        holder.itemView.startAnimation(animation);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Notification");
                builder.setMessage("Do you want delete it ?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            String idNote = listNote.get(position).getIdNote();
                            if(listNote.get(position).getImgNotes().size()!=0)
                            for(int i = 0; i < listNote.get(position).getImgNotes().size();i++)
                            {
                                HelpersService.deleteImageNoteByPosition(context, idNote.concat(i+""));
                            }else  {
                                HelpersService.deleteImageNoteByPosition(context, idNote);
                            }



                        listNote.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                builder.show();
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainNoteFragmentDirections.ActionAddNote action = MainNoteFragmentDirections.actionAddNote();
                action.setNote(listNote.get(position));
                Navigation.findNavController((Activity) context,R.id.fragmentHost).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView title;
        private  MaterialTextView content;
        private CardView priority;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_Note);
            content = itemView.findViewById(R.id.content_Note);
            priority = itemView.findViewById(R.id.priority_Note);
        }
        public void setComponents(Note note)
        {
            title.setText(note.getTitle());
            content.setText(note.getContent());
            switch (note.getLevel())
            {
                case 1:
                    priority.setCardBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    priority.setCardBackgroundColor(Color.YELLOW);
                    break;
                case 3:
                    priority.setCardBackgroundColor(Color.RED);
                    break;
            }
        }
    }
}
