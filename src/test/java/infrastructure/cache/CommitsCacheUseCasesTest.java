package infrastructure.cache;

import infrastructure.entities.FileSystemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommitsCacheUseCases.
 * Fully tests commit-chain operations: add, remove, retrieve subtree, last commit, etc.
 */
public class CommitsCacheUseCasesTest {

    private HashMap<String,String> commits;
    private FileSystemGateway fs; // Not used in logic, but class requires it
    private CommitsCacheUseCases cache;

    @BeforeEach
    void setup() {
        commits = new HashMap<>();
        fs = mock(FileSystemGateway.class);
        cache = new CommitsCacheUseCases(commits, fs);
    }

    // -----------------------------------------------------------------------------------------
    // addCommitToTree
    // -----------------------------------------------------------------------------------------

    @Test
    void addCommitToTree_firstCommit_createsRootAndLeaf() {
        Path repo = Path.of("repo");
        cache.addCommitToTree(repo, "c1");

        assertEquals("c1", commits.get("repo"));
        assertEquals("", commits.get("c1"));
    }

    @Test
    void addCommitToTree_appendsToExistingChain() {
        Path repo = Path.of("repo");
        // Prepare chain: repo -> c1 -> c2 -> ""
        commits.put("repo", "c1");
        commits.put("c1", "c2");
        commits.put("c2", "");

        cache.addCommitToTree(repo, "c3");

        assertEquals("c3", commits.get("c2")); // old tail now points to new leaf
        assertEquals("", commits.get("c3"));
    }

    // -----------------------------------------------------------------------------------------
    // isCommitExists
    // -----------------------------------------------------------------------------------------

    @Test
    void isCommitExists_returnsTrueWhenExists() {
        commits.put("repo", "c1");
        assertTrue(cache.isCommitExists(Path.of("repo")));
    }

    @Test
    void isCommitExists_returnsFalseWhenMissing() {
        assertFalse(cache.isCommitExists(Path.of("missing")));
    }

    // -----------------------------------------------------------------------------------------
    // getLastCommitForParent
    // -----------------------------------------------------------------------------------------

    @Test
    void getLastCommitForParent_returnsLastInChain() {
        commits.put("repo", "c1");
        commits.put("c1", "c2");
        commits.put("c2", "");

        assertEquals("c2", cache.getLastCommitForParent(Path.of("repo")));
    }

    @Test
    void getLastCommitForParent_singleCommit_returnsRoot() {
        commits.put("repo", "");
        assertEquals("repo", cache.getLastCommitForParent(Path.of("repo")));
    }

    // -----------------------------------------------------------------------------------------
    // retrieveSubtree
    // -----------------------------------------------------------------------------------------

    @Test
    void retrieveSubtree_returnsMapForChain() {
        commits.put("repo", "c1");
        commits.put("c1", "c2");
        commits.put("c2", "");

        Map<String,String> map = cache.retrieveSubtree("repo");

        assertEquals(3, map.size());
        assertEquals("c1", map.get("repo"));
        assertEquals("c2", map.get("c1"));
        assertTrue(map.containsKey("c2")); // end-of-chain is stored as "", not mapped forward
    }

    @Test
    void retrieveSubtree_returnsEmptyMapWhenMissing() {
        assertTrue(cache.retrieveSubtree("nothing").isEmpty());
    }

    @Test
    void retrieveSubtree_singleCommit_returnsTerminatedMap() {
        commits.put("repo", "");

        Map<String,String> map = cache.retrieveSubtree("repo");
        assertEquals(1, map.size());
        assertEquals("", map.get("repo"));
    }

    // -----------------------------------------------------------------------------------------
    // removeCommitsTree
    // -----------------------------------------------------------------------------------------

    @Test
    void removeCommitsTree_removesEntireChain() {
        commits.put("repo", "c1");
        commits.put("c1", "c2");
        commits.put("c2", "");

        cache.removeCommitsTree(Path.of("repo"));

        assertTrue(commits.isEmpty());
    }

    @Test
    void removeCommitsTree_missingRoot_doesNothing() {
        commits.put("a", "b");
        cache.removeCommitsTree(Path.of("repo"));

        assertEquals(1, commits.size());
    }

    // -----------------------------------------------------------------------------------------
    // removeCommitsSubTree
    // -----------------------------------------------------------------------------------------

    @Test
    void removeCommitsSubTree_removesChildrenButKeepsRoot() throws IOException {
        commits.put("root", "c1");
        commits.put("c1", "c2");
        commits.put("c2", "");

        cache.removeCommitsSubTree("root", Path.of("tree"), "meta");

        // root remains but points to ""
        assertEquals("", commits.get("root"));

        // children removed
        assertFalse(commits.containsKey("c1"));
        assertFalse(commits.containsKey("c2"));
    }

    @Test
    void removeCommitsSubTree_missingRoot_doesNothing() throws IOException {
        commits.put("x", "y");
        cache.removeCommitsSubTree("root", Path.of("tree"), "meta");

        assertEquals(1, commits.size());
    }
}
