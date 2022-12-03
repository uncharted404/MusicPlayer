package com.example.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.musicplayer.R;
import com.example.musicplayer.pojo.Music;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MySearchListAdapter extends ArrayAdapter<Music> {
    private int recourceId;
    private Context context;

    public MySearchListAdapter(@NonNull Context context, int resource, @NonNull List<Music> objects) {
        super(context, resource, objects);
        this.recourceId = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Music musicInfo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(recourceId,parent,false);

        ImageView image = view.findViewById(R.id.item_image);
        TextView title = view.findViewById(R.id.item_title);
        TextView author = view.findViewById(R.id.item_author);

        Picasso.with(context).load(musicInfo.getImageUrl()).into(image);
        title.setText(musicInfo.getTitle());
        author.setText(musicInfo.getAuthor());

        return view;
    }
}
