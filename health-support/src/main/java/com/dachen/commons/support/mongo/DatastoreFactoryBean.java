package com.dachen.commons.support.mongo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.MongoClient;

public class DatastoreFactoryBean extends AbstractFactoryBean<Datastore> {

    private Morphia morphia; // morphia实例，最好是单例

    private MongoClient mongoClient; // mongo实例，最好是单例

    private String dbName; // 数据库名

    private Boolean toEnsureIndexes = false; // 是否确认索引存在，默认false

    private Boolean toEnsureCaps = false; // 是否确认caps存在，默认false

    @Override
    protected Datastore createInstance() throws Exception {
        // 这里的username和password可以为null，morphia对象会去处理
        Datastore ds = morphia.createDatastore(mongoClient, dbName);
        if (toEnsureIndexes) {
            ds.ensureIndexes();
        }
        if (toEnsureCaps) {
            ds.ensureCaps();
        }
        return ds;
    }

    @Override
    public Class<?> getObjectType() {
        return Datastore.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (mongoClient == null) {
            throw new IllegalStateException("mongo is not set");
        }
        if (morphia == null) {
            throw new IllegalStateException("morphia is not set");
        }
    }

    /*----------------------setters-----------------------*/

    public void setMorphia(Morphia morphia) {
        this.morphia = morphia;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setToEnsureIndexes(Boolean toEnsureIndexes) {
        this.toEnsureIndexes = toEnsureIndexes;
    }

    public void setToEnsureCaps(Boolean toEnsureCaps) {
        this.toEnsureCaps = toEnsureCaps;
    }

}