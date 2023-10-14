package com.demo.metamusic.adapter.http.dto.request;

import java.util.Set;

public record ArtistUpdateDTO(String newName, Set<String> aliases) {
}
