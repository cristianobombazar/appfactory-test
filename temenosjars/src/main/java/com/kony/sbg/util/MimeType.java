package com.kony.sbg.util;

public enum MimeType {
    xls("application/vnd.ms-excel"),
    jpeg("image/jpeg"),
    jpg("image/jpg"),
    gif("image/gif"),
    png("image/png"),
    doc("application/msword"),
    csv("text/csv"),
    docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    pdf("application/pdf");

    public final String mimeType;

    MimeType(String mimeType) {
        this.mimeType=mimeType;
    }
}
