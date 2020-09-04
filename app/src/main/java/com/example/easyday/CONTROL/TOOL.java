package com.example.easyday.CONTROL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.easyday.ENTITY.ImageNote;
import com.example.easyday.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.easyday.FRAGMENT.HomeFragment;
import com.example.easyday.FRAGMENT.MeFragment;
import com.example.easyday.FRAGMENT.ThemesFragment;
import com.example.easyday.ENTITY.Note;

public class TOOL {

    public static void FULL_SCREEN(Activity activity)
    {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN );
    }
    public static void setAnimation(View view, Context context,int animation)
    {
        view.startAnimation(AnimationUtils.loadAnimation(context,animation));
    }
    public static void setMoveScreen(final Context activity_1, Class<?> activity_2, int time)
    {
       final Intent intent = new Intent(activity_1,activity_2);
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               activity_1.startActivity(intent);
           }
       }, time);
    }
    public static void setTimeHideView(final View view,int time)
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        }, time);
    }
    public static void setTimeShowView(final View view,int time)
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, time);
    }
    public static List<Fragment> makeListFragment()
    {
        List<Fragment> listFragment = new ArrayList();
        Fragment home = new HomeFragment();
        Fragment me = new MeFragment();
        Fragment themes = new ThemesFragment();

        listFragment.add(themes);
        listFragment.add(home);
        listFragment.add(me);

        return listFragment;
    }

    public static byte[] convertBitMapToByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte [] result = byteArrayOutputStream.toByteArray();
        return result;
    }

    public static void showToastCustomDone(Context context)
    {
        Toast toast = new Toast(context);
        toast.setView(LayoutInflater.from(context).inflate(R.layout.layout_click_theme_done, null));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
    public static boolean emptyEditText(EditText editText)
    {
        if(editText.getText().toString().trim().isEmpty())
        {
            editText.setError("Please Fill out!");
            editText.requestFocus();
            return true;
        }
        else return false;
    }

    public static String convertBitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte [] b = byteArrayOutputStream.toByteArray();
        String result = Base64.encodeToString(b, 0, b.length, Base64.DEFAULT);

        return result;
    }

    public static Bitmap getBitmapFromImageView(ImageView imageView)
    {
        Bitmap bitmap = null;
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        bitmap = drawable.getBitmap();
        return bitmap;
    }

    public static Bitmap convertStringToBitmap(String url)
    {
        byte [] b = Base64.decode(url, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b,0 , b.length);
        return bitmap;
    }
    public static void setToast(Context context,String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static Note pushDataFromJsonToListNote(String idNote,String title,String content,List<ImageNote> imageNote,int level)
    {
        Note note = new Note();
        note.setTitle(title);
        note.setLevel(level);
        note.setContent(content);
        note.setIdNote(idNote);
        note.setImgNotes(imageNote);
        return note;
    }
}
