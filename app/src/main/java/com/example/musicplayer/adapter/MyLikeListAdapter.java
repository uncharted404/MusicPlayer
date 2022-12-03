package com.example.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayer.R;
import com.example.musicplayer.factory.SingletonFactory;
import com.example.musicplayer.pojo.Music;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyLikeListAdapter extends RecyclerView.Adapter<MyLikeListAdapter.InnorHolder>{

    private List<Music> musicList;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListener onItemClickDeleteListener;

    public MyLikeListAdapter(List<Music> musicList){
        this.musicList = musicList;
    }

    @Override
    public InnorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_my_like, parent, false);

        return new InnorHolder(view);
    }

    @Override
    public void onBindViewHolder(InnorHolder holder, int position) {
        int mPosition = position;
        Music music = musicList.get(mPosition);
        holder.setMusic(music);

        SingletonFactory.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickDeleteListener != null){
                            onItemClickDeleteListener.onItemClick(mPosition, music);
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null){
                            onItemClickListener.onItemClick(mPosition, music);
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        if (musicList != null){
            return musicList.size();
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public void setOnItemClickDeleteListener(OnItemClickListener listener){
        this.onItemClickDeleteListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position, Music music);
    }

    public class InnorHolder extends RecyclerView.ViewHolder{
        private final ImageView image;
        private final TextView title;
        private final TextView author;
        private final View itemView;
        private final Button btnDelete;

        public InnorHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            image = itemView.findViewById(R.id.item_image_my_like);
            title = itemView.findViewById(R.id.item_title_my_like);
            author = itemView.findViewById(R.id.item_author_my_like);
            btnDelete = itemView.findViewById(R.id.btn_delete);

        }

        public void setMusic(Music music){

            Picasso.with(itemView.getContext()).load(music.getImageUrl()).into(image);
            title.setText(music.getTitle());
            author.setText(music.getAuthor());
        }
    }
}
