/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.task.Progress;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;

public class ReportGeneratorPlainText implements ReportGenerator {

    private ByteArrayOutputStream byteArrayOutputStream;

    protected ReportGeneratorPlainText() {}

    @Override
    public void begin() {
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.close();
            } catch (IOException ignore) {}
        }

        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public void setFooter(@Nullable final String footer) {
    }

    @Override
    public void generateCoverPage(@NonNull final String title, String subTitle) {
        if (byteArrayOutputStream == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        StringBuffer text = new StringBuffer();

        text.append(title);
        text.append("\n\n");
        text.append(subTitle);
        text.append("\n\n\n");

        byteArrayOutputStream.writeBytes(text.toString().getBytes());
    }

    @Override
    public void sectionBegin(@NonNull final String title) {
        StringBuffer text = new StringBuffer();

        StringBuffer decorationLine = new StringBuffer();
        title.chars().forEach((c) -> decorationLine.append("-"));
        text.append(decorationLine);
        text.append("\n" + title +"\n");
        text.append(decorationLine);
        text.append("\n\n");

        byteArrayOutputStream.writeBytes(text.toString().getBytes());
    }

    @Override
    public void sectionAppend(@NonNull final List<Progress> progressList) {
        if (byteArrayOutputStream == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        StringBuffer text = new StringBuffer();

        List<Progress> sortedProgressByOwnerAndCalendarWeek = sortByOwnerAndCalendarWeek(progressList);
        sortedProgressByOwnerAndCalendarWeek.forEach((progress) -> {
            LocalDate date = progress.getReportWeek();
            int reportWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int reportYear = date.get(IsoFields.WEEK_BASED_YEAR);
            text.append("\n\n\n");
            text.append("Author: " + progress.getOwnerName() + "\n");
            String dateString = LocalDateTime.ofInstant(progress.getDateCreation(), ZoneOffset.systemDefault()).format(DateTimeFormatter.ofPattern("MM.dd.yyyy - HH:mm"));
            text.append("Created: " + dateString + "\n");
            text.append("Calendar Week: " + reportYear + "/" + reportWeek);
            text.append("\n");
            if (progress.getTask() != null) {
                text.append("Task: " + progress.getTask().getTitle());
                text.append("\n");
            }
            if (progress.getTags() != null && !progress.getTags().isEmpty()) {
                text.append("Tags: ");
                progress.getTags().forEach((tag) -> text.append(tag.getName() + " "));
                text.append("\n");
            }
            text.append("\nTitle: " + progress.getTitle() + "\n");
            text.append("\nText:\n");
            text.append(progress.getText());
        });

        byteArrayOutputStream.writeBytes(text.toString().getBytes());
    }

    @Override
    public void sectionEnd() {
        byteArrayOutputStream.writeBytes("\n\n\n".getBytes());
    }

    @Override
    public ByteArrayOutputStream end() {
        if (byteArrayOutputStream == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }
        return byteArrayOutputStream;
    }
}
