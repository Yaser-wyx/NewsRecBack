package com.yaser.news.controller;

import com.yaser.news.dataEntity.NewsChannel;
import com.yaser.news.service.NewsService;
import com.yaser.news.service.dataWrap.NewsDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")
@RequestMapping(value = "/v1/news")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/getNewsList")
    public Map<String, Object> getNewsList(@RequestParam int pageNum) {
        return newsService.getAllNewsByPageNums(pageNum);
    }

    @GetMapping("")
    public NewsDetails getNewsById(@RequestParam String newsId) {
        return newsService.getNewsDetails(newsId);
    }

    @GetMapping("/getChannels")
    public List<NewsChannel> getChannels() {
        return newsService.getAllChannels();
    }

    @GetMapping("/getNewsListByChannelName")
    public Map<String, Object> getNewsListByChannelName(@RequestParam String channelName, @RequestParam int pageNum) {
        return newsService.getNewsListByChannelName(channelName, pageNum);
    }

    @GetMapping("/getHotNews")
    public Map<String, Object> getHotNews() {
        return null;
    }


}
