package com.jansmerecki.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansmerecki.domain.DeviceType;
import com.jansmerecki.dto.DeviceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldRegisterDeviceAndFetchByMac() throws Exception {
        var req = new DeviceRequest(DeviceType.GATEWAY, "11:22:33:44:55:66", null);

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deviceType").value("GATEWAY"))
                .andExpect(jsonPath("$.macAddress").value("11:22:33:44:55:66"));

        mockMvc.perform(get("/api/devices/11-22-33-44-55-66"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macAddress").value("11:22:33:44:55:66"));
    }

    @Test
    void shouldReturn400WhenMacIsInvalid() throws Exception {
        var bad = """
        {"deviceType":"GATEWAY","macAddress":"ZZ:ZZ:ZZ:ZZ:ZZ:ZZ"}
        """;

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("application/problem+json")));
    }

    @Test
    void shouldReturn409WhenMacIsDuplicate() throws Exception {
        DeviceRequest req1 = new DeviceRequest(DeviceType.GATEWAY, "AA:BB:CC:DD:EE:FF", null);
        DeviceRequest req2 = new DeviceRequest(DeviceType.SWITCH, "aa-bb-cc-dd-ee-ff", null);

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict())
                .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("application/problem+json")));
    }

    @Test
    void shouldReturnTopologyAndSubtree() throws Exception {
        mockMvc.perform(post("/api/devices").contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"deviceType":"GATEWAY","macAddress":"00:00:00:00:00:01"}
                """)).andExpect(status().isCreated());

        mockMvc.perform(post("/api/devices").contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"deviceType":"SWITCH","macAddress":"00:00:00:00:00:02","uplinkMacAddress":"00:00:00:00:00:01"}
                """)).andExpect(status().isCreated());

        mockMvc.perform(post("/api/devices").contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"deviceType":"ACCESS_POINT","macAddress":"00:00:00:00:00:03","uplinkMacAddress":"00:00:00:00:00:02"}
                """)).andExpect(status().isCreated());

        mockMvc.perform(get("/api/topology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].macAddress").value("00:00:00:00:00:01"))
                .andExpect(jsonPath("$[0].children[0].macAddress").value("00:00:00:00:00:02"));

        mockMvc.perform(get("/api/topology/00:00:00:00:00:02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macAddress").value("00:00:00:00:00:02"))
                .andExpect(jsonPath("$.children[0].macAddress").value("00:00:00:00:00:03"));
    }

}
