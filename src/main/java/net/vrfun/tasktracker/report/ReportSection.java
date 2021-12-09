package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Stream;

public class ReportSection {

    private String sectionTitle;
    private Stream<Progress> sectionProgess;

    public ReportSection(@NonNull String sectionTitle, @NonNull Stream<Progress> sectionProgess)
    {
        this.sectionProgess = sectionProgess;
        this.sectionTitle = sectionTitle;
    }

    @NonNull
    public String getSectionTitle()
    {
        return sectionTitle;
    }

    public void setSectionTitle(@NonNull String sectionTitle)
    {
        this.sectionTitle = sectionTitle;
    }

    @NonNull
    public Stream<Progress> getSectionProgess()
    {
        return sectionProgess;
    }

    public void setSectionProgess(@NonNull Stream<Progress> sectionProgess)
    {
        this.sectionProgess = sectionProgess;
    }
}
