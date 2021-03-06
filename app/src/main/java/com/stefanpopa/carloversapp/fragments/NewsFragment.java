package com.stefanpopa.carloversapp.fragments;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.controller.SimpleXMLParser;
import com.stefanpopa.carloversapp.model.Entry;
import com.stefanpopa.carloversapp.model.RssLink;
import com.stefanpopa.carloversapp.ui.FeedRecyclerAdapter;
import com.stefanpopa.carloversapp.util.JsonUtil;
import com.stefanpopa.carloversapp.util.UserApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private int rssLoadedNumber;
    private int rssTotalNumber;
    private SharedPreferences sharedPref;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private volatile List<Entry> finalEntryList;
    private FloatingActionButton floatingActionButton;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        UserApi.getInstance().getUsername(new UserApi.UsernameCallback() {
            @Override
            public void isUsernameExist(String username) {
                Log.d("NEWS_FRAGMENT", "Username: " + username);
                if (username != null) {
                    sharedPref = getActivity().getSharedPreferences("MY_PREFS_" + username, Context.MODE_PRIVATE);
                    try {
                        httpRequest();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(0).setChecked(true);
        Log.d("PROFILE_FRAGMENT", "onResume called: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rssLoadedNumber = 0;
        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupFilterFeed();
            }
        });
        return view;
    }

    private void httpRequest() throws JSONException {
        OkHttpClient client = new OkHttpClient();
        finalEntryList = new ArrayList<>();
        String url = "";
        String imageLinkTagName = "";
        Integer imageLinkTagPosition = 0;
        String rssName = "";
        JSONObject root = new JSONObject(JsonUtil.JSON_STRING);
        JSONArray jsonArray = root.getJSONArray("rssFeeds");
        List<RssLink> rssLinkList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            rssName = object.getString("rssName");
            if (sharedPref.getBoolean(rssName, JsonUtil.DEFAULT_VALUE) == JsonUtil.DEFAULT_VALUE) {
                continue;
            }
            url = object.getString("urlLink");
            imageLinkTagName = object.getString("imageLinkTagName");
            imageLinkTagPosition = object.getInt("imageLinkTagPosition");
            RssLink rssLink = new RssLink(url, imageLinkTagName, imageLinkTagPosition, rssName);
            rssLinkList.add(rssLink);
        }

        rssTotalNumber = rssLinkList.size();

        for (int i = 0; i < rssLinkList.size(); i++) {
            Log.d("RSS_LINK_LIST", rssLinkList.get(i).toString());
            String finalRssName = rssLinkList.get(i).getRssName();
            String finalUrl = rssLinkList.get(i).getUrl();
            String finalImageLinkTagName = rssLinkList.get(i).getImageLinkTagName();
            Integer finalImageLinkTagPosition = rssLinkList.get(i).getImageLinkTagPosition();

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = response.body().string();
                        SimpleXMLParser simpleXMLParser = new SimpleXMLParser(finalRssName, myResponse, finalImageLinkTagName, finalImageLinkTagPosition);
                        List<Entry> entryList = null;
                        try {
                            entryList = simpleXMLParser.parseXML();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        }
                        List<Entry> finalEntryList1 = entryList;
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onDataLoaded(finalEntryList1);
                            }
                        });
                    }
                }
            });
        }
    }

    private void onDataLoaded(List<Entry> entryList) {

        rssLoadedNumber++;
        finalEntryList.addAll(entryList);
        if (rssLoadedNumber == rssTotalNumber) {
            Collections.shuffle(finalEntryList);
            String parsedXml = "";
            for (Entry entry : finalEntryList) {
                parsedXml += entry.toString() + "\n";
            }
            feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), finalEntryList);
            feedRecyclerAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
            recyclerView.setAdapter(feedRecyclerAdapter);
            feedRecyclerAdapter.notifyDataSetChanged();
            Log.d("RSS_TOTAL_DATA", parsedXml);
        }
    }

    private void popupFilterFeed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your news sites");
        builder.setCancelable(false);

        Map<String[], Boolean[]> rssPrefs = JsonUtil.getRssPref(sharedPref);
        boolean[] checkedItems = new boolean[0];
        String[] rssNames = new String[0];

        for (Map.Entry<String[], Boolean[]> entry : rssPrefs.entrySet()) {
            rssNames = entry.getKey();
            checkedItems = toPrimitiveArray(Arrays.asList(entry.getValue()));
        }
        boolean[] finalCheckedItems = checkedItems;
        final boolean[][] checkedItemsTmp = {Arrays.copyOf(checkedItems, checkedItems.length)};

        builder.setMultiChoiceItems(rssNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItemsTmp[0][which] = isChecked;
            }
        });

        String[] finalRssNames = rssNames;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePreferences(finalRssNames, checkedItemsTmp[0]);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updatePreferences(String[] rssNames, boolean[] checkedItems) {
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int i = 0; i < rssNames.length; i++) {
            editor.putBoolean(rssNames[i], checkedItems[i]);
        }
        editor.apply();
    }

    private boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }
}