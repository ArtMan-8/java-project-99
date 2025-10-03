package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.util.TestDataFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndGetLabelById() throws Exception {
        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        String createResponse = mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(createResponse)
                .isObject()
                .containsEntry("name", "Bug")
                .containsKey("id");

        Long labelId = objectMapper.readTree(createResponse).get("id").asLong();

        String getResponse = mockMvc.perform(get("/api/labels/" + labelId)
                .with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(getResponse)
                .isObject()
                .containsKey("name")
                .containsKey("id");
    }

    @Test
    void shouldGetAllLabels() throws Exception {
        var label1 = TestDataFactory.createValidLabel("alpha-label");
        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label1)))
                .andExpect(status().isCreated());

        var label2 = TestDataFactory.createValidLabel("beta-label");
        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label2)))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(get("/api/labels")
                .with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(response)
                .inPath("$..name")
                .isArray()
                .contains(label1.getName(), label2.getName());
    }

    @Test
    void shouldUpdateLabelSuccessfully() throws Exception {
        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        String createResponse = mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long labelId = objectMapper.readTree(createResponse).get("id").asLong();

        LabelUpdateDTO updateLabel = TestDataFactory.createValidLabelUpdate("Feature");

        String response = mockMvc.perform(put("/api/labels/" + labelId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(response)
                .isObject()
                .containsEntry("name", "Feature")
                .containsEntry("id", labelId);
    }

    @Test
    void shouldDeleteLabelSuccessfully() throws Exception {
        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        label.setName("Новая метка для удаления");

        String response = mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/labels/" + id)
                .with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingLabelWithInvalidName() throws Exception {
        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        label.setName("");

        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingLabelWithInvalidName() throws Exception {
        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        String createResponse = mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long labelId = objectMapper.readTree(createResponse).get("id").asLong();

        LabelUpdateDTO updateLabel = TestDataFactory.createValidLabelUpdate("Feature");
        updateLabel.setName("");

        mockMvc.perform(put("/api/labels/" + labelId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/labels")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/labels/1")).andExpect(status().isUnauthorized());

        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        mockMvc.perform(post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isUnauthorized());

        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("Updated Label");
        mockMvc.perform(put("/api/labels/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/labels/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentLabel() throws Exception {
        mockMvc.perform(get("/api/labels/999")
                .with(jwt()))
                .andExpect(status().isNotFound());

        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("Updated Label");

        mockMvc.perform(put("/api/labels/999")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenCreatingLabelWithDuplicateName() throws Exception {
        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        label.setName("duplicate-test-label");

        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandlePartialUpdate() throws Exception {
        LabelCreateDTO label = TestDataFactory.createValidLabel("Bug");
        String createResponse = mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long labelId = objectMapper.readTree(createResponse).get("id").asLong();

        LabelUpdateDTO updateLabel = new LabelUpdateDTO();

        mockMvc.perform(put("/api/labels/" + labelId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isOk());
    }
}
