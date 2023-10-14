package com.demo.metamusic.adapter.http.dto.response;

import java.util.Set;

public record UpdatedArtistDTO(String name, Set<String> aliases) {
}
