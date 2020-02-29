package kr.heartpattern.mangrove;

import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginScanner {
    private PluginScanner() {
    }

    public static List<PluginDependency> scanFolder(File folder) throws IOException {
        if (!folder.isDirectory())
            throw new IllegalArgumentException(folder.getName() + "is not directory");

        List<PluginDependency> dependencies = new LinkedList<>();

        //noinspection ConstantConditions
        for (File plugin : folder.listFiles()) {
            if (plugin.isDirectory() || !plugin.getName().endsWith(".jar"))
                continue;

            dependencies.add(scanFile(plugin));
        }

        return dependencies;
    }

    public static PluginDependency scanFile(File plugin) throws IOException {
        JarFile jar = new JarFile(plugin);

        List<Dependency> dependencies;
        List<RemoteRepository> repositories;

        JarEntry dependencyEntry = jar.getJarEntry("dependency.mangrove.json");
        if (dependencyEntry != null) {
            dependencies = MangroveFileParser.parseDependencies(jar.getInputStream(dependencyEntry));
        } else {
            dependencies = new LinkedList<>();
        }

        JarEntry repositoryEntry = jar.getJarEntry("repository.mangrove.json");
        if (repositoryEntry != null) {
            repositories = MangroveFileParser.parseRepository(jar.getInputStream(repositoryEntry));
        } else {
            repositories = new LinkedList<>();
        }

        return new PluginDependency(dependencies, repositories);
    }
}
