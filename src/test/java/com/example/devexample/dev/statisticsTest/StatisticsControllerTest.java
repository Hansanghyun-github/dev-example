package com.example.devexample.dev.statisticsTest;

import com.example.devexample.statistics.controller.StatisticsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StatisticsControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper om;

    @Test
    void Api_Response_파싱_테스트() throws Exception {
        // when
        String content = mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successed").value(true))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonObject = (JSONObject) JSONParser.parseJSON(content);

        System.out.println(jsonObject);
        System.out.println(jsonObject.get("message"));
        System.out.println(jsonObject.get("successed"));

        JSONObject jsonObject1 = (JSONObject) JSONParser.parseJSON(jsonObject.get("data").toString());

        System.out.println(jsonObject1);


        // then
    }
}