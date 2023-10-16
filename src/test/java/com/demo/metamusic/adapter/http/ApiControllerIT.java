package com.demo.metamusic.adapter.http;

import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.core.model.Artist;
import com.demo.metamusic.core.model.Track;
import com.demo.metamusic.core.service.MetaMusicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.demo.metamusic.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
                .andExpect(content().json(String.format("""
                                {
                          "updatedCatalogueLink": "/api/v1/artists/byname/%s/tracks"
                        }""", EXAMPLE_ARTIST_NAME_URL_ENCODED)))
                .andDo(document("addTrack"));
        verify(metaMusicService).addTrack(eq(EXAMPLE_ARTIST_NAME), any());
    }

    private ResultActions addTrack(String requestBody) throws Exception {
        return mockMvc.perform(put("/api/v1/artists/byname/" + EXAMPLE_ARTIST_NAME_URL_ENCODED + "/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }

    @Test
    void whenAddTrack_withInvalidData_shouldRespondWithBadRequest() throws Exception {
        ResultActions addTrackResult = addTrack(EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_DURATION);

        addTrackResult.andExpect(status().isBadRequest());

        ResultActions anotherAddTrackResult = addTrack(EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_TITLE);

        anotherAddTrackResult.andExpect(status().isBadRequest());

        ResultActions thirdAddTrackResult = addTrack(EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_RELEASE_DATE);

        thirdAddTrackResult.andExpect(status().isBadRequest());
    }

    @Test
    void whenAddTrack_withNonExistingArtist_shouldRespondWithNotFound() throws Exception {
        doThrow(NoArtistFoundException.class).when(metaMusicService).addTrack(anyString(), any());
        ResultActions addTrackResult = addTrack(EXAMPLE_TRACK_PAYLOAD);

        addTrackResult.andExpect(status().isNotFound());
    }

    // TODO: 10/11/23 Scoped out / left other types of error handling due to time constraints (For example we may want to treat DB connectivity issues and present a user friendly message etc)
    @Test
    void whenEditArtist_withValidName_shouldRespondWithOkAndUpdatedData() throws Exception {
        String newName = "newArtistName";
        Artist updatedArtist = new Artist(newName, Set.of());
        when(metaMusicService.updateArtist(anyString(), any()))
                .thenReturn(updatedArtist);

        sendUpdateArtistRequest(newName)
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("""
                        {
                            "name": "%s",
                            "aliases": []
                        }""", newName)))
                .andDo(document("editArtistName"));
        verify(metaMusicService).updateArtist(eq(EXAMPLE_ARTIST_NAME), eq(updatedArtist));
    }

    private ResultActions sendUpdateArtistRequest(String newName) throws Exception {
        return mockMvc.perform(put("/api/v1/artists/byname/" + EXAMPLE_ARTIST_NAME_URL_ENCODED)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                         "newName":"%s"
                         }""", newName)));
    }

    @Test
    void whenEditArtist_withNonExistingArtistName_shouldRespondWithNotFound() throws Exception {
        when(metaMusicService.updateArtist(anyString(), any()))
                .thenThrow(NoArtistFoundException.class);

        sendUpdateArtistRequest("valid")
                .andExpect(status().isNotFound());
    }

    @Test
    void whenEditArtist_withInvalidNewName_shouldRespondWithBadRequest() throws Exception {
        when(metaMusicService.updateArtist(anyString(), any()))
                .thenThrow(IllegalArgumentException.class);

        sendUpdateArtistRequest("")
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEditArtist_withNoDataToUpdate_shouldRespondWithBadRequest() throws Exception {
        mockMvc.perform(put("/api/v1/artists/byname/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                 }"""))
                .andExpect(status().isBadRequest());

        sendUpdateArtistRequest("Fleetwood Mac")
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEditArtist_withAlreadyExistingNewName_shouldRespondWithConflict() throws Exception {
        when(metaMusicService.updateArtist(anyString(), any()))
                .thenThrow(ArtistAlreadyExistsException.class);

        sendUpdateArtistRequest("valid")
                .andExpect(status().isConflict());
    }

    @Test
    void whenEditArtist_withValidNewAliases_shouldRespondWithOkAndUpdatedData() throws Exception {
        String newAlias = "newAlias";
        String anotherNewAlias = "anotherNewAlias";
        Set<String> newAliases = Set.of(newAlias, anotherNewAlias);
        Artist newArtist = new Artist(EXAMPLE_ARTIST_NAME, newAliases);
        when(metaMusicService.updateArtist(anyString(), any()))
                .thenReturn(newArtist);

        mockMvc.perform(put("/api/v1/artists/byname/Fleetwood+Mac")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                 "aliases":["%s","%s"]
                                 }""", newAlias, anotherNewAlias)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("""
                        {
                            "name": "%s",
                            "aliases": ["%s","%s"]
                        }""", EXAMPLE_ARTIST_NAME, newAlias, anotherNewAlias)));
        verify(metaMusicService).updateArtist(eq(EXAMPLE_ARTIST_NAME), eq(newArtist));
    }

    @Test
    void whenFetchArtistTracksPaginated_withValidArtistName_shouldRespondWithOkAndExpectedContent() throws Exception {
        Track secondTrack = new Track(EXAMPLE_TRACK_TITLE + "2", EXAMPLE_GENRE, EXAMPLE_TRACK_DURATION, EXAMPLE_TRACK_RELEASE_DATE);
        Page<Track> pageResponse = new PageImpl<>(List.of(EXAMPLE_TRACK, secondTrack));
        when(metaMusicService.getArtistTracksPaginated(anyString(), anyInt(), anyInt()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/artists/byname/" + EXAMPLE_ARTIST_NAME_URL_ENCODED + "/tracks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format(EXAMPLE_PAGINATION_RESPONSE_TEMPLATE, EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT,
                        EXAMPLE_RELEASE_DATE_TEXT, EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT, EXAMPLE_RELEASE_DATE_TEXT)))
                .andDo(document("fetchTracks"));
    }

    @Test
    void whenFetchArtistTracksPaginated_testMultiplePages_shouldRespondWithOkAndExpectedContent() throws Exception {
        Track secondTrack = new Track(EXAMPLE_TRACK_TITLE + "2", EXAMPLE_GENRE, EXAMPLE_TRACK_DURATION, EXAMPLE_TRACK_RELEASE_DATE);
        Page<Track> firstPageResponse = new PageImpl<>(List.of(EXAMPLE_TRACK), PageRequest.of(0, 1), 2);
        Page<Track> secondPageResponse = new PageImpl<>(List.of(secondTrack), PageRequest.of(1, 1), 2);
        when(metaMusicService.getArtistTracksPaginated(anyString(), anyInt(), eq(1)))
                .thenReturn(firstPageResponse, secondPageResponse);

        mockMvc.perform(get("/api/v1/artists/byname/" + EXAMPLE_ARTIST_NAME_URL_ENCODED + "/tracks?max=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format(EXAMPLE_RESPONSE_PAGINATION_FIRST_PAGE_TEMPLATE, EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT,
                        EXAMPLE_RELEASE_DATE_TEXT)));

        mockMvc.perform(get("/api/v1/artists/byname/" + EXAMPLE_ARTIST_NAME_URL_ENCODED + "/tracks?max=1&page=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format(EXAMPLE_RESPONSE_PAGINATION_SECOND_PAGE_TEMPLATE, EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT,
                        EXAMPLE_RELEASE_DATE_TEXT)));
    }

    @Test
    void whenFetchArtistTracksPaginated_withNonExistingArtistName_shouldRespondWithNotFound() throws Exception {
        when(metaMusicService.getArtistTracksPaginated(anyString(), anyInt(), anyInt()))
                .thenThrow(NoArtistFoundException.class);

        mockMvc.perform(get("/api/v1/artists/byname/nonexistingname/tracks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetArtistOfTheDay_withArtistAlreadyResolved_shouldRespondWithWithOkAndExpectedContent() throws Exception {
        Optional<Artist> artistOfTheDay = Optional.of(new Artist(EXAMPLE_ARTIST_NAME, Set.of()));
        when(metaMusicService.getArtistOfTheDay())
                .thenReturn(artistOfTheDay);

        mockMvc.perform(get("/api/v1/artists/artistOfTheDay")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("""
                        {
                            "name": "%s",
                            "aliases": []
                        }""", EXAMPLE_ARTIST_NAME)))
                .andDo(document("artistOfTheDay"));
    }

    @Test
    void whenGetArtistOfTheDay_withNoArtists_shouldRespondWithNoContent() throws Exception {
        when(metaMusicService.getArtistOfTheDay())
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/artists/artistOfTheDay")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}