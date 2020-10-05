/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.task.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@RunWith(SpringRunner.class)
public class ReportGeneratorPDFTest {

    private ReportGeneratorPDF reportGeneratorPDF;

    @Before
    public void setup() {
        reportGeneratorPDF = (ReportGeneratorPDF) ReportGeneratorFactory.build(ReportFormat.PDF);
    }

    @Test
    public void generateDocument() {
        reportGeneratorPDF.begin();
        reportGeneratorPDF.generateCoverPage("Test", "Test Document");
        reportGeneratorPDF.sectionBegin("My Report Section");
        reportGeneratorPDF.sectionAppend(createProgressList());
        reportGeneratorPDF.sectionEnd();
        reportGeneratorPDF.setFooter("My Document Footer");

        ByteArrayOutputStream document = reportGeneratorPDF.end();

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
        assertThatThrownBy(() -> reportGeneratorPDF.generateCoverPage("Test", "Test Document")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> reportGeneratorPDF.sectionAppend(new ArrayList<>())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> reportGeneratorPDF.end()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void generateDocumentSubsequentBegin() {
        reportGeneratorPDF.begin();

        assertThatThrownBy(() -> reportGeneratorPDF.begin()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void encodeFoString() {
        assertThat(reportGeneratorPDF.encodeFoString(null)).isEqualTo("");

        String foString = reportGeneratorPDF.encodeFoString("ÖÄÜöäüß");

        assertThat(foString).isEqualTo("&#214;&#196;&#220;&#246;&#228;&#252;&#223;");

        foString = reportGeneratorPDF.encodeFoString("<fo name=\"injection-attempt\">&\\</fo>");

        assertThat(foString).isEqualTo("&#60;fo name=&#34;injection-attempt&#34;&#62;&#38;\\&#60;/fo&#62;");
    }

    @Test
    public void addMultiLineText() {
        assertThat(reportGeneratorPDF.addMultiLineText(null)).isEqualTo("");

        String multiLineFoText = reportGeneratorPDF.addMultiLineText("Hello\nWorld");

        assertThat(multiLineFoText).contains("<fo:block");
        assertThat(multiLineFoText).contains("Hello");
        assertThat(multiLineFoText).contains("World");
    }
}