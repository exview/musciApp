package com.leave.music.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leave.music.R;
import com.leave.music.fragment.dummy.DummyContent;
import com.leave.music.fragment.dummy.DummyContent.DummyItem;
import com.leave.music.javabean.Song;
import com.leave.music.utils.SongUtils;

import java.util.List;

import static com.leave.music.activity.BaseActivity.musics;
import static com.leave.music.activity.MainActivity.manager;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnLocalFragmentInteractionListener}
 * interface.
 */
public class LocalFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnLocalFragmentInteractionListener mListener;

    private List<Song> songs;
    private ItemRecyclerViewAdapter adapter;
    private mReceiver receiver;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocalFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LocalFragment newInstance(List<Song> songs) {
        LocalFragment fragment = new LocalFragment();
        fragment.songs = songs;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        receiver = new mReceiver();
        manager.registerReceiver(receiver,new IntentFilter("android.intent.finishFind"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new ItemRecyclerViewAdapter(songs, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocalFragmentInteractionListener) {
            mListener = (OnLocalFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.unregisterReceiver(receiver);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLocalFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLocalFragmentInteraction(Song song, int position);
    }

    class mReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            songs.clear();
            songs.addAll(new SongUtils().getSongList());
            adapter.notifyDataSetChanged();
            musics.clear();
            musics.addAll(new SongUtils().getSongList());
        }
    }
}
