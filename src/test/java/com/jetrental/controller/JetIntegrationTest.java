package com.jetrental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetrental.entity.Jet;
import com.jetrental.repository.JetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Jet Controller Integration Tests")
public class JetIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private JetRepository jetRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private Jet testJet;

        @BeforeEach
        void setUp() {
                jetRepository.deleteAll();
                testJet = Jet.builder()
                                .brand("TestBrand")
                                .model("TestModel")
                                .seats(10)
                                .pricePerDay(5000.0)
                                .tailNumber("TEST01")
                                .available(true)
                                .build();
                testJet = jetRepository.save(testJet);
        }

        // --- API Tests ---

        @Test
        @DisplayName("API: Get All Jets")
        void testGetAllJets() throws Exception {
                mockMvc.perform(get("/api/jets"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].tailNumber", is("TEST01")));
        }

        @Test
        @DisplayName("API: Get Jet By ID")
        void testGetJetById() throws Exception {
                mockMvc.perform(get("/api/jets/" + testJet.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(testJet.getId())));
        }

        @Test
        @DisplayName("API: Create Jet")
        void testCreateJet() throws Exception {
                Jet newJet = Jet.builder().brand("New").model("Jet").seats(4).pricePerDay(1000).tailNumber("NEW01")
                                .available(true).build();

                mockMvc.perform(post("/api/jets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newJet)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.tailNumber", is("NEW01")));
        }

        @Test
        @DisplayName("API: Update Jet")
        void testUpdateJet() throws Exception {
                testJet.setBrand("UpdatedBrand");

                mockMvc.perform(put("/api/jets/" + testJet.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testJet)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.brand", is("UpdatedBrand")));
        }

        @Test
        @DisplayName("API: Delete Jet")
        void testDeleteJet() throws Exception {
                mockMvc.perform(delete("/api/jets/" + testJet.getId()))
                                .andExpect(status().isOk());

                assertFalse(jetRepository.existsById(testJet.getId()));
        }

        @Test
        @DisplayName("API: Search Jets")
        void testSearchJets() throws Exception {
                mockMvc.perform(get("/api/jets/search")
                                .param("minSeats", "5")
                                .param("maxSeats", "15")
                                .param("maxPrice", "6000")
                                .param("brand", "TestBrand"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        // --- Web Tests ---

        @Test
        @DisplayName("Web: List Jets")
        void testListJetsPage() throws Exception {
                mockMvc.perform(get("/jets"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("jet/list"))
                                .andExpect(model().attributeExists("jets"));
        }

        @Test
        @DisplayName("Web: Show Add Form")
        void testShowAddForm() throws Exception {
                mockMvc.perform(get("/jets/add"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("jet/add"))
                                .andExpect(model().attributeExists("jet"));
        }

        @Test
        @DisplayName("Web: Add Jet Submit")
        void testAddJetSubmit() throws Exception {
                mockMvc.perform(post("/jets/add")
                                .param("brand", "WebBrand")
                                .param("model", "WebModel")
                                .param("seats", "8")
                                .param("pricePerDay", "4000")
                                .param("tailNumber", "WEB01")
                                .param("available", "true"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/jets"));
        }

        @Test
        @DisplayName("Web: Show Edit Form")
        void testShowEditForm() throws Exception {
                mockMvc.perform(get("/jets/edit/" + testJet.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("jet/edit"))
                                .andExpect(model().attributeExists("jet"));
        }

        @Test
        @DisplayName("Web: Update Jet Submit")
        void testUpdateJetSubmit() throws Exception {
                mockMvc.perform(post("/jets/edit")
                                .param("id", String.valueOf(testJet.getId()))
                                .param("brand", "UpdatedWeb")
                                .param("model", "Model")
                                .param("seats", "10")
                                .param("pricePerDay", "5000")
                                .param("tailNumber", "TEST01")
                                .param("available", "true"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/jets"));
        }

        @Test
        @DisplayName("Web: Delete Jet")
        void testWebDeleteJet() throws Exception {
                mockMvc.perform(get("/jets/delete/" + testJet.getId()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/jets"));
                assertFalse(jetRepository.existsById(testJet.getId()));
        }

        @Test
        @DisplayName("Web: Filter Form")
        void testShowFilterForm() throws Exception {
                mockMvc.perform(get("/jets/filter"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("jet/filter"));
        }

        @Test
        @DisplayName("Web: Filter Submit")
        void testFilterSubmit() throws Exception {
                mockMvc.perform(post("/jets/filter")
                                .param("minSeats", "5")
                                .param("maxSeats", "15")
                                .param("maxPrice", "6000"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("jet/filteredJets"))
                                .andExpect(model().attributeExists("jets"));
        }
}
