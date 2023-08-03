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
 * The purpose of this mojo is to uniquely identify
 * development builds of projects by modifying the
 * output jar name to contain a commit depth, as well
 * as a commit hash.
 *
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
        
        long before = System.currentTimeMillis(); // The time before retrieving git data
        Map.Entry<String, Integer> gitData = GitUtils.getGitData(); // Get the git data
        if (gitData == null) { // No git data
            System.out.println("Failed to retrieve git data, is this a git repository?");
        } else {
            depth = gitData.getValue(); // Set the depth
            commitHash = gitData.getKey().substring(0, 7); // Set the commit hash (trimmed)
            System.out.println("Commits since last tag (depth): " + depth);
            System.out.println("Last commit: " + commitHash);
        }
        System.out.println("Data retrieval took " + (System.currentTimeMillis() - before) + "ms");
        
        // Updating the output jar name
        Build build = project.getBuild(); // Get the build
        build.setFinalName(build.getFinalName().replace(target, String.format("-dev-%s-%s",
            depth, commitHash
        )));
    }
}