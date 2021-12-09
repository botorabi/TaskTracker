package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Report {

    private List<ReportSection> sections;

    private Stream<Progress> progresses;

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
        this.progresses = Stream.concat(this.progresses, progress);
    }

    public void sortSectionsBy(ReportSortType type)
    {
//        switch (type)
    }
}
