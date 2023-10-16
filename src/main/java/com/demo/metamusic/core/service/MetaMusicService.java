package com.demo.metamusic.core.service;

import com.demo.metamusic.core.model.Artist;
import com.demo.metamusic.core.model.Track;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MetaMusicService {


    void addTrack(String artistName, Track Track);

    Artist updateArtist(String oldArtistName, Artist newArtist);

    Page<Track> getArtistTracksPaginated(String artistName, int page, int numTracks);
}
