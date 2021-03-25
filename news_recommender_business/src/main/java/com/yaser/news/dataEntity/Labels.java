package com.yaser.news.dataEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "labels")
@AllArgsConstructor
public class Labels {
    private String label;
}
