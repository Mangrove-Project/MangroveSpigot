package kr.heartpattern.mangrove;

import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.net.URL;
import java.util.List;

public class PluginDependency {
    private final List<Dependency> dependencies;
    private final List<RemoteRepository> repositories;

    public PluginDependency(List<Dependency> dependencies, List<RemoteRepository> repositories) {
        this.dependencies = dependencies;
        this.repositories = repositories;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<RemoteRepository> getRepositories() {
        return repositories;
    }
}
