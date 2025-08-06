package com.zjlab.dataservice.modules.bench.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MongoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 分页查询 MongoDB 数据，支持排序
     *
     * @param collectionName 集合名称
     * @param orderByField   排序字段
     * @param orderByType    排序类型 ("asc"：升序，"desc"：降序)
     * @param page           页码（从 0 开始）
     * @param size           每页数量
     * @return 查询结果列表
     */
    public List<Document> getSortedPagedCollection(String collectionName, String orderByField, String orderByType, int page, int size) {
        // 获取集合
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        // 解析排序类型，默认升序
        Sort sort = "asc".equalsIgnoreCase(orderByType)
                ? Sort.by(Sort.Order.asc(orderByField))
                : Sort.by(Sort.Order.desc(orderByField));

        // 构建分页查询
        Query query = new Query()
                .with(PageRequest.of(page, size, sort)); // 分页 + 排序

        // 查询 MongoDB 获取数据
        return mongoTemplate.find(query, Document.class, collectionName);
    }
}
