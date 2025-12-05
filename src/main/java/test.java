import app.state.StateManager;
import infrastructure.cache.CachedRepositories;
import infrastructure.cache.CommitsCache;
import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.encryption.PathCipher;
import infrastructure.encryption.PathsEncryption;
import infrastructure.entities.*;
import infrastructure.filescomparison.ByteToByteComparator;
import infrastructure.filescomparison.FilesComparator;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;
import infrastructure.filesystem.Viewer;
import infrastructure.storage.JsonEntity;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class test {
    public static void main(String[] args) throws IOException {
        FileSystemGateway fsGate = new LocalFsTasksExecutor();
        CommitsCacheLoader commitsCache = new CommitsCache(fsGate);
        RepositoriesGateway repoGate = new CachedRepositories(fsGate);
        CommitsCacheGateway commitsGW = new CommitsCacheUseCases(
                commitsCache.returnCurrentState(), fsGate);
        Eraser eras = new Eraser();
        Copier cp = new Copier();
        Cleaner cl = new Cleaner();

        commitsCache.loadInMemory();
        repoGate.loadCachedDirs();
        StateManager state = new StateManager(commitsGW, repoGate, fsGate, eras, cp, cl);

        System.out.println("State Before : ");
        List<Path> current = repoGate.returnCachedDirectories();
        for (var x : current) {
            System.out.println("Directory : " +  x);
            HashMap<String, String> subTree
                    = commitsGW.retrieveSubtree(x.toString());

            String par = x.toString();
            Queue<String> queue = new LinkedList<>();
            queue.add(par);
            while (!queue.isEmpty()) {
                String curr = queue.poll();
                if (curr.isEmpty()) {
                    continue;
                }
                System.out.println("Commit : " + curr + " ----> ");
                String next = subTree.get(curr);
                queue.add(next);
            }
        }

        state.saveCurrentState();
        state.recoverPreviousState();

        System.out.println("State After : ");
        List<Path> current1 = repoGate.returnCachedDirectories();
        for (var x : current1) {
            System.out.println("Directory : " +  x);
            HashMap<String, String> subTree
                    = commitsGW.retrieveSubtree(x.toString());

            String par = x.toString();
            Queue<String> queue = new LinkedList<>();
            queue.add(par);
            while (!queue.isEmpty()) {
                String curr = queue.poll();
                if (curr.isEmpty()) {
                    continue;
                }
                System.out.println("Commit : " + curr + " ----> ");
                String next = subTree.get(curr);
                queue.add(next);
            }
        }
    }
}
