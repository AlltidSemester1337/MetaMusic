package com.demo.metamusic.adapter.http;

import com.demo.metamusic.adapter.http.dto.TrackInformationDTO;
import com.demo.metamusic.adapter.http.dto.UpdatedTrackCatalogueLinkDTO;
import com.demo.metamusic.core.util.LinkUtils;
import com.demo.metamusic.core.model.TrackInformation;
import com.demo.metamusic.core.service.MetaMusicService;
import lombok.extern.slf4j.Slf4j;
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

    @PutMapping(path = "/tracks/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedTrackCatalogueLinkDTO> addTrack(
            @RequestBody final TrackInformationDTO trackInformationDTO) {

        TrackInformation trackInformation;
        try {
            trackInformation = TrackInformation.fromDTO(trackInformationDTO);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            log.info("Caught exception on parsing TrackInformationDTO", e);
            // TODO: 10/11/23 This could/should be more detailed of specifically what data in the request is invalid (scoped out due to time constraints)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        metaMusicService.addTrack(trackInformation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UpdatedTrackCatalogueLinkDTO(LinkUtils.getCatalogueLink(trackInformationDTO.artist())));
    }

    @GetMapping(path = "/{brokerName}")
    public ResponseEntity<TrackInformationDTO> getBrokerInformation(
            @PathVariable("brokerName") final String brokerName) {
        //com.demo.metamusicservice.adapter.http.dto.MetaMusicInformationDTO metamusicInformationDTO = metamusicInformation.toDto(metamusicService.getBrokerInformation(brokerName));
        return ResponseEntity.status(HttpStatus.OK).build();//.body(metamusicInformationDTO);
    }

    @DeleteMapping(path = "/{brokerName}")
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

    @GetMapping(path = "/{brokerName}/receive/{topicName}")
    public ResponseEntity<String> getNextMessage(
            @PathVariable("brokerName") final String brokerName,
            @PathVariable("topicName") final String topicName) {
        //metamusicInformation metamusicInformation = metamusicService.getBrokerInformation(brokerName);

        //try {
        //String message = metaMusicRepository.awaitNextMessage(metamusicInformation, topicName);
        return ResponseEntity.status(HttpStatus.OK).build();//.body(message);
        //} catch (InterruptedException e) {
        //    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        //}
    }
}
