package com.example.chenguozhen.wcarbo.Fragment_weibo_detailed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.chenguozhen.wcarbo.Adapter.RecyclerViewAdapter.comment_list_adapter;
import com.example.chenguozhen.wcarbo.Bean.Comments;
import com.example.chenguozhen.wcarbo.Bean.Gson.error;
import com.example.chenguozhen.wcarbo.Bean.JSON.Comment;
import com.example.chenguozhen.wcarbo.module.base.BaseListFragment;
import com.example.chenguozhen.wcarbo.utils.BaseAsyncTask;
import com.example.chenguozhen.wcarbo.utils.JSONUitily;
import com.example.chenguozhen.wcarbo.utils.Utility;
import com.example.chenguozhen.wcarbo.wcarbo;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chenguozhen on 2018/1/18.
 */

public class Fragment_weibo_detailed_comments extends BaseListFragment{

    private String token;
    private String detial_idstr;
    private long max_id = 0;
    private String url = "https://api.weibo.com/2/comments/show.json?source=3867086258";
    private List<Comment> commentsList = new ArrayList<Comment>();
    private comment_list_adapter adapter;
    private CommentAsyncTask asyncTask;

    public static Fragment_weibo_detailed_comments newInstance(String detial_idstr){
        Bundle args = new Bundle();
        args.putString("detail",detial_idstr);

        Fragment_weibo_detailed_comments fragment_weibo_detailed_comments = new Fragment_weibo_detailed_comments();
        fragment_weibo_detailed_comments.setArguments(args);
        return fragment_weibo_detailed_comments;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = ((wcarbo)getActivity().getApplication()).getToken();
        detial_idstr = getArguments().getString("detail");
        Log.d("commentaa",detial_idstr);
        asyncTask = new CommentAsyncTask(commentsList,getActivity(),BaseAsyncTask.create);
        asyncTask.execute(url);
    }

    @Override
    public RecyclerView.Adapter adapter() {
        adapter = new comment_list_adapter(commentsList,Fragment_weibo_detailed_comments.this);
        return adapter;
    }

    @Override
    protected void ScrollListener_LoadMore() {
        asyncTask = new CommentAsyncTask(commentsList,getActivity(),BaseAsyncTask.scroll);
        asyncTask.execute(url);
    }

    @Override
    protected void SwipeRefresh_Refresh() {
        asyncTask = new CommentAsyncTask(commentsList,getActivity(),BaseAsyncTask.swipe);
        asyncTask.execute(url);
    }

    private class CommentAsyncTask extends BaseAsyncTask<Comment> {

        public CommentAsyncTask(List<Comment> DataList, FragmentActivity fragment, int type) {
            super(DataList, fragment, type);
        }

        @Override
        protected void notifyDataAdapterChanged() {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected List<Comment> cratevoid(String url, OkHttpClient client){
            List<Comment> commentList = new ArrayList<Comment>();
            Request request = Utility.budiler(url,max_id,token,detial_idstr);
            Log.d("commentaa",request.toString());
            try {
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                Comments comment = JSONUitily.comments(responseData);
                commentList.addAll(comment.getComments());
                max_id = comment.getNext_cursor();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commentList;
        }

        @Override
        protected List<Comment> scrollvoid(String url, OkHttpClient client) {
            List<Comment> commentList = new ArrayList<Comment>();
            Request request = Utility.budiler(url,max_id,token,detial_idstr);
            try {
                Response response = client.newCall(request).execute();
                String responseData  = response.body().string();
                Comments comment =JSONUitily.comments(responseData);
                List<Comment> comments = comment.getComments();
                if (max_id != 0){
                    commentList.addAll(comments);
                }
                max_id = comment.getNext_cursor();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return commentList;
        }

        @Override
        protected List<Comment> swipevoid(String url, OkHttpClient client) {
            max_id = 0;
            List<Comment> commentList = new ArrayList<Comment>();
            Request request = Utility.budiler(url,max_id,token,detial_idstr);
            try {
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                Comments comment = JSONUitily.comments(responseData);
                commentList.addAll(comment.getComments());
                max_id = comment.getNext_cursor();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commentList;
        }

    }

}
