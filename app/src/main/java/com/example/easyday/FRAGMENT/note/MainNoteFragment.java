package com.example.easyday.FRAGMENT.note;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.easyday.ADAPTER.RecyclerAdapterMainNote;
import com.example.easyday.CONTROL.HelpersService;
import com.example.easyday.CONTROL.SendListNote;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.Note;
import com.example.easyday.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainNoteFragment extends Fragment implements RecyclerAdapterMainNote.sendPositionNoteDelete {
    RecyclerAdapterMainNote recyclerAdapterMainNote;
    public static List<Note> listNotes;
    public static List<Note> listNotesTempAllList;
    RecyclerView recyclerNotes;
    TextInputEditText edt_search_note;
    FloatingActionButton bt_addNode;
    MediaPlayer mediaPlayer;
    public static boolean check = false;
    Chip chip_low, chip_high;
    ChipGroup chipGroup;
    final static String TAG = "MainNoteFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.main_note_layout, null);
        listNotesTempAllList = new ArrayList<>();
        mapping(view);
        initComponents();
        SendListNote sendListNote = ViewModelProviders.of((FragmentActivity) getContext()).get(SendListNote.class);
        sendListNote.getListNote().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> noteList) {
                listNotes.clear();
                    if(check) {

                        listNotes.addAll(SetNoteFragment.noteList);
                    }
                   else{
                        listNotes.addAll(noteList);
                    }
                    listNotesTempAllList.clear();
                    listNotesTempAllList.addAll(noteList);
                    Log.d("SetNoteFragment", "On Create View Main");
                    recyclerAdapterMainNote.notifyDataSetChanged();
            }
        });
        return view;
    }
    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(!check)
        HelpersService.getNoteFromServiceByIds(getContext(), HelpersService.getUrlGetNoteFromService(), FirebaseAuth.getInstance().getCurrentUser().getUid());

        Log.d("SetNoteFragment","On Created View");
        edt_search_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("SetNoteFragment","Text Change: " + listNotesTempAllList.size()+"");
                filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        controlSortList();
        super.onViewCreated(view, savedInstanceState);

    }

    private void controlSortList() {
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (chip_high.isChecked()) {
                    if(listNotes.size()>0)
                        Collections.sort(listNotes, new Comparator<Note>() {
                            @Override
                            public int compare(Note o1, Note o2) {
                                if(o1.getLevel()>o2.getLevel())
                                    return -1;else
                                if(o1.getLevel()<o2.getLevel())
                                    return 1;else
                                    return 0;
                            }
                        });
                    recyclerAdapterMainNote.notifyDataSetChanged();
                } else if (chip_low.isChecked()) {
                    if(listNotes.size()>0)
                        Collections.sort(listNotes, new Comparator<Note>() {
                            @Override
                            public int compare(Note o1, Note o2) {
                                if(o1.getLevel()>o2.getLevel())
                                    return 1;else
                                if(o1.getLevel()<o2.getLevel())
                                    return -1;else
                                    return 0;
                            }
                        });
                    recyclerAdapterMainNote.notifyDataSetChanged();
                }
                Log.d(getTAG(),"Size listnote: " + listNotes.size()+"");
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final MainNoteFragmentDirections.ActionAddNote action = MainNoteFragmentDirections.actionAddNote();
        bt_addNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(getContext(), R.raw.click);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Navigation.findNavController(getView()).navigate(action);
                    }
                }, 1000);
                mediaPlayer.start();

            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }catch (Exception e)
        {

        }
    }


    private void initComponents() {
        listNotes = new ArrayList<>();
        recyclerAdapterMainNote = new RecyclerAdapterMainNote(listNotes, getContext(),this);
        recyclerNotes.setHasFixedSize(true);
        StaggeredGridLayoutManager layout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layout.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerNotes.setLayoutManager(layout);
        recyclerNotes.setAdapter(recyclerAdapterMainNote);

    }

    private void mapping(View view) {
        edt_search_note = view.findViewById(R.id.edt_search_note);
        recyclerNotes = view.findViewById(R.id.recyclerViewNotes);
        bt_addNode = view.findViewById(R.id.fltbt_AddNote);
        chip_low = view.findViewById(R.id.chip_low);
        chip_high = view.findViewById(R.id.chip_high);
        chipGroup = view.findViewById(R.id.chip_priority_group);
    }

    private void filter(CharSequence s) {
        Log.d(getTAG(),"ListnoteTempAllist: " + listNotesTempAllList.size()+"");
        listNotes.clear();
        if (s.toString().length() == 0) {
            listNotes.addAll(listNotesTempAllList);
        } else {
            for (Note note : listNotesTempAllList)
                if (note.getTitle().toLowerCase().trim().contains(s.toString().toLowerCase()))
                    listNotes.add(note);
        }
        Log.d(getTAG(),"ListnoteTempAllist: " + listNotesTempAllList.size()+"");
        recyclerAdapterMainNote.notifyDataSetChanged();
    }

    public static String getTAG() {
        return TAG;
    }

    @Override
    public void send(int position) {
        listNotesTempAllList.clear();
        listNotesTempAllList.addAll(SetNoteFragment.noteList);
        listNotesTempAllList.remove(position);
        recyclerAdapterMainNote.notifyDataSetChanged();
    }
}
