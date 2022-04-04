/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class ReportEncoderFop {

    private final static String FOP_REPORT_FORMAT = "doc-templates/report_base.xsl";
    private final static String FOP_REPORT_TEMPLATE  = "doc-templates/template-report.xml";
    private final static String FOP_PAGES_TEMPLATE  = "doc-templates/template-standard_pages.xml";
    private final static String FOP_CONTENT_IMAGE_TEMPLATE  = "doc-templates/template-content-image.xml";
    private final static String FOP_CONTENT_TEXT_TEMPLATE  = "doc-templates/template-content-text.xml";
    private final static String FOP_CONTENT_TITLE_TEMPLATE  = "doc-templates/template-content-title.xml";
    private final static String FOP_CONTENT_SUBTTITLE_TEMPLATE = "doc-templates/template-content-subtitle.xml";
    private final static String FOP_CONTENT_METAINFORMATION_TEMPLATE = "doc-templates/template-content-metainformation.xml";

    private final String imageTemplate;
    private final String textTemplate;
    private final String contentTitleTemplate;
    private final String contentSubTitleTemplate;
    private final String contentMetaInfoTemplate;
    private final String reportFormat;
    private final String pagesTemplate;
    private String currentPages;
    private String report;
    private StringBuffer content;

    private final ReportFormat encodingType;
    private final FopFactory fopFactory;
    private OutputStream out;

    public ReportEncoderFop(@NonNull final ReportFormat type) {
        if (type == ReportFormat.Unknown) {
            throw new RuntimeException("Invalid DocumentEncoding used for FOP encoder!");
        }
        encodingType = type;

        try {
            pagesTemplate = getClassPathString(FOP_PAGES_TEMPLATE);
            report = getClassPathString(FOP_REPORT_TEMPLATE);
            reportFormat = getClassPathString(FOP_REPORT_FORMAT);
            textTemplate = getClassPathString(FOP_CONTENT_TEXT_TEMPLATE);
            imageTemplate = getClassPathString(FOP_CONTENT_IMAGE_TEMPLATE);
            contentTitleTemplate = getClassPathString(FOP_CONTENT_TITLE_TEMPLATE);
            contentSubTitleTemplate = getClassPathString(FOP_CONTENT_SUBTTITLE_TEMPLATE);
            contentMetaInfoTemplate = getClassPathString(FOP_CONTENT_METAINFORMATION_TEMPLATE);

            currentPages = pagesTemplate;
            content = new StringBuffer();

            fopFactory = FopFactory.newInstance(new File(".").toURI());

        } catch (IOException exception) {
            throw new RuntimeException("Could not create a FOP instance, reason: " + exception.getMessage());
        }
    }

    public void start(@NonNull final BufferedOutputStream document) {
        out = document;
    }

    public void setTitle(@NonNull final String title, @NonNull final String subTitle) {
        report = report.replace("@TITLE@", StringEscapeUtils.escapeXml(title));
        report = report.replace("@SUBTITLE@", StringEscapeUtils.escapeXml(subTitle));
    }

    public void setHeader(@NonNull final String header) {
        currentPages = currentPages.replace("@HEADER@", StringEscapeUtils.escapeXml(header));
    }

    public void setFooter(@NonNull final String footer) {
        currentPages = currentPages.replace("@FOOTER@", StringEscapeUtils.escapeXml(footer));
    }

    public void addSectionTitle(@NonNull final String text) {
        appendFilledTextTemplateToContent(contentTitleTemplate, text);
    }
    public void addSubTitle(@NonNull final String text) {
        appendFilledTextTemplateToContent(contentSubTitleTemplate, text);
    }

    public void addMetaInfo(@NonNull final String text) {
        appendFilledTextTemplateToContent(contentMetaInfoTemplate, text);
    }

    public void addText(@NonNull final String text) {
        appendFilledTextTemplateToContent(textTemplate, text);
    }

    public void addImage(@NonNull final String imageUri) {
        String imageContent = imageTemplate.replace("@IMAGE@", StringEscapeUtils.escapeXml(imageUri));
        content.append(imageContent);
    }

    public void addImage(@NonNull final String base64EncodedString, @NonNull final ReportFormat imageType) {
        StringBuilder prefix = new StringBuilder("data:image/");
        switch (imageType) {
            case PNG: {
                prefix.append("png");
                break;
            }
            case BITMAP: {
                prefix.append("bmp");
                break;
            }
            default: {
                throw new RuntimeException("Could not add image to Fop, unknown type!");
            }
        }
        prefix.append(";base64,");
        String imageContent = imageTemplate.replace("@IMAGE@", prefix + StringEscapeUtils.escapeXml(base64EncodedString));
        content.append(imageContent);
    }

    public void addPageBreak() {
        setContent();
        report = report.replace("@PAGES@", currentPages + "\n@PAGES@");
        currentPages = pagesTemplate;
    }

    public void end() {
        if (content.length() == 0) {
            addText("");
        }
        setContent();
        report = report.replace("@PAGES@", currentPages);
        try {
            Fop fop;
            switch (encodingType) {
                case PDF: {
                    fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
                    break;
                }

                case BITMAP: {
                    throw new RuntimeException("Currently there is no bitmap output support");
                }

                case PNG: {
                    fop = fopFactory.newFop(MimeConstants.MIME_PNG, out);
                    break;
                }

                default: {
                    return; //Should be unreachable if constructor acts correctly (LFU)
                }
            }

            Source xsltSource = new StreamSource(new StringReader(reportFormat));
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(xsltSource);

            Source xmlSource = new StreamSource(new StringReader(report));
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(xmlSource, result);
            out.flush();

        } catch (Exception exception) {
            throw new RuntimeException("Failed at encoding file, reason: " + exception.getMessage());
        }
    }

    private void appendFilledTextTemplateToContent(final String template, String textReplacement) {
        String textContent = template.replace("@TEXT@", ("\n" + StringEscapeUtils.escapeXml(textReplacement) + "\n"));
        content.append(textContent);
    }

    private void setContent() {
        currentPages = currentPages.replace("@CONTENT@", content.toString());
        setHeader("");
        setFooter("");
    }

    @NonNull
    private String getClassPathString(@NonNull final String classPath) throws IOException {
        Resource resource = new ClassPathResource(classPath);
        String returnValue;
        try(InputStream inputStream = resource.getInputStream()) {
            returnValue = new String(inputStream.readAllBytes());
        }
        return returnValue;
    }
}
