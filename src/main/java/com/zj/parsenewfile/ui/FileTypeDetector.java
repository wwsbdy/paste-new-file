package com.zj.parsenewfile.ui;

import java.util.regex.*;

public class FileTypeDetector {

    public enum DetectedType {
        JAVA, XML, JSON, HTML, UNKNOWN
    }

    public static DetectedType detect(String content) {
        String trim = content.trim();
        if (trim.startsWith("package ") || trim.contains("class ")) {
            return DetectedType.JAVA;
        }
        if (trim.startsWith("<")) {
            if (trim.contains("<?xml")) {
                return DetectedType.XML;
            }
            if (trim.contains("<html") || trim.contains("<!DOCTYPE html")) {
                return DetectedType.HTML;
            }
            return DetectedType.XML;
        }
        if (trim.startsWith("{") || trim.startsWith("[")) {
            return DetectedType.JSON;
        }
        return DetectedType.UNKNOWN;
    }

    public static String getJavaClassName(String content) {
        Matcher m = Pattern.compile("class\\s+(\\w+)").matcher(content);
        if (m.find()) return m.group(1);
        return "Unnamed";
    }

    public static String getXmlRootTag(String content) {
        Matcher m = Pattern.compile("<(\\w+)[^>]*>").matcher(content);
        if (m.find()) return m.group(1);
        return "file";
    }

    public static String getHtmlTitle(String content) {
        Matcher m = Pattern.compile("<title>(.*?)</title>").matcher(content);
        if (m.find()) return m.group(1).replaceAll("\\s+", "_");
        return "index";
    }
}