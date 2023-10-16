package com.demo.metamusic.adapter.http.dto.response;

import java.util.Set;

public record ArtistDTO(String name, Set<String> aliases) {
}
