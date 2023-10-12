package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrackInformationRepositoryIT extends PersistenceIntegrationTestParent {

    @Autowired
    private TrackInformationRepository trackInformationRepository;

    @Test
    void givenValidTrackInformation_shouldPersistOnSave() {
        TrackInformationEntity newTrack = trackInformationRepository.save(new TrackInformationEntity(
                1L, TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH)));

        assertEquals(1, trackInformationRepository.findById(newTrack.getId()).get().getId());
    }
}