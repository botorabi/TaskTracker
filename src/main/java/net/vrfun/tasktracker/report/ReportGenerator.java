/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import org.springframework.lang.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.List;


/**
 * Interface for various report generators
 *
 * @author          boto
 * Creation Date    September 2020
 */
public interface ReportGenerator {

    /**
     * Begin report generation.
     */
    ByteArrayOutputStream begin();

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
}
