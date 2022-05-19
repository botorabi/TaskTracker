package net.vrfun.tasktracker.report;

import java.util.Comparator;
import java.util.regex.Pattern;

public class ReportSectionFieldComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {

        var pattern = Pattern.compile("W\\d.\\D+([\\d-]*)");
        var matcher1 = pattern.matcher(o1);
        var matcher2 = pattern.matcher(o2);
        if (matcher1.find() && matcher2.find()) {
            int week1 = numericalWeek(matcher1.group(1));
            int week2 = numericalWeek(matcher2.group(1));
            return week1 - week2;
        } else {
            return o1.compareTo(o2);
        }

    }
    int numericalWeek(final String text) {
        return Integer.parseInt(text.replaceAll("-", ""));
    }
}
