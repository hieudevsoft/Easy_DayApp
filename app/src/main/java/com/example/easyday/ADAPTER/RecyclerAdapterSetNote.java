package com.example.easyday.ADAPTER;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.ImageNote;
import com.example.easyday.R;


import java.io.ByteArrayOutputStream;
import java.util.List;

public class RecyclerAdapterSetNote extends RecyclerView.Adapter<RecyclerAdapterSetNote.ViewHolder> {
    private Context context;
    private List<ImageNote> listImageNote=null;
    public RecyclerAdapterSetNote(Context context, List<ImageNote> listImageNote) {
        this.context = context;
        this.listImageNote = listImageNote;
    }


    public RecyclerAdapterSetNote(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapterSetNote.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_image_note, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterSetNote.ViewHolder holder, final int position) {
        if(listImageNote!=null) {
            holder.setComponents(listImageNote.get(position));
            Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_note);
            holder.itemView.startAnimation(animation);

            holder.bt_submit_image_note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String des = holder.edt_des.getText().toString().trim();
                    listImageNote.get(position).setDescriptionImage(des);
                    TOOL.setToast(context, "Saved ~");
                    notifyDataSetChanged();
                }
            });
            holder.bt_delete_image_note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positionTemp = position+1;
                    listImageNote.remove(position);
                    TOOL.setToast(context, "You deleted note "+positionTemp+"");
                    notifyDataSetChanged();
                }
            });
            holder.imgNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.layout_show_image);
                    ImageView imageView = dialog.findViewById(R.id.showImage);
//                    BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imgNote.getDrawable();
 //                   Bitmap bitmap = bitmapDrawable.getBitmap();
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100,byteArrayOutputStream);
//                    byte[] img = byteArrayOutputStream.toByteArray();
                    imageView.setImageBitmap(listImageNote.get(position).getImageNote());
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(listImageNote!=null)
        return listImageNote.size();
        else return 1;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgNote;
        private EditText edt_des;
        Button bt_submit_image_note , bt_delete_image_note;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNote = itemView.findViewById(R.id.image_Note);
            edt_des = itemView.findViewById(R.id.des_image_note);
            bt_delete_image_note = itemView.findViewById(R.id.delete_imageNote);
            bt_submit_image_note = itemView.findViewById(R.id.submit_imageNote);
        }
        public void setComponents(ImageNote Note){
            imgNote.setImageBitmap(Note.getImageNote());
            edt_des.setText(Note.getDescriptionImage());
        }
    }
}
