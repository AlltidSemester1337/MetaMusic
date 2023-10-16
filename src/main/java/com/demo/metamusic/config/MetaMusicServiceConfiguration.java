package com.demo.metamusic.config;


import com.demo.metamusic.adapter.persistence.ArtistRepository;
import com.demo.metamusic.core.service.MetaMusicService;
import com.demo.metamusic.core.service.MetaMusicServiceImpl;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MetaMusicServiceConfiguration {

    @Bean
    MetaMusicService metaMusicService(ArtistRepository ArtistRepository) {
        return new MetaMusicServiceImpl(ArtistRepository);
    }

    // TODO: 10/13/23 May be ok for small scale production, but as the application scales proper log indexing / persistence should be implemented
    @Bean
    HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }

}
