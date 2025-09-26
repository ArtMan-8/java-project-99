package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.utils.TestDataFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LabelControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFactory testDataFactory;

    private Label testLabel;

    @BeforeEach
    void setUp() {
        testDataFactory.cleanAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testLabel = testDataFactory.createLabel();
    }

    @AfterEach
    void cleanUp() {
        testDataFactory.cleanAll();
    }

    private LabelCreateDTO createValidLabel() {
        LabelCreateDTO label = new LabelCreateDTO();
        label.setName("Bug");
        return label;
    }

    @Test
    void shouldCreateLabelSuccessfully() throws Exception {
        LabelCreateDTO label = createValidLabel();
        var result = mockMvc.perform(post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("Bug"),
                v -> v.node("id").isPresent(),
                v -> v.node("createdAt").isPresent()
        );
    }

    @Test
    void shouldGetAllLabels() throws Exception {
        var result = mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    void shouldGetLabelById() throws Exception {
        var result = mockMvc.perform(get("/api/labels/{id}", testLabel.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testLabel.getName()),
                v -> v.node("id").isEqualTo(testLabel.getId())
        );
    }

    @Test
    void shouldUpdateLabelSuccessfully() throws Exception {
        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("Feature");

        var result = mockMvc.perform(put("/api/labels/{id}", testLabel.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("Feature"),
                v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("createdAt").isPresent()
        );
    }

    @Test
    void shouldDeleteLabelSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/labels/{id}", testLabel.getId()).with(jwt()))
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

        mockMvc.perform(put("/api/labels/{id}", testLabel.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingLabelWithTooShortName() throws Exception {
        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("ab");

        mockMvc.perform(put("/api/labels/{id}", testLabel.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/labels")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/labels/{id}", testLabel.getId())).andExpect(status().isUnauthorized());

        LabelCreateDTO label = createValidLabel();
        mockMvc.perform(post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(label)))
                .andExpect(status().isUnauthorized());

        LabelUpdateDTO updateLabel = new LabelUpdateDTO();
        updateLabel.setName("Updated Label");
        mockMvc.perform(put("/api/labels/{id}", testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/labels/{id}", testLabel.getId())).andExpect(status().isUnauthorized());
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

        mockMvc.perform(put("/api/labels/{id}", testLabel.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLabel)))
                .andExpect(status().isOk());
    }
}
