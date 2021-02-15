package com.platonicideal.plugins.api;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QuietMapper {

    private static final Logger LOG = LoggerFactory.getLogger(QuietMapper.class);
    
    private final ObjectMapper mapper;

    @Autowired
    public QuietMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    public <Type> Type readValueFromString(String value, Class<Type> valueType) {
        try {
            return mapper.readValue(value, valueType);
        } catch(JsonProcessingException e) {
            LOG.error("An error occured deserializing {}", ReflectionToStringBuilder.toString(value));
            throw new IllegalStateException("Serialization failed", e);
        }
    }
    
    public <Type> Type readValueFromString(String value, TypeReference<Type> valueTypeReference) {
        try {
            return mapper.readValue(value, valueTypeReference);
        } catch(JsonProcessingException e) {
            LOG.error("An error occured deserializing {}", ReflectionToStringBuilder.toString(value));
            throw new IllegalStateException("Serialization failed", e);
        }
    }
    
    public String writeValueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOG.error("An error occured serializing {}", ReflectionToStringBuilder.toString(value));
            throw new IllegalStateException("Serialization failed", e);
        }
    }
    
}
