package com.demo.metamusic.adapter.http;

import com.demo.metamusic.adapter.persistence.MetaMusicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MetaMusicApiControllerIT {


    private MockMvc mockMvc;

    @MockBean
    private MetaMusicRepository mockMetaMusicRepository;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    /*@Test
    void whenRegister_withValidData_shouldRespondWithCreated() throws Exception {
        ResultActions registerResult = registerBroker();
        registerResult.andExpect(status().isCreated());
    }

    private ResultActions registerBroker() throws Exception {
        final String requestBody = String.format("""
                {
                  "hostName": "%s",
                  "port": %d
                }
                """, TestConstants.BROKER_INFORMATION.hostName(), TestConstants.BROKER_INFORMATION.port());

        return mockMvc.perform(put("/mqtt/" + TestConstants.BROKER_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }

    @Test
    void whenGet_withValidData_shouldRespondWithExpectedOkResponse() throws Exception {
        registerBroker();
        mockMvc.perform(get("/mqtt/" + TestConstants.BROKER_NAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                {
                          "hostName": "hostName",
                          "port": -1
                        }"""));
    }

    @Test
    void whenDelete_withValidBrokername_shouldRespondWithOkResponse() throws Exception {
        registerBroker();
        mockMvc.perform(delete("/mqtt/" + TestConstants.BROKER_NAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenSend_shouldBroadcastMessage() throws Exception {
        doNothing().when(mockMetaMusicRepository).broadCastMessage(any(), anyString(), anyString());

        sendTestMessage()
                .andExpect(status().isOk());

        verify(mockMetaMusicRepository).broadCastMessage(any(), eq(TestConstants.TEST_TOPIC), eq(TestConstants.TEST_MESSAGE));
    }

    private ResultActions sendTestMessage() throws Exception {
        return mockMvc.perform(post("/mqtt/" + TestConstants.BROKER_NAME + "/send/" + TestConstants.TEST_TOPIC)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestConstants.TEST_MESSAGE));
    }

    @Test
    void whenReceive_givenIncomingMessage_shouldRespondWithExpectedMessage() throws Exception {
        when(mockMetaMusicRepository.awaitNextMessage(any(), eq(TestConstants.TEST_TOPIC))).thenReturn(TestConstants.TEST_MESSAGE);

        mockMvc.perform(get("/mqtt/" + TestConstants.BROKER_NAME + "/receive/" + TestConstants.TEST_TOPIC)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(TestConstants.TEST_MESSAGE));
    }

     */


}