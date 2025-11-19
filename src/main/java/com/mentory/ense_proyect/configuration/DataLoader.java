package com.mentory.ense_proyect.configuration;

import java.io.InputStream;
import java.util.List;
import com.mentory.ense_proyect.model.Ability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired private ObjectMapper mapper;

    @Override
    public void run(String... args) throws Exception {
        loadCollection("abilities", Ability.class);
    }

    private <T> void loadCollection(String collection, Class<T> clazz) throws Exception {

        if (mongoTemplate.getCollection(collection).countDocuments() > 0) {
            return;
        }

        InputStream input = getClass().getResourceAsStream("/data/" + collection + ".json");
        if (input == null) return;

        List<T> items = mapper.readValue(input,
                mapper.getTypeFactory().constructCollectionType(List.class, clazz));

        mongoTemplate.insert(items, collection);
    }
}
