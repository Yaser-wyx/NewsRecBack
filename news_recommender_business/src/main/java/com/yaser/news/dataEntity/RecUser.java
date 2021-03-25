package com.yaser.news.dataEntity;

import com.yaser.news.constant.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;


@Data
@Document(collection = "rec_user")
@NoArgsConstructor
public class RecUser {
    @Id
    private long uid;
    @Field(name = "nickname")
    private String nickname;
    private String introduce = "没有简介";
    private int gender = 0;
    private String password = "";
    private String email = "";
    private List<String> labels = new ArrayList<>();
    private Roles role = Roles.USER;
}
