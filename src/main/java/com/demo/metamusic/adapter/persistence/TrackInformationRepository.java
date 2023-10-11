package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.adapter.persistence.dto.TrackInformationDTO;
import org.springframework.data.repository.CrudRepository;

public interface TrackInformationRepository extends CrudRepository<TrackInformationDTO, Long> {
}
