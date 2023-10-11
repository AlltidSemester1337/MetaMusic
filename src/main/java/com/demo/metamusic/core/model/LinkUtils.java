package com.demo.metamusic.core.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LinkUtils {

    public static String getCatalogueLink(String artistName) {
        return "/api/v1/artists/tracks?artistName=" + URLEncoder.encode(artistName, StandardCharsets.UTF_8);
    }
}
