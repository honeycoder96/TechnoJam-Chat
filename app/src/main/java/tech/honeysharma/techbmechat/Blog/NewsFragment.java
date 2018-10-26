package tech.honeysharma.techbmechat.Blog;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.honeysharma.techbmechat.Models.Article;
import tech.honeysharma.techbmechat.Models.Example;
import tech.honeysharma.techbmechat.R;
import tech.honeysharma.techbmechat.Retrofit.APIService;
import tech.honeysharma.techbmechat.Retrofit.APIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private APIService mAPIService;
    private Example example = new Example();
    private List<Article> myArticlesList = new ArrayList<>();
    private NewsAdapter mAdapter;
    private RecyclerView recyclerView;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false);
        recyclerView.setLayoutManager(layoutManager);



        mAPIService = APIUtils.getAPIService();

        loadTechNews();

       return view;
    }

    private void loadTechNews() {

        mAPIService.postProblem("in" , "technology" , "f5c378275ae2436e96fc7eb61924cc1d").enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if (response.isSuccessful()){


                    example = response.body();
                    myArticlesList = example.getArticles();

                    Log.i("NewsFragment" , "List  : " + example.getArticles() );


                    Log.i("NewsFragment" , "Sending list size to adapter : " + myArticlesList.size() );

                    Toast.makeText(getActivity(), "Size : " + myArticlesList.size(), Toast.LENGTH_SHORT).show();
                    mAdapter = new NewsAdapter(getContext(), myArticlesList, new NewsAdapter.OnNewsItemClickListener() {
                        @Override
                        public void OnNewsItemClicked(NewsAdapter.NewsViewHolder holder, int position) {



                            Intent i = new Intent(getActivity() , DetailedNewsActivity.class);
                            i.putExtra("newsUrl" , myArticlesList.get(position).getUrl());

                            startActivity(i);

                        }
                    });

                    recyclerView.setAdapter(mAdapter);




                }

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {


                Log.i("NewsFragment" , "Inside Failure : " + t.getMessage());
            }
        });


    }

}
