package com.github.lihang941.swagger.base;

public enum CollectionFormat {
    CSV("csv"),
    SSV("ssv"),
    TSV("tsv"),
    PIPES("pipes"),
    MULTI("multi");
    private String format;

    public String format() {
        return format;
    }

    CollectionFormat(String format) {
        this.format = format;
    }
}
