package tech.honeysharma.techbmechat.Blog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import tech.honeysharma.techbmechat.Models.Article;
import tech.honeysharma.techbmechat.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

   Context context;
   List<Article> articleList;
   OnNewsItemClickListener onNewsItemClickListener;

    public NewsAdapter(Context context  , List<Article> articleList, OnNewsItemClickListener onNewsItemClickListener){

        this.context = context;
        this.articleList = articleList;
        this.onNewsItemClickListener = onNewsItemClickListener;

    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.news_single_list ,viewGroup, false);

        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder newsViewHolder, final int position) {

        String dateAndTime = articleList.get(position).getPublishedAt();
        String[] date = dateAndTime.split("T", 2);

        newsViewHolder.newsPublishedDate.setText(date[0]);
        newsViewHolder.newsTitle.setText(articleList.get(position).getTitle());
        newsViewHolder.newsDescription.setText(articleList.get(position).getDescription());

        Glide.with(context).load(articleList.get(position).getUrlToImage()).into(newsViewHolder.newsImage);

        newsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewsItemClickListener.OnNewsItemClicked(newsViewHolder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public interface OnNewsItemClickListener {
        void OnNewsItemClicked(NewsViewHolder holder, int position);
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{

        ImageView newsImage;
        TextView newsTitle , newsDescription , newsPublishedDate;


        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            newsImage = itemView.findViewById(R.id.newsImage);
            newsDescription = itemView.findViewById(R.id.newsDescription);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsPublishedDate = itemView.findViewById(R.id.newsPublishedDate);
        }
    }
}
