package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.TeamRepository;
import net.vrfun.tasktracker.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Report job scheduler
 *
 * @author          boto
 * Creation Date    September 2020
 */
@Component
public class ReportGeneratorScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Reports reports;
    private final JavaMailSender emailSender;
    private final TeamRepository teamRepository;
    private final ReportGeneratorConfigurationRepository reportGeneratorConfigurationRepository;

    @Autowired
    public ReportGeneratorScheduler(@NonNull final Reports reports,
                                    @NonNull final JavaMailSender emailSender,
                                    @NonNull final TeamRepository teamRepository,
                                    @NonNull final ReportGeneratorConfigurationRepository reportGeneratorConfigurationRepository) {

        this.reports = reports;
        this.emailSender = emailSender;
        this.teamRepository = teamRepository;
        this.reportGeneratorConfigurationRepository = reportGeneratorConfigurationRepository;
    }

    //! TODO the schedule must be a matter of configuration, e.g. once weekly, once monthly etc.
    //@Scheduled(cron = "0 0 18 * * *")
    @Scheduled(fixedRate = 10000)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public void generateReports() {
        LOGGER.info("Starting cron Job for generating reports");

        List<ReportGeneratorConfiguration> configs = reportGeneratorConfigurationRepository.findAll();
        if (configs.isEmpty()) {
            LOGGER.info(" No report generation configuration exist, skip reporting.");
            return;
        }

        configs.forEach((configuration) -> {
            if (configuration.getReportingTeams() == null &&
                configuration.getMasterRecipients() == null) {
                LOGGER.info(" No report recipients configured, skip reporting.");
            }

            List<Team> reportingTeams = null;
            List<User> masterRecipients = null;

            if (configuration.getReportingTeams() != null) {
                reportingTeams = configuration.getReportingTeams().stream().collect(Collectors.toList());
            }
            if (configuration.getMasterRecipients() != null) {
                masterRecipients = configuration.getMasterRecipients().stream().collect(Collectors.toList());
            }

            sendTeamLeadEMails(configuration, reportingTeams, masterRecipients);
        });
    }

    protected void sendTeamLeadEMails(@NonNull final ReportGeneratorConfiguration configuration,
                                      @Nullable final List<Team> teams,
                                      @Nullable final List<User> masterRecipients) {
        teams.forEach((team) -> {
            try {
                String recipients = getAllRecipients(team, masterRecipients);
                ByteArrayResource reportFile = reports.createTeamReportTextCurrentWeek(Arrays.asList(team.getId()));
                sendMail(configuration.getMailSenderName(),
                        recipients,
                        configuration.getMailSubject(),
                        configuration.getMailText(),
                        reportFile);

            } catch (IOException | MessagingException exception) {
                LOGGER.error(" Could not create report, reason: {}", exception.getMessage());
            }
        });
    }

    @NonNull
    protected String getAllRecipients(@Nullable final Team team, @Nullable final List<User> masterRecipients) {
        List<String> allRecipients = new ArrayList<>();
        if (team != null) {
            team.getTeamLeaders().stream()
                    .map((user) -> allRecipients.add(user.getEmail()));
        }
        if (masterRecipients != null) {
            masterRecipients.stream()
                    .map((user) -> allRecipients.add(user.getEmail()));
        }
        if (allRecipients.isEmpty()) {
            return "";
        }
        return String.join(",", allRecipients);
    }

    protected void sendMail(@NonNull  final String from,
                            @NonNull  final String to,
                            @NonNull  final String subject,
                            @Nullable final String text,
                            @NonNull  final ByteArrayResource attachment) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        String body = (text != null ? text + "\n\n" : "") + new String(attachment.getByteArray());
        helper.setText(body);

        helper.addAttachment("Report.txt", attachment);

        emailSender.send(message);
    }
}
