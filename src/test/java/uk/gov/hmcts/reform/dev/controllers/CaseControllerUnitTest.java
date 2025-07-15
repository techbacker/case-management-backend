package uk.gov.hmcts.reform.dev.controllers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.services.CaseService;

@ExtendWith(MockitoExtension.class)
class CaseControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private CaseService caseService;

    @InjectMocks
    private CaseController caseController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(caseController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should create case successfully with valid data")
    void createCase_ValidData_ReturnsCreated() throws Exception {
        Case task = new Case();
        task.setTitle("Test Case");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");
        task.setDueDateTime(LocalDateTime.now().plusDays(1));

        Case createdTask = new Case();
        createdTask.setId(1L);
        createdTask.setTitle("Test Case");
        createdTask.setDescription("Test Description");
        createdTask.setStatus("TODO");
        createdTask.setCaseId("CASE-123456");
        createdTask.setDueDateTime(task.getDueDateTime());
        createdTask.setCreatedDate(LocalDateTime.now());

        when(caseService.createCase(any(Case.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Case"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.caseId").value("CASE-123456"));
    }

    @Test
    @DisplayName("Should return bad request when title is null")
    void createCase_NullTitle_ReturnsBadRequest() throws Exception {
        Case task = new Case();
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");

        mockMvc.perform(post("/api/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when title is empty")
    void createCase_EmptyTitle_ReturnsBadRequest() throws Exception {
        Case task = new Case();
        task.setTitle("");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");

        mockMvc.perform(post("/api/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when title is blank")
    void createCase_BlankTitle_ReturnsBadRequest() throws Exception {
        Case task = new Case();
        task.setTitle("   ");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");

        mockMvc.perform(post("/api/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return case when valid ID is provided")
    void getCaseById_ValidId_ReturnsCase() throws Exception {
        Case task = new Case();
        task.setId(1L);
        task.setTitle("Test Case");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");
        task.setCreatedDate(LocalDateTime.now());

        when(caseService.getCaseById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/cases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Case"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.caseId").value("CASE-123456"));
    }

    @Test
    @DisplayName("Should return not found when case ID does not exist")
    void getCaseById_NonExistentId_ReturnsNotFound() throws Exception {
        when(caseService.getCaseById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cases/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return all cases")
    void getAllCases_ReturnsAllCases() throws Exception {
        Case case1 = new Case();
        case1.setId(1L);
        case1.setTitle("Case 1");
        case1.setStatus("TODO");
        case1.setCaseId("CASE-123456");

        Case case2 = new Case();
        case2.setId(2L);
        case2.setTitle("Case 2");
        case2.setStatus("IN_PROGRESS");
        case2.setCaseId("CASE-654321");

        List<Case> cases = Arrays.asList(case1, case2);
        when(caseService.getAllCases()).thenReturn(cases);

        mockMvc.perform(get("/api/cases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Case 1"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Case 2"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Should update case status successfully")
    void updateCaseStatus_ValidData_ReturnsUpdatedCase() throws Exception {
        Case updatedCase = new Case();
        updatedCase.setId(1L);
        updatedCase.setTitle("Test Case");
        updatedCase.setStatus("COMPLETED");
        updatedCase.setCaseId("CASE-123456");
        updatedCase.setUpdatedDate(LocalDateTime.now());

        when(caseService.updateCaseStatus(1L, "COMPLETED")).thenReturn(updatedCase);

        mockMvc.perform(put("/api/cases/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Case"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should return bad request when status is null")
    void updateCaseStatus_NullStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/cases/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when status is empty")
    void updateCaseStatus_EmptyStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/cases/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when status is blank")
    void updateCaseStatus_BlankStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/cases/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return not found when updating non-existent case")
    void updateCaseStatus_NonExistentCase_ReturnsNotFound() throws Exception {
        when(caseService.updateCaseStatus(999L, "COMPLETED")).thenReturn(null);

        mockMvc.perform(put("/api/cases/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete case successfully")
    void deleteCase_ExistingCase_ReturnsNoContent() throws Exception {
        when(caseService.deleteCase(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/cases/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent case")
    void deleteCase_NonExistentCase_ReturnsNotFound() throws Exception {
        when(caseService.deleteCase(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/cases/999"))
                .andExpect(status().isNotFound());
    }
}