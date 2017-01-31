package com.allnew.facebookgraphapi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ArrayList<Model> modelArrayList;
    private String afterPaging = "";
    private String nextLink = "";
    private RecyclerView recycleView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,1);
        AppEventsLogger.activateApp(this);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        recycleView = (RecyclerView) findViewById(R.id.recycleview);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        recycleView.setLayoutManager(layoutManager);
        loginButton.setVisibility(View.GONE);
        recycleView.setVisibility(View.GONE);
        loginButton.setReadPermissions("email");
        modelArrayList = new ArrayList<>();

        if (AccessToken.getCurrentAccessToken() != null) {
            callGraphAPI();
        } else {
            loginButton.setVisibility(View.VISIBLE);
        }

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    callGraphAPI();
                }
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    //    private void callGraphAPI() {
//        GraphRequest request = GraphRequest.newGraphPathRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/693849603993571",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        if (response != null) {
//                            addAllInArrayList(response);
//                        }
//                        Log.e("Responce", response.getJSONObject() + "");
//                    }
//                });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "videos{source,description,thumbnails{uri},length,comments.limit(1){from,message},likes.limit(0).summary(true)}");
//        request.setParameters(parameters);
//        request.executeAsync();
//    }
    public void callGraphAPI() {
        progressBar.setVisibility(View.VISIBLE);
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/693849603993571/videos",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.e("AA===========", response + "");
                        addAllInArrayList(response);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("pretty", "0");
        parameters.putString("fields", "source,description,thumbnails{uri},length,comments.limit(1){from,message},likes.limit(0).summary(true)");
        parameters.putString("limit", "125");
        if (!TextUtils.isEmpty(afterPaging)) {
            parameters.putString("after", afterPaging);
        }
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void addAllInArrayList(GraphResponse response) {
        nextLink = "";
        afterPaging = "";
        final JSONObject jsonObject = response.getJSONObject();
//        final JSONObject videoObject = jsonObject.optJSONObject("videos");
        final JSONArray dataArray = jsonObject.optJSONArray("data");
        final JSONObject pagingObject = jsonObject.optJSONObject("paging");
        final JSONObject cursorsObject = pagingObject.optJSONObject("cursors");
        if (pagingObject.has("next")) {
            nextLink = pagingObject.optString("next");
            afterPaging = cursorsObject.optString("after");
        }
        int k = 0;
        for (int i = 0; i < dataArray.length(); i++) {
            k++;
            final JSONObject object = dataArray.optJSONObject(i);
            final String source = object.optString("source");
            final String description = object.optString("description");
            final float length = Float.parseFloat(object.optString("length"));
            final float min = length / 60;
            final float sec = length % 60;
            final String strLength = min + ":" + sec;
            Log.e("DataArray+++++++++", i + "==============");
            final JSONObject thumbnailsObject = object.optJSONObject("thumbnails");
            final JSONArray thumbnailsArray = thumbnailsObject.optJSONArray("data");
            final JSONObject thumbnailsDataObject = thumbnailsArray.optJSONObject(0);
            final String imgUrl = thumbnailsDataObject.optString("uri");

            final ArrayList<CommentModel> commentModelArrayList = new ArrayList<>();
            if (object.has("comments")) {
                final JSONObject commentsObject = object.optJSONObject("comments");
                if (commentsObject.has("data")) {
                    final JSONArray commentsArray = commentsObject.optJSONArray("data");
                    for (int j = 0; j < commentsArray.length(); j++) {
                        final JSONObject commentsDataObject = commentsArray.optJSONObject(j);
                        final JSONObject fromCommentObject = commentsDataObject.optJSONObject("from");
                        final String name = fromCommentObject.optString("name");
                        final String message = commentsDataObject.optString("message");
                        final CommentModel commentModel = new CommentModel(name, message);
                        commentModelArrayList.add(commentModel);
                    }
                }
            }

            final JSONObject likesObject = object.optJSONObject("likes");
            final JSONObject likesSummaryArray = likesObject.optJSONObject("summary");
            final String likes = likesSummaryArray.optString("total_count");

            final Model model = new Model(description, imgUrl, source, strLength, likes, commentModelArrayList);
            if (k == 10) {
                modelArrayList.add(null);
                k = 0;
            }
            modelArrayList.add(model);
        }
        progressBar.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        recycleView.setVisibility(View.VISIBLE);
        final FBAdapter fbAdapter = new FBAdapter(modelArrayList,MainActivity.this,nextLink);
        recycleView.setAdapter(fbAdapter);
//        Log.e("Total++++++++", "" + modelArrayList.size());

//        if (!TextUtils.isEmpty(nextLink)) {
//            callGraphAPI();
//        }
    }

    public void callSimplaeActivity(final String text){
//        final Intent intent = new Intent(MainActivity.this,SimpleActivity.class);
//        intent.putExtra("text",text);
//        startActivity(intent);
    }

    public void callNewWeb(final String url) {
        Uri uri = Uri.parse(url);

// create an intent builder
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

// Begin customizing
// set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

// set start and exit animations
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

// build custom tabs intent
        CustomTabsIntent customTabsIntent = intentBuilder.build();

// launch the url
        customTabsIntent.launchUrl(this, uri);
    }

}
