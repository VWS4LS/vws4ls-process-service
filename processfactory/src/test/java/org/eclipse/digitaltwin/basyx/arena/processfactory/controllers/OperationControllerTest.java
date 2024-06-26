package org.eclipse.digitaltwin.basyx.arena.processfactory.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.basyx.arena.processfactory.config.CamundaSettings;
import org.eclipse.digitaltwin.basyx.arena.processfactory.services.CamundaProcessManager;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;

@ZeebeProcessTest
@WebMvcTest(OperationController.class)
public class OperationControllerTest {
    final static String TEST_BPMN_FILENAME = "fusebox.bpmn";

    private ZeebeClient zeebeClient;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws IOException {
        CamundaProcessManager processManager = new CamundaProcessManager(
                new CamundaSettings("/tmp", null),
                zeebeClient);
        processManager.addProcessResource(getResourceFromClasspath(TEST_BPMN_FILENAME), TEST_BPMN_FILENAME);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(buildObjectMapper());

        this.mockMvc = MockMvcBuilders.standaloneSetup(new OperationController(processManager))
                .setMessageConverters(mappingJackson2HttpMessageConverter).build();
    }

    @Test
    void testDeployProcessOperation() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(OperationController.DEPLOY_PROCESS_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildInputDeployProcessContent()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        BpmnAssert.assertThat(InspectionUtility.findProcessInstances().findFirstProcessInstance().get());
    }

    private static byte[] buildInputDeployProcessContent() throws JsonProcessingException {
        return buildObjectMapper().writeValueAsBytes(
                new DefaultOperationRequest.Builder().inputArguments(List.of(buildInputVariables())).build());
    }

    private static OperationVariable[] buildInputVariables() {
        return new OperationVariable[] { new DefaultOperationVariable.Builder()
                .value(new DefaultProperty.Builder()
                        .idShort("input")
                        .valueType(DataTypeDefXsd.STRING)
                        .value("0").build())
                .build() };
    }

    private static ObjectMapper buildObjectMapper() {
        List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension());

        return new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
    }

    static InputStream getResourceFromClasspath(String relPath) throws IOException {
        return new ClassPathResource(relPath).getInputStream();
    }

}
