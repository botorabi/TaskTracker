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

    private String sectionTitle;
    private List<String> sectionBody;

    public ReportSection(@NonNull String sectionTitle, @NonNull List<String> sectionBody)
    {
        this.sectionBody = sectionBody;
        this.sectionTitle = sectionTitle;
    }

    @NonNull
    public String getSectionTitle()
    {
        return sectionTitle;
    }

    @NonNull
    public List<String> getSectionBody()
    {
        return sectionBody;
    }

}
