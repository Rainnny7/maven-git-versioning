package me.braydon.plugin;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * @author Braydon
 */
public final class GitUtils {
    /**
     * Can't construct utility classes.
     */
    public GitUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }
    
    /**
     * Get the git data for the current repository.
     * <p>
     * "Git data" is referred to as the latest commit
     * hash, as well as the commit depth, or in other
     * words, the amount of commits since the last tag.
     * <p>
     * If successful, a map entry is returned containing
     * the commit hash as the key, and the commit depth
     * as the value.
     * <p>
     * If there is no local git repository, or
     * an error has occurred, then null is returned.
     * </p>
     *
     * @return the map entry containing the git data
     * @see Map.Entry for map entry
     */
    public static Map.Entry<String, Integer> getGitData() {
        File gitDir = new File(".git");
        if (!gitDir.exists()) { // Not a git repository, return null
            return null;
        }
        try (
            FileRepository repository = new FileRepository(gitDir); // Create a new file repository
            Git git = new Git(repository); // Create a new git instance
            RevWalk walk = new RevWalk(repository); // Create a new rev walk of the repository
        ) {
            RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next(); // Get the latest commit
            
            // Find the commits since the last tag
            int commitsSinceLastTag = 0;
            List<Ref> tags = git.tagList().call();
            RevCommit commit = walk.parseCommit(repository.resolve("HEAD"));
            while (true) {
                for (Ref tag : tags) {
                    // If the commit is not the same as the tag, continue
                    if (!walk.parseCommit(tag.getObjectId()).equals(commit)) {
                        continue;
                    }
                    walk.dispose(); // Dispose of the rev walk
                    
                    // Return the git data
                    return new AbstractMap.SimpleEntry<>(latestCommit.getName(), commitsSinceLastTag);
                }
                commitsSinceLastTag++; // Increment commits since last tag
                commit = walk.parseCommit(commit.getParent(0)); // Get the parent commit
            }
        } catch (IOException | GitAPIException ex) {
            System.err.println("Failed fetching git data:");
            ex.printStackTrace();
        }
        return null;
    }
}