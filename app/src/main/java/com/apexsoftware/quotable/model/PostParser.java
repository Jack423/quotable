package com.apexsoftware.quotable.model;
//Created by Jack Butler on 4/2/2019

import com.apexsoftware.quotable.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostParser {

    public Post parse(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        String id = jsonObject.optString("objectId");
        String quote = jsonObject.optString("quote");
        String description = jsonObject.optString("description");
        String tags = jsonObject.optString("tags");
        long createdDate = jsonObject.optLong("createdDate");
        String authorId = jsonObject.optString("authorId");
        long commentsCount = jsonObject.optLong("commentsCount");
        long likesCount = jsonObject.optLong("likesCount");
        long watchersCount = jsonObject.optLong("watchersCount");
        boolean hasReport = jsonObject.optBoolean("hasReport");

        if (id != null
                && quote != null
                && description != null
                && tags != null
                && createdDate > 0
                && authorId != null
                && commentsCount >= 0
                && watchersCount >= 0
                && !hasReport) {
            return new Post(id, quote, description, tags, createdDate, authorId, commentsCount, likesCount, watchersCount, hasReport);
        }

        return null;
    }

    public class SearchResultsJsonParser {
        private final String TAG = PostParser.class.getSimpleName();

        private PostParser postParser = new PostParser();
        public List<Post> parseResults(JSONObject jsonObject) {
            if (jsonObject == null) {
                return null;
            }

            List<Post> results = new ArrayList<>();
            JSONArray hits = jsonObject.optJSONArray("hits");

            if (hits == null) {
                return null;
            }

            for (int i = 0; i < hits.length(); i++) {
                JSONObject hit = hits.optJSONObject(i);
                if (hit == null) {
                    continue;
                }
                Post post = postParser.parse(hit);
                if (post == null) {
                    continue;
                }
                results.add(post);
            }

            return results;
        }
    }
}
