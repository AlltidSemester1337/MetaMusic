package com.demo.metamusic.core.service;

import com.demo.metamusic.core.model.Artist;
import com.demo.metamusic.core.model.Track;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface MetaMusicService {


    void addTrack(String artistName, Track Track);

    Artist updateArtist(String oldArtistName, Artist newArtist);

    Page<Track> getArtistTracksPaginated(String artistName, int page, int numTracks);

    // TODO: 10/16/23 To ensure true fairness we could add a scheduled background async task to make sure this is invoked also on a day
    // where there is no incoming request to the service, however I scoped this out due to time constraint and very little benefit
    // as a first version / release I think it's sufficient to rely on incoming get to trigger update
    Optional<Artist> getArtistOfTheDay();
}
