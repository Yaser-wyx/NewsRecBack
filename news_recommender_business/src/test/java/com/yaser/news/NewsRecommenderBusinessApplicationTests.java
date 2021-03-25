package com.yaser.news;

import com.mongodb.client.MongoClients;
import com.yaser.news.dataEntity.News;
import com.yaser.news.dataEntity.NewsChannel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SpringBootTest
class NewsRecommenderBusinessApplicationTests {
    MongoOperations mongoOpsSource = new MongoTemplate(MongoClients.create(), "news_db");
    MongoOperations mongoOpsTarget = new MongoTemplate(MongoClients.create(), "news_recommender");

    @Test
    void copyDB() {
        List<News> newsList = mongoOpsSource.find(new Query(), News.class);
        mongoOpsTarget.dropCollection(News.class);
        newsList.forEach(news -> {
            Random random = new Random();
            if (news.getChannelName() != null && news.getChannelName().contains("新浪")) {
                news.setChannelName(news.getChannelName().replace("新浪", ""));
            }
            news.setViewCount(random.nextInt(1000));
            news.setCommentTotal(random.nextInt(500));
            mongoOpsTarget.insert(news);
        });
    }

    @Test
    void clearData() {
        List<News> newsList = mongoOpsTarget.find(new Query(), News.class);
        newsList.forEach(news -> {
            if (news.getChannelName() == null) {
                news.setChannelName("其它");
                mongoOpsTarget.save(news);
            }
        });
    }

    @Test
    void createChannelTable() {
        if (mongoOpsTarget.collectionExists(NewsChannel.class)) {
            mongoOpsTarget.dropCollection(NewsChannel.class);
        }
        mongoOpsTarget.createCollection(NewsChannel.class);
        List<News> newsList = mongoOpsTarget.find(new Query(), News.class);
        List<NewsChannel> channelNamesList = new ArrayList<>();
        HashMap<String, Integer> channels = new HashMap<>();
        channels.put("推荐", newsList.size());
        channels.put("其它", 0);

        newsList.forEach(news -> {
            String channelName = news.getChannelName();
            if (channelName != null) {
                channels.put(channelName, channels.getOrDefault(channelName, 0) + 1);
            } else {
                channels.put("其它", channels.get("其它"));
            }
        });
        channels.forEach((k, v) -> {
            if (v >= 10) {
                NewsChannel newsChannel = new NewsChannel(k, v);
                channelNamesList.add(newsChannel);
            }
        });
        channelNamesList.sort((o1, o2) -> o1.getCount() > o2.getCount() ? -1 : 0);
        channelNamesList.forEach(channel -> {
            mongoOpsTarget.insert(channel);
        });
    }
}

