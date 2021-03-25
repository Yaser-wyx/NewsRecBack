package com.yaser.news.service;

import com.mongodb.client.MongoClients;
import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.constant.ResultCode;
import com.yaser.news.dataEntity.News;
import com.yaser.news.dataEntity.NewsChannel;
import com.yaser.news.repository.NewsChannelRepository;
import com.yaser.news.repository.NewsRepository;
import com.yaser.news.service.dataWrap.NewsDetails;
import com.yaser.news.service.dataWrap.NewsSimple;
import com.yaser.news.service.dataWrap.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.yaser.news.constant.ResultCode.CHANNEL_NOT_EXIST;

@Slf4j
@Service
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsChannelRepository newsChannelRepository;
    private final static int PAGE_SIZE = 10;
    private final MongoOperations mongoOps = new MongoTemplate(MongoClients.create(), "news_recommender");

    @Autowired
    public NewsService(NewsRepository newsRepository, NewsChannelRepository newsChannelRepository) {
        this.newsRepository = newsRepository;
        this.newsChannelRepository = newsChannelRepository;
    }

    public Map<String, Object> getAllNewsByPageNums(int pageNum) {
        int maxPageNum = (int) Math.ceil((double) this.newsRepository.count() / PAGE_SIZE);
        if (pageNum > maxPageNum) pageNum = maxPageNum;
        if (pageNum < 1) pageNum = 1;
        var result = new HashMap<String, Object>();
        Page<News> newsPage = this.newsRepository.findAll(PageRequest.of(pageNum - 1, PAGE_SIZE));
        this.iteratorPage(newsPage, pageNum, result);
        return result;
    }


    //读取page所有信息，并提取出数据
    private void iteratorPage(Page<News> newsPage, int pageNum, HashMap<String, Object> result) {
        PageData<News> pageData = new PageData<>(newsPage, pageNum);
        result.put("page", pageData);
        List<NewsSimple> newsSimpleList = new ArrayList<>();
        newsPage.forEach(news -> {
            var newsSimple = new NewsSimple();
            BeanUtils.copyProperties(news, newsSimple);
            newsSimpleList.add(newsSimple);
        });
        result.put("newsList", newsSimpleList);
    }

    public NewsDetails getNewsDetails(String docId) {
        if (!this.newsRepository.existsById(docId)) {
            throw new APIException(ResultCode.NEW_NOT_EXIST);
        }
        Optional<News> news = this.newsRepository.findById(docId);
        NewsDetails newsDetails = new NewsDetails();
        BeanUtils.copyProperties(news, newsDetails);
        return newsDetails;
    }

    public List<NewsChannel> getAllChannels() {
        return this.newsChannelRepository.findAll();
    }


    public Map<String, Object> getNewsListByChannelName(String channelName, int pageNum) {
        if (!channelName.equals("推荐") && !this.newsRepository.existsByChannelName(channelName))
            throw new APIException(CHANNEL_NOT_EXIST);
        Page<News> newsPage;
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        if (pageNum < 1) pageNum = 1;
        var result = new HashMap<String, Object>();
        if (channelName.equals("推荐")) {
            //暂时先用全部数据代替
            int maxPageNum = (int) Math.ceil((double) this.newsRepository.count() / PAGE_SIZE);
            if (pageNum > maxPageNum) pageNum = maxPageNum;
            newsPage = this.newsRepository.findAll(PageRequest.of(pageNum - 1, PAGE_SIZE, sort));
        } else {
            int maxPageNum = (int) Math.ceil((double) this.newsRepository.countAllByChannelName(channelName) / PAGE_SIZE);
            if (pageNum > maxPageNum) pageNum = maxPageNum;
            newsPage = this.newsRepository.findAllByChannelName(PageRequest.of(pageNum - 1, PAGE_SIZE, sort), channelName);
            log.info(newsPage.toString());
        }
        this.iteratorPage(newsPage, pageNum, result);
        return result;
    }


}
