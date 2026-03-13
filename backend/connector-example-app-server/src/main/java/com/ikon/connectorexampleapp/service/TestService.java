package com.ikon.connectorexampleapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ikon.connector.dto.response.FieldsConfigDto;
import com.ikon.connector.spi.ConnectorDataSync;

@Component
public class TestService implements ConnectorDataSync {
    public TestService() {
        System.out.println("TestService initialized");
    }

    @Override
    public List<FieldsConfigDto> getFieldsConfig() {
        List<FieldsConfigDto> fieldsConfig = new ArrayList<>();

        fieldsConfig.add(FieldsConfigDto.builder()
                .key("field1")
                .label("Field 1")
                .type("string")
                .build());
        return fieldsConfig;
    }

    @Override
    public String getModule() {
        return "Lead";
    }

    @Override
    public void syncBatch(List<Map<String, Object>> payload) {

        System.out.println("Syncing batch with payload: " + payload);
    }

}
