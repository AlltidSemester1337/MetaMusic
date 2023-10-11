package com.demo.metamusic.adapter.persistence;


import com.demo.metamusic.adapter.persistence.dto.ArtistInformationDTO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtistInformationRepository extends CrudRepository<ArtistInformationDTO, Long> {
    List<ArtistInformationDTO> findByName(String artist);
}
