/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.task.Progress;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.List;

public class ReportGeneratorPDF implements ReportGenerator {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private PDDocument document;
    private PDPageContentStream contentStream;

    protected ReportGeneratorPDF() {}

    @Override
    public void begin() {
        if (document != null) {
            try {
                contentStream.close();
                document.close();
            } catch (IOException ignore) {}
        }

        document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try {
            contentStream = new PDPageContentStream(document, page);

        } catch (IOException exception) {
            LOGGER.error("Could not create PDF page content stream, reason: {}", exception.getMessage());
        }
    }

    @Override
    public void generateCoverPage(@NonNull final String title, String subTitle) {
        if (contentStream == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        try {
            contentStream.setFont(PDType1Font.HELVETICA, 16);
            contentStream.beginText();
            contentStream.newLine();
            contentStream.newLine();
            addText(title);
            contentStream.newLine();
            addText(subTitle);
            contentStream.endText();

        } catch (IOException exception) {
            LOGGER.error("Could not create PDF cover page, reason: {}", exception.getMessage());
            throw new IllegalStateException(exception.getMessage());
        }
    }

    protected void addText(@NonNull final String text) {
        String textLines[] = text.split("\n");
        for (String line: textLines) {
            try {
                contentStream.showText(line);
                contentStream.newLine();

            } catch (IOException exception) {
                LOGGER.error("Could not add text to PDF report, reason: {}", exception.getMessage());
                throw new IllegalStateException(exception.getMessage());
            }
        }
    }

    @Override
    public void sectionBegin(@NonNull final String title) {
        document.addPage(new PDPage());

        try {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            addText(title);
            contentStream.endText();

        } catch (IOException exception) {
            LOGGER.error("Could not create new section, reason: {}", exception.getMessage());
            throw new IllegalStateException(exception.getMessage());
        }
    }

    @Override
    public void sectionAppend(@NonNull final List<Progress> progressList) {
        if (contentStream == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        List<Progress> sortedProgressByOwnerAndCalendarWeek = sortByOwnerAndCalendarWeek(progressList);
        sortedProgressByOwnerAndCalendarWeek.forEach((progress) -> {
            try {
                LocalDate date = progress.getReportWeek();
                int reportWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int reportYear = date.get(IsoFields.WEEK_BASED_YEAR);

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();

                addText("Author: " + progress.getOwnerName());
                contentStream.newLine();
                String dateString = LocalDateTime.ofInstant(progress.getDateCreation(), ZoneOffset.systemDefault()).format(DateTimeFormatter.ofPattern("MM.dd.yyyy - HH:mm"));
                addText("Created: " + dateString);
                contentStream.newLine();
                addText("Calendar Week: " + reportYear + "/" + reportWeek);
                contentStream.newLine();

                if (progress.getTask() != null) {
                    addText("Task: " + progress.getTask().getTitle());
                    contentStream.newLine();
                }
                if (progress.getTags() != null && !progress.getTags().isEmpty()) {
                    StringBuffer tags = new StringBuffer();
                    progress.getTags().forEach((tag) -> tags.append(tag.getName() + " "));
                    addText("Tags: " + tags.toString());
                    contentStream.newLine();
                }
                contentStream.newLine();
                addText("Title: " + progress.getTitle());
                contentStream.newLine();
                addText("Text:");
                contentStream.newLine();
                addText(progress.getText());
                contentStream.endText();

            } catch (IOException exception) {
                LOGGER.error("Could not create new section, reason: {}", exception.getMessage());
                throw new IllegalStateException(exception.getMessage());
            }
        });
    }

    @Override
    public void sectionEnd() {
        try {
            contentStream.beginText();
            contentStream.newLine();
            contentStream.newLine();
            contentStream.endText();

        } catch (IOException exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    @Override
    public ByteArrayOutputStream end() {
        if (contentStream == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            contentStream.close();
            document.save(byteArrayOutputStream);
            document.close();
            return byteArrayOutputStream;

        } catch (IOException exception) {
            LOGGER.error("Could not finalize PDF document generation, reason: {}", exception.getMessage());
            throw new IllegalStateException(exception.getMessage());
        }
    }
}
