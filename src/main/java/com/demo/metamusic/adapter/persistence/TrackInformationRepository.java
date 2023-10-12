package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import org.springframework.data.repository.CrudRepository;

public interface TrackInformationRepository extends CrudRepository<TrackInformationEntity, Long> {
}
