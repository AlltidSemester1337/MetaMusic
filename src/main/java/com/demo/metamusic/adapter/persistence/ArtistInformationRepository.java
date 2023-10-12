package com.demo.metamusic.adapter.persistence;


import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtistInformationRepository extends CrudRepository<ArtistInformationEntity, Long> {
    List<ArtistInformationEntity> findByName(String artist);
}
