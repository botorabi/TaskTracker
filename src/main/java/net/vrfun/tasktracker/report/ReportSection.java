/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import org.springframework.lang.NonNull;
import java.util.List;

public class ReportSection {

    private final String title;
    private final List<ReportSectionBody> sectionBody;

    public ReportSection(@NonNull final String sectionTitle, @NonNull final List<ReportSectionBody> sectionBody)
    {
        this.sectionBody = sectionBody;
        this.title = sectionTitle;
    }

    @NonNull
    public String getSectionTitle()
    {
        return title;
    }

    @NonNull
    public List<ReportSectionBody> getSectionBody()
    {
        return sectionBody;
    }

}
