/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;

public class ReportGeneratorPDF implements ReportGenerator {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final String REPORT_DATE_FORMAT = "d. MMMM yyyy";
    private final String FOP_INPUT_FILE_DOCUMENT = "doc-template/template-document.fo.xml";
    private final String FOP_INPUT_FILE_CONTENT  = "doc-template/template-progress.fo.xml";

    private ReportI18n reportI18n;

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

    public void setLocale(@NonNull final ReportI18n reportI18n) {
        this.reportI18n = reportI18n;
    }

    @Override
    public void begin() {
        if (fop != null) {
            throw new IllegalStateException("Call end() before beginning a new PDF generation!");
        }

        if (reportI18n == null) {
            loadDefaultLocalization();
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

    protected void loadDefaultLocalization() {
        LOGGER.info("Loading default localization: 'EN'");
        try {
            this.reportI18n = ReportI18n.build(ReportI18n.Locale.EN);
        } catch (Exception exception) {
            LOGGER.error("Could not load report generator's localization, reason: {}", exception.getMessage());
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
            String dateString = LocalDateTime.ofInstant(progress.getDateCreation(),
                    ZoneOffset.systemDefault()).format(DateTimeFormatter.ofPattern(REPORT_DATE_FORMAT + " - HH:mm"));

            LocalDate date = progress.getReportWeek();
            int reportWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int reportYear = date.get(IsoFields.WEEK_BASED_YEAR);

            String progressHeader =
                            reportI18n.translate("author") + ": " + encodeFoString(progress.getOwnerName()) + ", " +
                            reportI18n.translate("created") + ": " + encodeFoString(dateString) + ", " +
                            reportI18n.translate("calendar.week") + ": " +  encodeFoString("" + reportWeek + "/" + reportYear);

            String progressSection = progressTemplate.replace("@HEADER@", progressHeader);

            StringBuffer tags = new StringBuffer();
            if (progress.getTags() != null && !progress.getTags().isEmpty()) {
                progress.getTags().forEach((tag) -> tags.append(tag.getName() + " "));
            }
            progressSection = progressSection.replace("@TAGS@", encodeFoString(tags.toString()));

            progressSection = progressSection.replace("@TEXTTITLE@", encodeFoString(progress.getTitle()));
            progressSection = progressSection.replace("@TEXT@", addMultiLineText(progress.getText()));

            taskNames.add(progress.getTask().getTitle());

            section.append(progressSection);
        });

        String sectionContent = section.toString();
        if (!sectionContent.isEmpty()) {
            documentContentFo += "\n<fo:block space-after=\"12pt\" text-align=\"left\">\n" +
                                 "\n  <fo:inline font-weight=\"bold\">" +
                                 encodeFoString(currentSectionTitle) + encodeFoString(" - " + String.join(", ", taskNames)) +
                                 "</fo:inline>\n" +
                                 "\n</fo:block>\n";

            documentContentFo += sectionContent;
        }
    }

    @NonNull
    protected String encodeFoString(@Nullable final String text) {
        if (text == null) {
            return "";
        }
        // NOTE the usual html escaping (Spring's and Apache's) converts also the UTF8 leading to a problem in FOP Sax parser.
        // We take Bruno Eberhard's solution: https://stackoverflow.com/questions/1265282/recommended-method-for-escaping-html-in-java
        StringBuilder out = new StringBuilder(Math.max(16, text.length()));
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    @NonNull
    protected String addMultiLineText(@Nullable final String text) {
        if (text == null) {
            return "";
        }
        String multiLineText = "";
        String textLines[] = text.split("\n");
        for (String line: textLines) {
            multiLineText += "\n<fo:block space-after=\"6pt\" >" + encodeFoString(line) + "</fo:block>\n";
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
