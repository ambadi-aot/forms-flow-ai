package org.camunda.bpm.extension.hooks.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.extension.commons.connector.HTTPServiceInvoker;
import org.camunda.bpm.extension.commons.ro.res.IResponse;
import org.camunda.bpm.extension.hooks.delegates.data.TextSentimentData;
import org.camunda.bpm.extension.hooks.delegates.data.TextSentimentRequest;
import org.camunda.bpm.extension.hooks.services.FormSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.camunda.bpm.extension.commons.utils.VariableConstants.FORM_URL;
import static org.camunda.bpm.extension.commons.utils.VariableConstants.APPLICATION_ID;

/**
 * Form Text Analysis Delegate Test.
 * Test class for FormTextAnalysisDelegate.
 */
@ExtendWith(SpringExtension.class)
public class FormTextAnalysisDelegateTest {

    @InjectMocks
    private FormTextAnalysisDelegate formTextAnalysisDelegate;

    @Mock
    private FormSubmissionService formSubmissionService;

    @Mock
    private HTTPServiceInvoker httpServiceInvoker;

    @BeforeEach
    public void setup() {
        try {
            Field field = formTextAnalysisDelegate.getClass().getDeclaredField("bpmObjectMapper");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(this.formTextAnalysisDelegate, "bpmObjectMapper", objectMapper);
    }

    /**
     * This test case perform a positive test over execute method in FormTextAnalysisDelegate
     * This will verify the textSentimentRequest
     */
    @Test
    public void formTextAnalysisDelegate_happyFlow() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        Map<String, Object> variable = new HashMap<>();
        String data = "{\"data\":{\"formId\":\"123\",\"formName\":\"New Business Licence\"," +
                "\"description\":{\"type\":\"textAreaWithAnalytics\",\"topics\":[\"t1\",\"t2\"],\"text\":\"test\"}}}";
        variable.put(FORM_URL, "http://localhost:3001/submission/id1");
        variable.put(APPLICATION_ID, 123);
        when(execution.getVariables())
                .thenReturn(variable);
        when(execution.getVariable(FORM_URL))
                .thenReturn(variable.get(FORM_URL));
        when(execution.getVariable(APPLICATION_ID))
                .thenReturn(variable.get(APPLICATION_ID));
        when(formSubmissionService.readSubmission(anyString()))
                .thenReturn(data);
        List<TextSentimentData> txtRecords = new ArrayList<>();

        //txtRecords.add(formTextAnalysisDelegate .CreateTextSentimentData("description",
        //        new ArrayList<>(Arrays.asList("t1","t2")), "test"));
        //when(httpServiceInvoker.execute(anyString(), any(HttpMethod.class), any(), any()))
        //        .thenReturn(ResponseEntity.ok());

        TextSentimentRequest textSentimentRequest = new TextSentimentRequest(123, "http://localhost:3001/submission/id1",txtRecords);
        ArgumentCaptor<TextSentimentRequest> captor = ArgumentCaptor.forClass(TextSentimentRequest.class);

        Properties properties = mock(Properties.class);
        when(httpServiceInvoker.getProperties())
                .thenReturn(properties);
        when(properties.getProperty("api.url"))
                .thenReturn("http://localhost:5001");
        formTextAnalysisDelegate.execute(execution);
        verify(httpServiceInvoker).execute(anyString(), any(HttpMethod.class),captor.capture());
        assertEquals(textSentimentRequest, captor.getValue());
    }

    /**
     * This test case perform a positive test over execute method in FormTextAnalysisDelegate
     * This will handle the runtime Exception
     */
    @Test
    public void formTextAnalysisDelegate_with_nullSubmissionData() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        Map<String, Object> variable = new HashMap<>();
        variable.put(FORM_URL, "http://localhost:3001/submission/id1");
        when(execution.getVariables())
                .thenReturn(variable);
        when(formSubmissionService.readSubmission(anyString()))
                .thenReturn("");
        assertThrows(RuntimeException.class, () -> {
            formTextAnalysisDelegate.execute(execution);
        });
    }

    /**
     * This test case perform a test over execute method with empty submission data
     * This will verify the TextSentimentRequest
     */
    @Test
    public void formTextAnalysisDelegate_with_emptySubmissionData() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        Map<String, Object> variable = new HashMap<>();
        variable.put(FORM_URL, "http://localhost:3001/submission/id1");
        when(execution.getVariables())
                .thenReturn(variable);
        when(formSubmissionService.readSubmission(anyString()))
                .thenReturn("{}");
        formTextAnalysisDelegate.execute(execution);
        verify(httpServiceInvoker, times(0)).execute(anyString(), any(HttpMethod.class),any(TextSentimentRequest.class));
    }
}
