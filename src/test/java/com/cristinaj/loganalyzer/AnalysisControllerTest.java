package com.cristinaj.loganalyzer;

import com.cristinaj.loganalyzer.analysis.AnalysisController;
import com.cristinaj.loganalyzer.analysis.LogAnalysisService;
import com.cristinaj.loganalyzer.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AnalysisController.class)
@Import(GlobalExceptionHandler.class)
class AnalysisControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LogAnalysisService logAnalysisService;

    @Test
    void getLogs_returns400_whenFromAfterTo() throws Exception {
        mockMvc.perform(get("/logs")
                        .param("from", "2025-10-07T10:00:00Z")
                        .param("to",   "2025-10-06T10:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("from must be <= to"));

        Mockito.verifyNoInteractions(logAnalysisService);
    }
}
