package com.erickogi14gmail.demo_news_api_android1.News;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.erickogi14gmail.demo_news_api_android1.MainActivity;
import com.erickogi14gmail.demo_news_api_android1.R;
import com.erickogi14gmail.demo_news_api_android1.RecyclerViewUtils.HidingScrollListener;
import com.erickogi14gmail.demo_news_api_android1.RecyclerViewUtils.RecyclerTouchListener;
import com.erickogi14gmail.demo_news_api_android1.Utils.ArticlesJsonParser;
import com.erickogi14gmail.demo_news_api_android1.Utils.ArticlesModel;
import com.erickogi14gmail.demo_news_api_android1.Utils.ArticlesModelAdapter;
import com.erickogi14gmail.demo_news_api_android1.Utils.Constants;
import com.erickogi14gmail.demo_news_api_android1.Utils.SorcesModelAdapter;
import com.erickogi14gmail.demo_news_api_android1.Utils.SourcesJsonParser;
import com.erickogi14gmail.demo_news_api_android1.Utils.SourcesModel;

import java.util.ArrayList;

/**
 * Created by kimani kogi on 4/22/2017.
 */

public class fragment_tech_news extends android.support.v4.app.Fragment {
    static RequestQueue queue;
    static Context context;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    static RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout swipe_refresh_layout;
    RecyclerView recyclerView_vertical;
    RecyclerView recyclerView_horizontal;
    static View view;
    static View viewSource;
    private boolean isListView;
    FloatingActionButton fab;
    ArrayList<SourcesModel> sources;
    static ArrayList<ArticlesModel> articles;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);

        view = inflater.inflate(R.layout.fragment_all_news, container, false);

        recyclerView_horizontal = (RecyclerView) view.findViewById(R.id.all_news_horizontal_recyclerView);
        recyclerView_vertical = (RecyclerView) view.findViewById(R.id.all_news_vertical_recyclerView);
        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
        swipe_refresh_layout.setBackgroundResource(android.R.color.white);
        swipe_refresh_layout.setColorSchemeResources(android.R.color.white, android.R.color.holo_purple, android.R.color.white);

        swipe_refresh_layout.setRefreshing(true);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh_layout.setRefreshing(true);
                getRecyclerView_sources();

            }
        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        recyclerView_horizontal.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView_horizontal, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                if (viewSource != null) {
                    viewSource.setBackgroundColor(Color.WHITE);
                }
                viewSource = view;
                view.setBackgroundColor(Color.RED);
                swipe_refresh_layout.setRefreshing(true);
                getRecyclerView_articles(sources.get(position).getId());


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        recyclerView_vertical.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView_vertical, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Intent intent=new Intent(getActivity(),FullNews.class);
                intent.putExtra(Constants.KEY_URL_TAG,articles.get(position).getUrl());
                intent.putExtra(Constants.KEY_URL_TO_IMAGE_TAG,articles.get(position).getUrlToImage());

                startActivity(intent);



            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
         recyclerView_vertical.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
        isListView = true;
        getRecyclerView_sources();

        return view;
    }

    private void hideViews() {

        //   Toast.makeText(context, "scrolledd", Toast.LENGTH_SHORT).show();
//         FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
        int fabBottomMargin = 45;
        fab.animate().translationY(fab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        //  mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    public void setLayout(boolean isListView) {
//        if (isListView) {
//          //  mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
//          // this. mStaggeredLayoutManager.setSpanCount(2);
//
//            this.isListView = false;
//        } else {
//           // mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
//
//           this. isListView = true;
//        }
        setRecyclerView_articles(articles, isListView);
    }

    void getRecyclerView_sources() {


        requestDataSources(Constants.SOURCES_END_POINT+"?category="+Constants.KEY_CATEGORY_TECH);
    }

    public void setRecyclerView_sources(ArrayList<SourcesModel> sourcesModelArrayList) {
        SorcesModelAdapter adapter;
        this.sources = sourcesModelArrayList;
        adapter = new SorcesModelAdapter(sourcesModelArrayList, getContext());
       SourcesModel model = sourcesModelArrayList.get(0);

        adapter.notifyDataSetChanged();
        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        recyclerView_horizontal = (RecyclerView) view.findViewById(R.id.all_news_horizontal_recyclerView);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());


        recyclerView_horizontal.setLayoutManager(mStaggeredLayoutManager);
        recyclerView_horizontal.setItemAnimator(new DefaultItemAnimator());


        recyclerView_horizontal.setAdapter(adapter);

        swipe_refresh_layout.setRefreshing(false);

       getRecyclerView_articles(model.getId());
    }

    void getRecyclerView_articles(String name) {

        requestDataArticles(Constants.ARTICLES_END_POINT + name + "&apiKey=" + Constants.API_KEY);

    }

    public void setRecyclerView_articles(ArrayList<ArticlesModel> articlesModelArrayList, boolean isListView) {
        articles = articlesModelArrayList;
        ArticlesModelAdapter adapter;
        adapter = new ArticlesModelAdapter(articlesModelArrayList, getContext());
        adapter.notifyDataSetChanged();
        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        recyclerView_vertical = (RecyclerView) view.findViewById(R.id.all_news_vertical_recyclerView);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        if (isListView) {

            mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

            this.isListView = false;
        } else {
            mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);


            this.isListView = true;
        }


        recyclerView_vertical.setLayoutManager(mStaggeredLayoutManager);

        recyclerView_vertical.setItemAnimator(new DefaultItemAnimator());


        recyclerView_vertical.setAdapter(adapter);
        swipe_refresh_layout.setRefreshing(false);
    }

    public void requestDataSources(String uri) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<SourcesModel> sourcesModelArrayList;

                        if (response != null || !response.isEmpty()) {

                            sourcesModelArrayList = SourcesJsonParser.parseData(response,Constants.ALL_TECH_SOURCES_PARSING_CODE);

                            setRecyclerView_sources(sourcesModelArrayList);

                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        swipe_refresh_layout.setRefreshing(false);


                    }
                });
        queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);
        context = getContext();
    }

    public Context getApplicationContext() {
        Context applicationContext = getContext();
        context = applicationContext;
        return applicationContext;
    }

    public void requestDataArticles(String uri) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ArrayList<ArticlesModel> articlesModelArrayList;
//                        if (a == Constants.KEY_SOURCES_REQUEST) {
//                            if (response != null || !response.isEmpty()) {
//
//                                sourcesModelArrayList = SourcesJsonParser.parseData(response);
//                                fragment_all_news f = new fragment_all_news();
//                                f.setRecyclerView_sources(sourcesModelArrayList);
//
//                            }
//                        } else if (a == Constants.KEY_ARTICLE_REQUEST) {


                        if (response != null || !response.isEmpty()) {

                            articlesModelArrayList = ArticlesJsonParser.parseData(response);

                            //fragment_all_news f = new fragment_all_news();
                            setRecyclerView_articles(articlesModelArrayList, false);


                        }

                        //  }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                        swipe_refresh_layout.setRefreshing(false);
                    }
                });
        // queue = Volley.newRequestQueue(fragment_all_news.context);
        queue.add(stringRequest);


    }

}