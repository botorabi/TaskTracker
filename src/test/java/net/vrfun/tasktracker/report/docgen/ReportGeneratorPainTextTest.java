/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.task.Progress;
import net.vrfun.tasktracker.task.Tag;
import net.vrfun.tasktracker.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



public class ReportGeneratorPainTextTest {

    private ReportGeneratorPlainText reportGeneratorPlainText;

    @BeforeEach
    public void setup() {
        reportGeneratorPlainText = (ReportGeneratorPlainText) ReportGeneratorFactory.build(ReportFormat.PlainText);
    }

    @Test
    public void generateDocument() {
        reportGeneratorPlainText.begin();
        reportGeneratorPlainText.generateCoverPage("Test", "Test Document");
        reportGeneratorPlainText.sectionBegin("My Report Section");
        reportGeneratorPlainText.sectionAppend(createProgressList());
        reportGeneratorPlainText.sectionEnd();
        reportGeneratorPlainText.setFooter("My Document Footer");

        ByteArrayOutputStream document = reportGeneratorPlainText.end();

        assertThat(document).isNotNull();
        assertThat(document.size()).isGreaterThan(0);
    }

    @NonNull
    private List<Progress> createProgressList() {
        List<Progress> progressList = new ArrayList<>();
        Progress progress = new Progress("Tester", 100L);
        progress.setId(42L);
        progress.setReportWeek(LocalDate.now());
        progress.setTask(new Task("My Task"));
        progress.setTitle("My Progress");
        progress.setText("My Text");
        progress.setTags(Arrays.asList(new Tag("Tag1")));
        progress.setOwnerName("My Name");

        progressList.add(progress);

        return progressList;
    }

    @Test
    public void generateDocumentMissingBegin() {
        assertThatThrownBy(() -> reportGeneratorPlainText.generateCoverPage("Test", "Test Document")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> reportGeneratorPlainText.sectionAppend(new ArrayList<>())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> reportGeneratorPlainText.end()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void generateDocumentSubsequentBegin() {
        reportGeneratorPlainText.begin();

        assertThatThrownBy(() -> reportGeneratorPlainText.begin()).isInstanceOf(IllegalStateException.class);
    }
}