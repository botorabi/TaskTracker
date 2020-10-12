/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import com.sun.istack.ByteArrayDataSource;
import net.vrfun.tasktracker.report.docgen.ReportFormat;
import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Report generation service used by scheduler
 *
 * @author          boto
 * Creation Date    September 2020
 */
@Service
@Transactional
public class ReportGeneratorService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final JavaMailSender emailSender;
    private final ReportMailConfigurationRepository reportMailConfigurationRepository;
    private final ReportComposer reportComposer;

    @Autowired
    public ReportGeneratorService(@NonNull final JavaMailSender emailSender,
                                  @NonNull final ReportMailConfigurationRepository reportMailConfigurationRepository,
                                  @NonNull final ReportComposer reportComposer) {

        this.emailSender = emailSender;
        this.reportMailConfigurationRepository = reportMailConfigurationRepository;
        this.reportComposer = reportComposer;
    }

    public void generateReport(@NonNull final Long configurationID) {
        Optional<ReportMailConfiguration> foundConfiguration = reportMailConfigurationRepository.findById(configurationID);
        if (!foundConfiguration.isPresent()) {
            LOGGER.warn("Report generation configuration with ID {} no longer exists, skipping", configurationID);
            return;
        }

        ReportMailConfiguration configuration = foundConfiguration.get();

        if (configuration.getReportingTeams() == null &&
            configuration.getMasterRecipients() == null) {
            LOGGER.info("No report recipients configured for '{}' ({}), skip reporting.", configuration.getName(), configurationID);
            return;
        }

        LOGGER.info("Start generating report: '{}' ({})", configuration.getName(), configurationID);

        List<Team> reportingTeams = null;
        List<User> masterRecipients = null;

        if (configuration.getReportingTeams() != null) {
            reportingTeams = configuration.getReportingTeams().stream().collect(Collectors.toList());
        }
        if (configuration.getMasterRecipients() != null) {
            masterRecipients = configuration.getMasterRecipients().stream().collect(Collectors.toList());
        }

        sendTeamLeadEMails(configuration, reportingTeams, masterRecipients);
    }

    protected void sendTeamLeadEMails(@NonNull final ReportMailConfiguration configuration,
                                      @Nullable final List<Team> teams,
                                      @Nullable final List<User> masterRecipients) {
        teams.forEach((team) -> {
            String recipients = "";
            try {
                recipients = getAllRecipients(team, masterRecipients);
                if (!recipients.isEmpty()) {
                    ByteArrayOutputStream reportDocument =
                            reportComposer.createTeamReportCurrentWeek(
                                    Arrays.asList(team.getId()),
                                    ReportFormat.PDF,
                                    configuration.getReportTitle(),
                                    configuration.getReportSubTitle());

                    String cleanMailSender = configuration.getMailSenderName().trim().replace(" ", "-");
                    cleanMailSender = cleanMailSender.replaceAll("\\P{Print}", "");

                    LOGGER.info(" Sending report mail '{}' to recipients: '{}'", configuration.getMailSubject(), recipients);

                    sendMail(cleanMailSender,
                            recipients,
                            configuration.getMailSubject(),
                            configuration.getMailText(),
                            reportDocument);
                }
                else {
                    LOGGER.warn(" Could not send report mail, no recipients for configuration {}", configuration.getName());
                }
            } catch (MessagingException exception) {
                LOGGER.error(" Could not create report for configuration {}, reason: '{}', recipients: {}",
                        configuration.getName(), exception.getMessage(), recipients);
            }
        });
    }

    @NonNull
    protected String getAllRecipients(@Nullable final Team team, @Nullable final List<User> masterRecipients) {
        Set<String> allRecipients = new HashSet<>();
        if (team != null) {
            team.getTeamLeaders().forEach((user) -> appendUserEmailAddress(allRecipients, user));
        }
        if (masterRecipients != null) {
            masterRecipients.forEach((user) -> appendUserEmailAddress(allRecipients, user));
        }
        if (allRecipients.isEmpty()) {
            return "";
        }
        return String.join(",", allRecipients);
    }

    protected void appendUserEmailAddress(@NonNull final Set<String> emails, @NonNull final User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            emails.add(user.getEmail());
        }
        else {
            LOGGER.info(" User '{}' ({}) hand and an invalid email address!", user.getRealName(), user.getLogin());
        }
    }

    protected void sendMail(@NonNull  final String from,
                            @NonNull  final String to,
                            @NonNull  final String subject,
                            @Nullable final String text,
                            @NonNull  final ByteArrayOutputStream attachment) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(InternetAddress.parse(to));
        helper.setSubject(subject);

        String body = (text != null ? text + "\n\n" : "");
        helper.setText(body);

        DataSource attachmentSource = new ByteArrayDataSource(attachment.toByteArray(), MediaType.APPLICATION_PDF_VALUE);
        helper.addAttachment("Report.pdf", attachmentSource);

        try {
            emailSender.send(message);

        } catch(Throwable throwable) {
            LOGGER.warn(" Could not send report mail, reason: {}", throwable.getMessage());
        }
    }
}
