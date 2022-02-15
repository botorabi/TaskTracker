/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.report.ReportSection;
import net.vrfun.tasktracker.task.Progress;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ReportDocumentCreator {

    static ByteArrayOutputStream getAs(ReportFormat type,
                                          List<ReportSection> sections,
                                          String title,
                                          String subTitle,
                                          String header,
                                          String footer) {

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

    private static void addSection(ReportEncoderFop encoder, ReportSection section) {
        encoder.addSectionTitle(section.getSectionTitle());
        encoder.addText(String.join("\n", section.getSectionBody()));
    }

}
