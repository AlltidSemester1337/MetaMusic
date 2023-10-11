package com.demo.metamusic.config;


import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.TrackInformationRepository;
import com.demo.metamusic.core.service.MetaMusicService;
import com.demo.metamusic.core.service.MetaMusicServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MetaMusicServiceConfiguration {

    @Bean
    MetaMusicService metaMusicService(TrackInformationRepository trackInformationRepository, ArtistInformationRepository artistInformationRepository) {
        return new MetaMusicServiceImpl(trackInformationRepository, artistInformationRepository);
    }

}
