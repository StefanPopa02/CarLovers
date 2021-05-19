package com.stefanpopa.carloversapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.Entry;

import java.util.List;


public class FeedRecyclerAdapter extends RecyclerView.Adapter<com.stefanpopa.carloversapp.ui.FeedRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Entry> entryList;
    private AlertDialog alertDialog;
    private AlertDialog.Builder dialogBuilder;

    public FeedRecyclerAdapter(Context context, List<Entry> entryList) {
        this.context = context;
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public com.stefanpopa.carloversapp.ui.FeedRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull com.stefanpopa.carloversapp.ui.FeedRecyclerAdapter.ViewHolder holder, int position) {
        Entry entry = entryList.get(position);
        holder.postedBy.setText("Posted by " + entry.getRssName());
        holder.title.setText(entry.getTitle());
        String imageUrl = entry.getImageUrl();
        Picasso.get().load(imageUrl).placeholder(R.drawable.no_image).fit().into(holder.feedImage);
        holder.readMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = entry.getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, postedBy;
        public Button readMoreBtn;
        public ImageView feedImage;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.feed_title);
            postedBy = itemView.findViewById(R.id.feed_post_name);
            readMoreBtn = itemView.findViewById(R.id.feed_link_to_url);
            feedImage = itemView.findViewById(R.id.feed_image);

        }
    }
}
