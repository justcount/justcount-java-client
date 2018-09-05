package io.justcount;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Metric extends Backend {

    public String name;

    public Metric(String realm, String backend, String name) {
        this.name = name;
        this.realm = realm;
        this.backend = backend;
    }

    public static Metric from(String s) {
        String[] tokens = s.split(":");
        if (tokens.length != 3) return null;
        try {
            return new Metric(
                    URLDecoder.decode(tokens[0], "UTF-8"),
                    URLDecoder.decode(tokens[1], "UTF-8"),
                    URLDecoder.decode(tokens[2], "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        try {
            return String.format(
                    "%s:%s:%s",
                    URLEncoder.encode(this.realm, "UTF-8"),
                    URLEncoder.encode(this.backend, "UTF-8"),
                    URLEncoder.encode(this.name, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
