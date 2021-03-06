package com.example.chenguozhen.wcarbo.Fragment_weibo_detailed;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.chenguozhen.wcarbo.Adapter.RecyclerViewAdapter.comment_list_adapter;
import com.example.chenguozhen.wcarbo.Bean.Comments;
import com.example.chenguozhen.wcarbo.Bean.JSON.Comment;
import com.example.chenguozhen.wcarbo.Fragment_weibo_profile.CommentListFragment;
import com.example.chenguozhen.wcarbo.Interface.HttpListener;
import com.example.chenguozhen.wcarbo.Interface.RecyclerViewItemClickLisntner;
import com.example.chenguozhen.wcarbo.module.base.BaseListFragment;
import com.example.chenguozhen.wcarbo.AsyncTask.BaseAsyncTask;
import com.example.chenguozhen.wcarbo.utils.HttpUtil;
import com.example.chenguozhen.wcarbo.utils.JSONUitily;
import com.example.chenguozhen.wcarbo.utils.Utility;
import com.example.chenguozhen.wcarbo.widget.CommentChoiceFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chenguozhen on 2018/1/18.
 */

public class Fragment_weibo_detailed_comments extends BaseListFragment implements HttpListener,RecyclerViewItemClickLisntner{

    private String detial_idstr;
    private long max_id = 0;
    private String url = "https://api.weibo.com/2/comments/show.json?source=3867086258";
    private List<Comment> commentsList = new ArrayList<Comment>();
    private comment_list_adapter adapter;
    private Request request = null;
    private ProgressDialog dialog;
    private Comments mComment;

    public static Fragment_weibo_detailed_comments newInstance(String detial_idstr){
        Bundle args = new Bundle();
        args.putString("detail",detial_idstr);

        Fragment_weibo_detailed_comments fragment_weibo_detailed_comments = new Fragment_weibo_detailed_comments();
        fragment_weibo_detailed_comments.setArguments(args);
        return fragment_weibo_detailed_comments;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        commentsList.clear();
    }

    @Override
    public RecyclerView.Adapter adapter() {
        adapter = new comment_list_adapter(commentsList,Fragment_weibo_detailed_comments.this,this);
        return adapter;
    }

    @Override
    protected void Create_Content(String token) {
        detial_idstr = getArguments().getString("detail");
        dialog = ProgressDialog.show(getContext(),"","加载中，请稍后……");
        request =Utility.budiler(url,max_id,token,detial_idstr);
        send(request);
    }

    @Override
    protected void ScrollListener_LoadMore(String token) {
        if (max_id != 0) {
            request =Utility.budiler(url,max_id,token,detial_idstr);
            send(request);
        }
    }

    @Override
    protected void SwipeRefresh_Refresh(String token) {
        max_id = 0;
        request =Utility.budiler(url,max_id,token,detial_idstr);
        send(request);
        commentsList.clear();
    }

    @Override
    public void ItemClick(int position) {
        String cid = commentsList.get(position).getIdstr();
        String id = mComment.getStatus().getIdstr();
        CommentChoiceFragment choiceFragment = CommentChoiceFragment.newInstance(id,cid, CommentListFragment.me);
        choiceFragment.show(getFragmentManager());
    }

    @Override
    public void success(String responseData) {
        final Comments comment = JSONUitily.bytomentions(responseData);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mComment = comment;
                if (comment.getComments() != null){
                    commentsList.addAll(comment.getComments());
                    max_id = comment.getNext_cursor();
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public void failed() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),"网络出现问题",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void send(Request request){
        HttpUtil httpUtil = new HttpUtil(this);
        httpUtil.Data_update(request);
    }



}
