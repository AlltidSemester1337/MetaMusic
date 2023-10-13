package com.demo.metamusic.core.util;

import lombok.experimental.UtilityClass;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@UtilityClass
public class LinkUtils {

    public static String getCatalogueLink(String artistName) {
        return "/api/v1/artists/tracks?artistName=" + URLEncoder.encode(artistName, StandardCharsets.UTF_8);
    }
}
