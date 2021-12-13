package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.task.Progress;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Stream;

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
