package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Report {

    private List<ReportSection> sections;

    private Stream<Progress> progresses;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public Report()
    {
        sections = new ArrayList<>();
        progresses = new ArrayList<Progress>().stream();
    }

    @NonNull
    public List<ReportSection> getSections() {
        return sections;
    }

    public void addProgress(@NonNull Stream<Progress> progress)
    {
        try {
            this.progresses = Stream.concat(this.progresses, progress);
        } catch (IllegalStateException e) {
            LOGGER.debug("Cannot concatenate progress, because previous progress stream has already been used. Resetting report progresses instead! ");
            resetProgress(progress);
        }
    }

    public void resetProgress(@NonNull Stream<Progress> progresses) {
        this.progresses = progresses;
    }

    public void sortSectionsBy(ReportSortType type)
    {
        Function<Collection<Progress>, List<ReportSection>> sorter;
        switch (type)
        {
            case REPORT_SORT_TYPE_TASK: {
                sorter = ReportSorter::sortByTask;
                break;
            }

            case REPORT_SORT_TYPE_TEAM: {
                sorter = ReportSorter::sortByTeam;
                break;
            }

            case REPORT_SORT_TYPE_USER: {
                sorter = ReportSorter::sortByUser;
                break;
            }

            case REPORT_SORT_TYPE_WEEK: {
                sorter = ReportSorter::sortByWeek;
                break;
            }

            default: {
                return;
            }
        }
        Collection<Progress> progressCollection;
        try {
            progressCollection = progresses.collect(Collectors.toList());
        } catch (IllegalStateException e) {
            LOGGER.error("Cannot resort the Report! Try resetting the progress stream first.");
            return;
        }
        sections = sorter.apply(progressCollection);
    }
}
