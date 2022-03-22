/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.springframework.lang.NonNull;

import java.util.*;

/**
 * Paged progress data transfer object
 *
 * @author          boto
 * Creation Date    Oktober 2020
 */
public class ProgressPagedDTO {

    private long totalCount;
    private long currentPage;

    private List<ProgressDTO> progressList;

    public ProgressPagedDTO() {}

    public ProgressPagedDTO(long totalCount,
                            long currentPage,
                            @NonNull final List<ProgressDTO> progressList) {

        this.setTotalCount(totalCount);
        this.setCurrentPage(currentPage);
        this.setProgressList(progressList);
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public List<ProgressDTO> getProgressList() {
        return progressList;
    }

    public void setProgressList(List<ProgressDTO> progressList) {
        this.progressList = progressList;
    }
}
