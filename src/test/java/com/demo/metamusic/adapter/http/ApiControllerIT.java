package com.demo.metamusic.adapter.http;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.service.MetaMusicService;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static com.demo.metamusic.TestConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs(outputDir = "target/docs")
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
// TODO: 10/12/23 Scoped out documenting request/response field description etc due to time constraints but to be prod ready we should have proper documentation in place
//  This is a fair start at least (prod 1.0?)
@ExtendWith({RestDocumentationExtension.class})
class ApiControllerIT {


    private MockMvc mockMvc;

    @MockBean
    private MetaMusicService metaMusicService;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void whenAddTrack_withValidData_shouldRespondWithCreatedAndLinkToArtistTracks() throws Exception {
        ResultActions registerResult = addTrack(EXAMPLE_TRACK_PAYLOAD);

        registerResult.andExpect(status().isCreated())
                .andExpect(content().json("""
                                {
                          "updatedCatalogueLink": "/api/v1/artists/tracks?artistName=Fleetwood+Mac"
                        }"""))
                .andDo(document("addTrack"));
        verify(metaMusicService).addTrack(any());
    }

    private ResultActions addTrack(String requestBody) throws Exception {
        return mockMvc.perform(put("/api/v1/tracks/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }

    @Test
    void whenAddTrack_withInvalidData_shouldRespondWithBadRequest() throws Exception {
        ResultActions anotherAddTrackResult = addTrack(EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_TITLE);

        anotherAddTrackResult.andExpect(status().isBadRequest());

        ResultActions addTrackResult = addTrack(EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_DURATION);

        addTrackResult.andExpect(status().isBadRequest());

        ResultActions thirdAddTrackResult = addTrack(EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_RELEASE_DATE);

        thirdAddTrackResult.andExpect(status().isBadRequest());
    }
    // TODO: 10/11/23 Scoped out / left other types of error handling due to time constraints (For example we may want to treat DB connectivity issues and present a user friendly message etc)

    @Test
    void whenEditArtistName_withValidName_shouldRespondWithOkAndUpdatedData() throws Exception {
        String newName = "newName";
        when(metaMusicService.updateArtistInformation(anyString(), any()))
                .thenReturn(Optional.of(new ArtistInformation(newName, List.of())));

        mockMvc.perform(put("/api/v1/artists/Fleetwood+Mac/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                 "name":"%s"
                                 }""", newName)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("""
                        {
                            "name": "%s",
                            "aliases": []
                        }""", newName)))
                .andDo(document("editArtistName"));
        verify(metaMusicService).updateArtistInformation(eq(EXAMPLE_ARTIST_NAME), any());
    }

    @Test
    void whenEditArtistName_withNonExistingArtistName_shouldRespondWithNotFound() throws Exception {
        when(metaMusicService.updateArtistInformation(anyString(), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/artists/nonexistingartistname/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                 "name":"notBlank"
                                 }"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenEditArtistName_withInvalidNewName_shouldRespondWithBadRequest() throws Exception {
        when(metaMusicService.updateArtistInformation(anyString(), any()))
                .thenThrow(IllegalArgumentException.class);

        mockMvc.perform(put("/api/v1/artists/valid/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                 "name":""
                                 }"""))
                .andExpect(status().isBadRequest());
    }


    /*

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