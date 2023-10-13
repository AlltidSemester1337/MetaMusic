package com.demo.metamusic.core.util;

import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@UtilityClass
public class UrlEncodingUtils {

    public static String getCatalogueLink(String artistName) {
        return "/api/v1/artists/tracks?artistName=" + URLEncoder.encode(artistName, StandardCharsets.UTF_8);
    }

    public static String decodeArtistName(String name) {
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
    }
}
