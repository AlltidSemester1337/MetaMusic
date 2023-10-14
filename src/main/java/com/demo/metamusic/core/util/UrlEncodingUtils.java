package com.demo.metamusic.core.util;

import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class UrlEncodingUtils {

    public static String decodeArtistName(String name) {
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
    }
}
