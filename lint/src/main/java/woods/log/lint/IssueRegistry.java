package woods.log.lint;


import com.android.tools.lint.detector.api.Issue;

import java.util.Arrays;
import java.util.List;


/**
 * This code is a copy from JakeWharton's timber project
 * You can find the original code at Github. Url: "https://github.com/JakeWharton/timber"
 */


public final class IssueRegistry extends com.android.tools.lint.client.api.IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(WrongTimberUsageDetector.getIssues());
    }
}
