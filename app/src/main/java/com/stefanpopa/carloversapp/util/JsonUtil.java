package com.stefanpopa.carloversapp.util;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    public final static boolean DEFAULT_VALUE = false;
    public final static String JSON_STRING = "{\n" +
            "  \"rssFeeds\": [\n" +
            "    {\n" +
            "      \"rssName\": \"AutoBlog\",\n" +
            "      \"urlLink\": \"https://www.autoblog.com/rss.xml\",\n" +
            "      \"imageLinkTagName\": \"enclosure\",\n" +
            "      \"imageLinkTagPosition\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"Auto-data\",\n" +
            "      \"urlLink\": \"https://www.auto-data.net/ro/rss.php\",\n" +
            "      \"imageLinkTagName\": \"enclosure\",\n" +
            "      \"imageLinkTagPosition\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"Motor1\",\n" +
            "      \"urlLink\": \"https://www.motor1.com/rss/news/all/\",\n" +
            "      \"imageLinkTagName\": \"enclosure\",\n" +
            "      \"imageLinkTagPosition\": \"2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"Car and Driver\",\n" +
            "      \"urlLink\": \"https://www.caranddriver.com/rss/all.xml/\",\n" +
            "      \"imageLinkTagName\": \"content\",\n" +
            "      \"imageLinkTagPosition\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"Paultan\",\n" +
            "      \"urlLink\": \"https://paultan.org/feed/\",\n" +
            "      \"imageLinkTagName\": \"enclosure\",\n" +
            "      \"imageLinkTagPosition\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"MotorTrend\",\n" +
            "      \"urlLink\": \"https://www.motortrend.com/feed/\",\n" +
            "      \"imageLinkTagName\": \"image\",\n" +
            "      \"imageLinkTagPosition\": \"-1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"CarAdvice\",\n" +
            "      \"urlLink\": \"https://www.caradvice.com.au/feed/\",\n" +
            "      \"imageLinkTagName\": \"enclosure\",\n" +
            "      \"imageLinkTagPosition\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"IHS Markit\",\n" +
            "      \"urlLink\": \"https://ihsmarkit.com/BlogFeed.ashx?i=Automotive\",\n" +
            "      \"imageLinkTagName\": \"enclosure\",\n" +
            "      \"imageLinkTagPosition\": \"2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"Mechanic Base\",\n" +
            "      \"urlLink\": \"https://mechanicbase.com/feed/\",\n" +
            "      \"imageLinkTagName\": \"NOT EXISTING\",\n" +
            "      \"imageLinkTagPosition\": \"0\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"rssName\": \"Concept Carz\",\n" +
            "      \"urlLink\": \"http://www.conceptcarz.com/rss/default.xml\",\n" +
            "      \"imageLinkTagName\": \"image\",\n" +
            "      \"imageLinkTagPosition\": \"-1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static Map<String[], Boolean[]> getRssPref(SharedPreferences sharedPref) {
        JSONObject root = null;
        List<String> rssNames = new ArrayList<>();
        List<Boolean> rssChoices = new ArrayList<>();
        String[] stringArray = new String[0];
        Boolean[] choicesArray = new Boolean[0];
        String rssName;
        Map<String[], Boolean[]> rssFilter = null;
        try {
            root = new JSONObject(JSON_STRING);
            JSONArray jsonArray = root.getJSONArray("rssFeeds");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                rssName = object.getString("rssName");
                rssNames.add(rssName);
                rssChoices.add(sharedPref.getBoolean(rssName, com.stefanpopa.carloversapp.util.JsonUtil.DEFAULT_VALUE));
            }
            stringArray = rssNames.toArray(new String[0]);
            choicesArray = rssChoices.toArray(new Boolean[0]);
            for (int i = 0; i < stringArray.length; i++) {
                Log.d("JSON_UTIL_ARRAY", "stringArray " + stringArray[i]);
                Log.d("JSON_UTIL_ARRAY", "choicesArray " + choicesArray[i]);
            }

            rssFilter = new HashMap<>();
            rssFilter.put(stringArray, choicesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rssFilter;
    }
}
