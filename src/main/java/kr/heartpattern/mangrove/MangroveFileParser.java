package kr.heartpattern.mangrove;

import com.google.gson.*;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MangroveFileParser {
    private MangroveFileParser() {
    }

    private static Gson gson = new GsonBuilder().create();

    public static List<Dependency> parseDependencies(InputStream inputStream) {
        LinkedList<Dependency> dependencies = new LinkedList<>();

        JsonArray root = gson.fromJson(new InputStreamReader(inputStream), JsonArray.class);

        for (int i = 0; i < root.size(); i++) {
            JsonObject obj = root.get(i).getAsJsonObject();

            String group = obj.get("group").getAsString();
            String name = obj.get("name").getAsString();
            String version = obj.get("version").getAsString();

            List<Exclusion> exclusion = new LinkedList<>();

            if (obj.has("exclude")) {
                JsonArray excludes = obj.get("exclude").getAsJsonArray();

                for (int j = 0; j < excludes.size(); j++) {
                    JsonObject exclude = excludes.get(j).getAsJsonObject();
                    exclusion.add(
                            new Exclusion(
                                    exclude.get("group").getAsString(),
                                    exclude.get("name").getAsString(),
                                    null,
                                    "jar"
                            )
                    );
                }
            }

            Dependency dependency = new Dependency(
                    new DefaultArtifact(
                            group,
                            name,
                            "jar",
                            version
                    ),
                    "runtime",
                    false,
                    exclusion
            );

            dependencies.add(dependency);
        }

        return dependencies;
    }

    public static List<RemoteRepository> parseRepository(InputStream inputStream) {
        LinkedList<RemoteRepository> repositories = new LinkedList<>();

        JsonObject root = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            repositories.add(
                    new RemoteRepository.Builder(
                            entry.getKey(),
                            "default",
                            entry.getValue().getAsString()
                    ).build()
            );
        }

        return repositories;
    }
}