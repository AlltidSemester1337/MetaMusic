package com.demo.metamusic.adapter.http;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import com.demo.metamusic.adapter.http.dto.request.TrackDTO;
import com.demo.metamusic.adapter.http.dto.response.UpdatedArtistDTO;
import com.demo.metamusic.adapter.http.dto.response.UpdatedTrackCatalogueLinkDTO;
import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.core.model.Artist;
import com.demo.metamusic.core.util.UrlEncodingUtils;
import com.demo.metamusic.core.model.Track;
import com.demo.metamusic.core.service.MetaMusicService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@RequestMapping(
        path = "/api/v1",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Slf4j
public class ApiController {

    private final MetaMusicService metaMusicService;


    public ApiController(MetaMusicService metaMusicService) {
        this.metaMusicService = metaMusicService;
    }

    @PutMapping(path = "/artists/byname/{name}/tracks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedTrackCatalogueLinkDTO> addTrack(
            @PathVariable("name") final String artistName,
            @RequestBody final TrackDTO TrackDTO) {

        Track Track;
        try {
            Track = Track.fromDTO(TrackDTO);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            log.info("Caught exception on parsing TrackDTO", e);
            // TODO: 10/11/23 This could/should be more detailed of specifically what data in the request is invalid (scoped out due to time constraints)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            metaMusicService.addTrack(UrlEncodingUtils.decodeArtistName(artistName), Track);
            String updatedCatalogueLink = "/api/v1/artists/byname/" + artistName + "/tracks";

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UpdatedTrackCatalogueLinkDTO(updatedCatalogueLink));
        } catch (NoArtistFoundException e) {
            log.info("Could not find artist", e);
            // TODO: 10/11/23 This could/should be more detailed of specifically what data in the request is conflicting (scoped out due to time constraints)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(path = "/artists/byname/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedArtistDTO> editArtist(
            @PathVariable("name") final String artistName,
            @RequestBody final ArtistUpdateDTO artistUpdateDTO) {
        String oldArtistNameNormalized = UrlEncodingUtils.decodeArtistName(artistName);

        if (isNoDataToUpdate(oldArtistNameNormalized, artistUpdateDTO)) {
            log.info("Received update request without any data to update {}, {}", oldArtistNameNormalized, artistUpdateDTO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Artist newArtist;
        try {
            newArtist = Artist.fromDTO(oldArtistNameNormalized, artistUpdateDTO);
        } catch (IllegalArgumentException e) {
            log.info("Caught exception on parsing ArtistUpdateDTO", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Artist updatedArtist;
        try {
            updatedArtist = metaMusicService.updateArtist(oldArtistNameNormalized, newArtist);
        } catch (NoArtistFoundException e) {
            log.info("Could not find artist", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ArtistAlreadyExistsException e) {
            log.info("Artist with new name already exists", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(Artist.toDTO(updatedArtist));
    }

    private boolean isNoDataToUpdate(String oldArtistName, ArtistUpdateDTO artistUpdateDTO) {
        boolean aliasesIsEmptyOrNull = artistUpdateDTO.aliases() == null || artistUpdateDTO.aliases().isEmpty();
        boolean newNameIsBlankOrSameAsOld = StringUtils.isBlank(artistUpdateDTO.newName()) || artistUpdateDTO.newName().equals(oldArtistName);
        return aliasesIsEmptyOrNull && newNameIsBlankOrSameAsOld;
    }

    // TODO: 2023-10-15 Since we are paginating we should consider the order for optimization and user value purpose
    // most likely this could mean order by release date newest first, however this was scoped out since it wasn't requested in the requirements
    @GetMapping(path = "/artists/byname/{name}/tracks")
    public ResponseEntity<Page<TrackDTO>> fetchTracks(
            @PathVariable("name") final String artistName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int max) {
        Page<Track> artistTracks;
        try {
            artistTracks = metaMusicService.getArtistTracksPaginated(UrlEncodingUtils.decodeArtistName(artistName), page, max);
        } catch (NoArtistFoundException e) {
            log.info("Could not find artist", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(artistTracks.map(Track::toDTO));
    }
}
