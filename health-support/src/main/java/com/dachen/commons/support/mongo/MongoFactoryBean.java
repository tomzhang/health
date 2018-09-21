package com.dachen.commons.support.mongo;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.ReadPreference;

public class MongoFactoryBean extends AbstractFactoryBean<Mongo> {

    // 表示服务器列表(主从复制或者分片)的字符串数组
    private String serverURI;

    // 是否主从分离(读取从库)，默认读写都在主库
    private Boolean readSecondary = true;

    // 设定写策略(出错时是否抛异常)，默认采用SAFE模式(需要抛异常)
    private WriteConcern writeConcern = WriteConcern.SAFE;

    @Override
    public Class<?> getObjectType() {
        return Mongo.class;
    }

    @Override
    protected MongoClient createInstance() throws Exception {
        MongoClient mongoClient = initMongo();

        // 设定主从分离
         if (readSecondary) {
        	 mongoClient.setReadPreference(ReadPreference.secondaryPreferred());
         }

        // 设定写策略
        //mongoClient.setWriteConcern(writeConcern);
        return mongoClient;
    }

    /**
     * 初始化mongo实例
     *
     * @return
     * @throws Exception
     */
    private MongoClient initMongo() throws Exception {
        return new MongoClient(new MongoClientURI(serverURI));
    }

    /* ------------------- setters --------------------- */

    public void setServerURI(String serverURI) {
        this.serverURI = serverURI;
    }

    public void setReadSecondary(Boolean readSecondary) {
        this.readSecondary = readSecondary;
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }

}