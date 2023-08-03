package me.braydon.plugin;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Braydon
 */
@Mojo(name = "versioning", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public final class VersioningMojo extends AbstractMojo {
    /**
     * The regex to use to replace target versions.
     */
    private static final Pattern TARGET_REGEX = Pattern.compile("(?i)(-snapshot|-dev)");
    
    /**
     * The {@link MavenProject} to use for
     * reading and writing pom data.
     */
    @Component private MavenProject project;
    
    @Override
    public void execute() {
        Matcher matcher = TARGET_REGEX.matcher(project.getVersion()); // Create a matcher for the version
        if (!matcher.find()) { // Matcher found nothing, aka the version is not a target
            return;
        }
        String target = matcher.group(); // Extract the found target version

        // Fetch git data
        System.out.println("Retrieving git data..."); // Log git data retrieval
        int depth = 0; // The amount of commits since the last tag
        String commitHash = "unknown"; // The commit hash of the current commit
        
        Map.Entry<String, Integer> gitData = GitUtils.getGitData(); // Get the git data
        if (gitData == null) { // No git data
            System.out.println("Failed to retrieve git data, is this a git repository?");
        } else {
            depth = gitData.getValue(); // Set the depth
            commitHash = gitData.getKey(); // Set the commit hash
        }
        
        // Updating the output jar name
        Build build = project.getBuild(); // Get the build
        build.setFinalName(build.getFinalName().replace(target, "-dev-" + depth + "-" + commitHash));
    }
}