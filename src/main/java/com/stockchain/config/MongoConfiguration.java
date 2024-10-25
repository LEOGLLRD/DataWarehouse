package com.stockchain.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfiguration.class);

    @Value("${database.dbname}")
    private String database;

    @Value("${mongo.db.url}")
    private String dbUrl;


    @Override
    public @NonNull MongoClient mongoClient() {

        LOGGER.info("Connecting to Mongo DB: {}", dbUrl);
        final ConnectionString connectionString = new ConnectionString(dbUrl);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);

    }


    @Override
    protected @NonNull String getDatabaseName() {
        return database;
    }
}