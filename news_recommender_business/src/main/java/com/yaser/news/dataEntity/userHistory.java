package com.yaser.news.dataEntity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "user_history")
@NoArgsConstructor
public class userHistory {
    private long uid;
    @Field(name = "news_id")
    private String newsId;

    @Field(name = "browse_time")
    private int browseTime;
}
