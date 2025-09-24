package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LabelCreateDTO createValidLabel() {
        LabelCreateDTO label = new LabelCreateDTO();
        label.setName("Bug");
        return label;
    }

    @Test
    void shouldCreateLabelSuccessfully() throws Exception {
        LabelCreateDTO label = createValidLabel();
        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bug"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldGetAllLabels() throws Exception {
        mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetLabelById() throws Exception {
        LabelCreateDTO label = createValidLabel();
        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/labels/1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldUpdateLabelSuccessfully() throws Exception {
        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("Feature");

        mockMvc.perform(put("/api/labels/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Feature"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldDeleteLabelSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/labels/1").with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingLabelWithInvalidData() throws Exception {
        LabelCreateDTO label = createValidLabel();
        label.setName("");

        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreatingLabelWithTooShortName() throws Exception {
        LabelCreateDTO label = createValidLabel();
        label.setName("ab");

        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreatingLabelWithTooLongName() throws Exception {
        LabelCreateDTO label = createValidLabel();
        label.setName("a".repeat(1001));

        mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingLabelWithInvalidData() throws Exception {
        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("");

        mockMvc.perform(put("/api/labels/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingLabelWithTooShortName() throws Exception {
        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("ab");

        mockMvc.perform(put("/api/labels/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/labels")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/labels/1")).andExpect(status().isUnauthorized());

        LabelCreateDTO label = createValidLabel();
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

        mockMvc.perform(delete("/api/labels/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentLabel() throws Exception {
        mockMvc.perform(get("/api/labels/999").with(jwt()))
                .andExpect(status().isNotFound());

        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("Updated Label");

        mockMvc.perform(put("/api/labels/999")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/labels/999").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenCreatingLabelWithDuplicateName() throws Exception {
        LabelCreateDTO label = createValidLabel();
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
        LabelUpdateDTO updateLabel = new LabelUpdateDTO();

        mockMvc.perform(put("/api/labels/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isOk());
    }
}
