/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.task.Progress;
import org.apache.fop.apps.*;
import org.apache.tools.ant.filters.StringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGeneratorPDF implements ReportGenerator {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    final String FOP_INPUT_FILE_DOCUMENT = "doc-template/template-document.fo.xml";
    final String FOP_INPUT_FILE_CONTENT  = "doc-template/template-progress.fo.xml";

    private FopFactory fopFactory;
    private Fop fop;

    private ByteArrayOutputStream outputStream;
    private String progressTemplate;
    private String documentTemplate;
    private String documentContentFo = "";

    private String documentTitle;
    private String documentSubTitle;
    private String currentSectionTitle;
    private String documentFooter;

    protected ReportGeneratorPDF() {}

    @Override
    public void begin() {
        if (fop != null) {
            throw new IllegalStateException("Call end() before beginning a new PDF generation!");
        }

        Resource resourceDocument = new ClassPathResource(FOP_INPUT_FILE_DOCUMENT);
        Resource resourceContent = new ClassPathResource(FOP_INPUT_FILE_CONTENT);
        try (InputStream documentInputStream = resourceDocument.getInputStream();
             InputStream progressInputStream = resourceContent.getInputStream()) {

            fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            outputStream = new ByteArrayOutputStream();
            fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream);

            documentTemplate = new String(documentInputStream.readAllBytes());
            progressTemplate = new String(progressInputStream.readAllBytes());

        } catch (FOPException | IOException exception) {
            LOGGER.error("Could not create a FOP instance, reason: {}", exception.getMessage());
            throw new IllegalStateException("Could not create a FOP instance, reason: " + exception.getMessage());
        }
    }

    @Override
    public void setFooter(@Nullable final String footer) {
        documentFooter = "" + footer;
    }

    @Override
    public void generateCoverPage(@NonNull final String title, String subTitle) {
        if (fop == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        documentTitle = title;
        documentSubTitle = subTitle;
    }

    @Override
    public void sectionBegin(@NonNull final String title) {
        currentSectionTitle = title;
    }

    @Override
    public void sectionAppend(@NonNull final List<Progress> progressList) {
        if (fop == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        List<Progress> sortedProgressByOwnerAndCalendarWeek = sortByOwnerAndCalendarWeek(progressList);

        final StringBuffer section = new StringBuffer();
        final Set<String> taskNames = new HashSet<>();

        sortedProgressByOwnerAndCalendarWeek.forEach((progress) -> {

            String progressSection = progressTemplate.replace("@AUTHOR@", encodeHtml(progress.getOwnerName()));

            String dateString = LocalDateTime.ofInstant(progress.getDateCreation(),
                    ZoneOffset.systemDefault()).format(DateTimeFormatter.ofPattern("MM.dd.yyyy - HH:mm"));
            progressSection = progressSection.replace("@DATE@", encodeHtml(dateString));

            LocalDate date = progress.getReportWeek();
            int reportWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int reportYear = date.get(IsoFields.WEEK_BASED_YEAR);
            progressSection = progressSection.replace("@WEEK@", encodeHtml("" + reportYear + "/" + reportWeek));

            progressSection = progressSection.replace("@TASK@", encodeHtml(progress.getTask().getTitle()));

            StringBuffer tags = new StringBuffer();
            if (progress.getTags() != null && !progress.getTags().isEmpty()) {
                progress.getTags().forEach((tag) -> tags.append(tag.getName() + " "));
            }
            progressSection = progressSection.replace("@TAGS@", encodeHtml(tags.toString()));

            progressSection = progressSection.replace("@TEXTTITLE@", encodeHtml(progress.getTitle()));
            progressSection = progressSection.replace("@TEXT@", addMultiLineText(progress.getText()));

            taskNames.add(progress.getTask().getTitle());

            section.append(progressSection);
        });

        String sectionContent = section.toString();
        if (!sectionContent.isEmpty()) {
            documentContentFo += "\n<fo:block space-after=\"12pt\" text-align=\"left\">\n" +
                                 "\n  <fo:inline font-weight=\"bold\">" +
                                 encodeHtml(currentSectionTitle) + encodeHtml(" - " + String.join(", ", taskNames)) +
                                 "</fo:inline>\n" +
                                 "\n</fo:block>\n";

            documentContentFo += sectionContent;
        }
    }

    @Nullable
    protected String encodeHtml(@Nullable final String text) {
        return (text == null) ? "" : HtmlUtils.htmlEscape(text);
    }

    @NonNull
    protected String addMultiLineText(@Nullable final String text) {
        if (text == null) {
            return "";
        }
        String multiLineText = "";
        String textLines[] = text.split("\n");
        for (String line: textLines) {
            multiLineText += "\n<fo:block space-after=\"6pt\" >" + encodeHtml(line) + "</fo:block>\n";
        }
        return multiLineText;
    }

    @Override
    public void sectionEnd() {
        documentContentFo += "<fo:block page-break-before=\"auto\"></fo:block>\n";
    }

    @Override
    public ByteArrayOutputStream end() {
        if (fop == null) {
            throw new IllegalStateException("Generator was not initialized by calling begin()");
        }

        String totalDocument = documentTemplate.replace("@TITLE@", addMultiLineText(documentTitle));
        totalDocument = totalDocument.replace("@SUBTITLE@", addMultiLineText(documentSubTitle));
        totalDocument = totalDocument.replace("@__CONTENT__@", documentContentFo);
        totalDocument = totalDocument.replace("@PAGE_HEADER@", documentTitle);
        totalDocument = totalDocument.replace("@PAGE_FOOTER@", StringUtils.isEmpty(documentFooter) ? "" : documentFooter);

        TransformerFactory factory = TransformerFactory.newInstance();
        InputStream inputStreamFo = null;
        try {
            Transformer transformer = factory.newTransformer();
            inputStreamFo = new StringInputStream(totalDocument);
            Source src = new StreamSource(inputStreamFo);
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);

            return outputStream;

        } catch (TransformerConfigurationException | FOPException exception) {
            LOGGER.error("Could not finalize PDF document generation, reason: {}", exception.getMessage());
            throw new IllegalStateException(exception.getMessage());
        } catch (TransformerException exception) {
            LOGGER.error("Could not finalize PDF document generation, reason: {}", exception.getMessage());
            throw new IllegalStateException(exception.getMessage());
        } finally {
            try {
                inputStreamFo.close();
            } catch (IOException ignored) {
            }
            documentContentFo = "";
        }
    }
}
