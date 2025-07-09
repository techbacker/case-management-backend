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

import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should create task successfully with valid data")
    void createTask_ValidData_ReturnsCreated() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");
        task.setDueDateTime(LocalDateTime.now().plusDays(1));

        Task createdTask = new Task();
        createdTask.setId(1L);
        createdTask.setTitle("Test Task");
        createdTask.setDescription("Test Description");
        createdTask.setStatus("TODO");
        createdTask.setCaseId("CASE-123456");
        createdTask.setDueDateTime(task.getDueDateTime());
        createdTask.setCreatedDate(LocalDateTime.now());

        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.caseId").value("CASE-123456"));
    }

    @Test
    @DisplayName("Should return bad request when title is null")
    void createTask_NullTitle_ReturnsBadRequest() throws Exception {
        Task task = new Task();
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when title is empty")
    void createTask_EmptyTitle_ReturnsBadRequest() throws Exception {
        Task task = new Task();
        task.setTitle("");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when title is blank")
    void createTask_BlankTitle_ReturnsBadRequest() throws Exception {
        Task task = new Task();
        task.setTitle("   ");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return task when valid ID is provided")
    void getTaskById_ValidId_ReturnsTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setCaseId("CASE-123456");
        task.setCreatedDate(LocalDateTime.now());

        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.caseId").value("CASE-123456"));
    }

    @Test
    @DisplayName("Should return not found when task ID does not exist")
    void getTaskById_NonExistentId_ReturnsNotFound() throws Exception {
        when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return all tasks")
    void getAllTasks_ReturnsAllTasks() throws Exception {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setStatus("TODO");
        task1.setCaseId("CASE-123456");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setStatus("IN_PROGRESS");
        task2.setCaseId("CASE-654321");

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Should update task status successfully")
    void updateTaskStatus_ValidData_ReturnsUpdatedTask() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Test Task");
        updatedTask.setStatus("COMPLETED");
        updatedTask.setCaseId("CASE-123456");
        updatedTask.setUpdatedDate(LocalDateTime.now());

        when(taskService.updateTaskStatus(1L, "COMPLETED")).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should return bad request when status is null")
    void updateTaskStatus_NullStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when status is empty")
    void updateTaskStatus_EmptyStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when status is blank")
    void updateTaskStatus_BlankStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return not found when updating non-existent task")
    void updateTaskStatus_NonExistentTask_ReturnsNotFound() throws Exception {
        when(taskService.updateTaskStatus(999L, "COMPLETED")).thenReturn(null);

        mockMvc.perform(put("/api/tasks/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_ExistingTask_ReturnsNoContent() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent task")
    void deleteTask_NonExistentTask_ReturnsNotFound() throws Exception {
        when(taskService.deleteTask(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }
}