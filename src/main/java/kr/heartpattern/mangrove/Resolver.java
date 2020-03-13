package kr.heartpattern.mangrove;

import kr.heartpattern.mangrove.logger.JULLoggerFactory;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedTransferQueue;

public class Resolver {
    RepositorySystem system;
    DefaultRepositorySystemSession session;
    RemoteRepository defaultRepository;

    public Resolver(File local) {
        system = Resolver.newRepositorySystem();
        session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepository = new LocalRepository(local);
        session.setLocalRepositoryManager(
                system.newLocalRepositoryManager(session, localRepository)
        );

        defaultRepository = new RemoteRepository.Builder(
                "HeartPattern-Public",
                "default",
                "https://maven.heartpattern.kr/repository/maven-public/"
        ).build();
    }

    public Set<File> resolveDependencies(List<PluginDependency> dependencies) throws DependencyResolutionException {
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.addRepository(defaultRepository);
        collectRequest.setRoot(null);
        dependencies
                .stream()
                .flatMap(plugin -> plugin.getRepositories().stream())
                .forEach(collectRequest::addRepository);
        dependencies
                .stream()
                .flatMap(plugin -> plugin.getDependencies().stream())
                .forEach(collectRequest::addDependency);

        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setCollectRequest(collectRequest);
        dependencyRequest.setFilter(new BukkitFilter());

        DependencyResult dependencyResult = system.resolveDependencies(session, dependencyRequest);

        Queue<DependencyNode> queue = new LinkedTransferQueue<>();
        queue.add(dependencyResult.getRoot());

        Set<File> result = new HashSet<>();
        while (!queue.isEmpty()) {
            DependencyNode current = queue.poll();
            if (current.getArtifact() != null && current.getArtifact().getFile() != null) {
                result.add(current.getArtifact().getFile());
            }

            queue.addAll(current.getChildren());
        }

        return result;
    }

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.addService(LoggerFactory.class, JULLoggerFactory.class);

        return locator.getService(RepositorySystem.class);
    }
}
