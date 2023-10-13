package com.demo.metamusic.adapter.http.dto.response;

import java.util.List;

public record UpdatedArtistDTO(String name, List<String> aliases) {
}
