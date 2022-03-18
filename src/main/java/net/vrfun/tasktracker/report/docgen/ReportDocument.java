/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.report.ReportSection;
import org.springframework.lang.NonNull;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class ReportDocument {

    @NonNull
    public static ReportDocument build() {
        return new ReportDocument();
    }

    @NonNull
    public ByteArrayOutputStream create(@NonNull final ReportFormat type,
                                        @NonNull final List<ReportSection> sections,
                                        @NonNull final String title,
                                        @NonNull final String subTitle,
                                        @NonNull final String header,
                                        @NonNull final String footer) {

        ReportEncoderFop encoder = new ReportEncoderFop(type);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outStream);
        encoder.start(bufferedOutputStream);
        encoder.setFooter(footer);
        encoder.setHeader(header);
        encoder.setTitle(title, subTitle);

        for (ReportSection section : sections) {
            addSection(encoder, section);
        }
        encoder.end();
        return outStream;
    }

    private void addSection(@NonNull final ReportEncoderFop encoder, @NonNull final ReportSection section) {
        encoder.addSectionTitle(section.getSectionTitle());
        section.getSectionBody().forEach(sectionBody -> {
                    encoder.addMetaInfo(sectionBody.getMetaInformation());
                    encoder.addSubTitle(sectionBody.getSubtitle());
                    encoder.addText(sectionBody.getText());
                }
        );
    }

}
