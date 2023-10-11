package com.demo.metamusic.adapter.http;

import com.demo.metamusic.adapter.persistence.MetaMusicRepository;
import com.demo.metamusic.core.service.MetaMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        path = "/mqtt",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MetaMusicApiController {

    private final MetaMusicService metamusicService;
    @Autowired
    private final MetaMusicRepository metaMusicRepository;


    public MetaMusicApiController(MetaMusicService metamusicService, MetaMusicRepository metaMusicRepository) {
        this.metamusicService = metamusicService;
        this.metaMusicRepository = metaMusicRepository;
    }

    @PutMapping(path = "/{brokerName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registerBroker(
            @PathVariable("brokerName") final String brokerName,
            @RequestBody final com.demo.metamusicservice.adapter.http.dto.MetaMusicInformationDTO metamusicInformationDTO) {
        //metamusicService.registerBroker(brokerName, metamusicInformation.fromDto(metamusicInformationDTO));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(path = "/{brokerName}")
    public ResponseEntity<com.demo.metamusicservice.adapter.http.dto.MetaMusicInformationDTO> getBrokerInformation(
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
