package com.demo.metamusic.adapter.http;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import com.demo.metamusic.adapter.http.dto.request.TrackInformationDTO;
import com.demo.metamusic.adapter.http.dto.response.UpdatedArtistDTO;
import com.demo.metamusic.adapter.http.dto.response.UpdatedTrackCatalogueLinkDTO;
import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.util.UrlEncodingUtils;
import com.demo.metamusic.core.model.TrackInformation;
import com.demo.metamusic.core.service.MetaMusicService;
import io.micrometer.common.util.StringUtils;
import liquibase.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
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
            @RequestBody final TrackInformationDTO trackInformationDTO) {

        TrackInformation trackInformation;
        try {
            trackInformation = TrackInformation.fromDTO(trackInformationDTO);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            log.info("Caught exception on parsing TrackInformationDTO", e);
            // TODO: 10/11/23 This could/should be more detailed of specifically what data in the request is invalid (scoped out due to time constraints)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            metaMusicService.addTrack(UrlEncodingUtils.decodeArtistName(artistName), trackInformation);
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

        ArtistInformation newArtistInformation;
        try {
            newArtistInformation = ArtistInformation.fromDTO(oldArtistNameNormalized, artistUpdateDTO);
        } catch (IllegalArgumentException e) {
            log.info("Caught exception on parsing ArtistUpdateDTO", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ArtistInformation updatedArtistInformation;
        try {
            updatedArtistInformation = metaMusicService.updateArtistInformation(oldArtistNameNormalized, newArtistInformation);
        } catch (NoArtistFoundException e) {
            log.info("Could not find artist", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ArtistAlreadyExistsException e) {
            log.info("Artist with new name already exists", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ArtistInformation.toDTO(updatedArtistInformation));
    }

    private boolean isNoDataToUpdate(String oldArtistName, ArtistUpdateDTO artistUpdateDTO) {
        boolean aliasesIsEmptyOrNull = artistUpdateDTO.aliases() == null || artistUpdateDTO.aliases().isEmpty();
        boolean newNameIsBlankOrSameAsOld = StringUtils.isBlank(artistUpdateDTO.newName()) || artistUpdateDTO.newName().equals(oldArtistName);
        return aliasesIsEmptyOrNull && newNameIsBlankOrSameAsOld;
    }

    /*@DeleteMapping(path = "/{brokerName}")
    public ResponseEntity<Void> deleteBrokerInformation(
            @PathVariable("brokerName") final String brokerName) {
        //metamusicService.deleteBrokerInformation(brokerName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/{brokerName}/send/{topicName}")
    public ResponseEntity<Void> sendBrokerMessage(
            @PathVariable("brokerName") final String brokerName,
            @PathVariable("topicName") final String topicName,
            @RequestBody final String message) {
        //metamusicInformation metamusicInformation = metamusicService.getBrokerInformation(brokerName);
        //metaMusicRepository.broadCastMessage(metamusicInformation, topicName, message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
*/
}
