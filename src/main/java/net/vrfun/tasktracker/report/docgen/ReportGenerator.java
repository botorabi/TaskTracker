/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import net.vrfun.tasktracker.task.Progress;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Interface for various report generators
 *
 * @author          boto
 * Creation Date    September 2020
 */
interface ReportGenerator {

    /**
     * Set report generator's locale.
     */
    void setLocale(@NonNull final ReportI18n reportI18n);

    /**
     * Begin report generation.
     */
    void begin();

    /**
     * Set the document footer text.
     */
    void setFooter(@Nullable final String footer);

    /**
     * Generate the cover page.
     */
    void generateCoverPage(@NonNull final String title, @NonNull final String subTitle);

    /**
     * Begin a new document section.
     */
    void sectionBegin(@NonNull final String title);

    /**
     * Append progress entries to current section.
     */
    void sectionAppend(@NonNull final List<Progress> progressList);

    /**
     * End of current section
     */
    void sectionEnd();

    /**
     * Finalize the report generation delivering the document in an output stream.
     * The caller is responsible to close the stream when no longer needed.
     */
    ByteArrayOutputStream end();

    /**
     * Sort the progress entries by owner and calendar week.
     */
    default List<Progress> sortByOwnerAndCalendarWeek(@NonNull final List<Progress> progressList) {
        List<Progress> sortedProgressList = new ArrayList<>();
        HashMap<String /*owner*/, List<Progress>> sortedMap = new HashMap<>();
        List<String> owners = new ArrayList<>();

        progressList.sort(Comparator.comparing(Progress::getOwnerName));
        progressList.forEach((progress -> {
            if (!sortedMap.containsKey(progress.getOwnerName())) {
                sortedMap.put(progress.getOwnerName(), new ArrayList<>());
                owners.add(progress.getOwnerName());
            }
            sortedMap.get(progress.getOwnerName()).add(progress);
        }));

        owners.forEach((ownerName) -> sortedMap.get(ownerName).sort(Comparator.comparing(Progress::getReportWeek).reversed()));
        owners.sort(Comparator.comparing(String::toUpperCase));
        owners.forEach((ownerName) -> sortedProgressList.addAll(sortedMap.get(ownerName)));

        return sortedProgressList;
    }
}
