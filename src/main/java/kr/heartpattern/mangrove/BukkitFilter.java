package kr.heartpattern.mangrove;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

import java.util.ArrayList;
import java.util.List;

public class BukkitFilter implements DependencyFilter {
    private static final List<ExclusionInformation> exclusions = new ArrayList<>();

    public BukkitFilter() {
        exclusions.add(new ExclusionInformation("org.bukkit", "bukkit"));
        exclusions.add(new ExclusionInformation("org.spigotmc", "spigot-api"));
        exclusions.add(new ExclusionInformation("org.spigotmc", "spigot"));
        exclusions.add(new ExclusionInformation("com.destroystokyo.paper", "paper-api"));
        exclusions.add(new ExclusionInformation("com.destroystokyo.paper", "paper"));
    }

    @Override
    public boolean accept(DependencyNode node, List<DependencyNode> parents) {
        if (isBukkit(node)) return false;
        for (DependencyNode parent : parents) {
            if (isBukkit(parent)) return false;
        }

        return true;
    }

    private boolean isBukkit(DependencyNode node) {
        Artifact artifact = node.getArtifact();
        if (artifact == null) return true;

        for (ExclusionInformation exclusion : exclusions) {
            if (artifact.getGroupId().equals(exclusion.getGroup()) && artifact.getArtifactId().equals(exclusion.getName())) {
                return false;
            }
        }
        return true;
    }

    private static class ExclusionInformation {
        private final String group;
        private final String name;

        public ExclusionInformation(String group, String name) {
            this.group = group;
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public String getName() {
            return name;
        }
    }
}
