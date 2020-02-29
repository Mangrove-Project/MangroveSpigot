package kr.heartpattern.mangrove;

import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.eclipse.aether.resolution.DependencyResolutionException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

@Plugin(name = "Mangrove", version = "1.0.0")
@Description("Mangrove is runtime dependency resolver.")
@LoadOrder(PluginLoadOrder.STARTUP)
@Author("HeartPattern")
@Website("https://github.com/HeartPattern/")
public class MangrovePlugin extends JavaPlugin {
    // Execute on constructor to load before other plugin loaded.
    public MangrovePlugin() throws NoSuchFieldException, IllegalAccessException, IOException, DependencyResolutionException, NoSuchMethodException, InvocationTargetException {
        getLogger().fine("Start resolving dependencies");
        Field fileField = JavaPlugin.class.getDeclaredField("file");
        fileField.setAccessible(true);
        File pluginFile = (File) fileField.get(this);

        File pluginFolder = pluginFile.getParentFile();

        List<PluginDependency> dependencies = PluginScanner.scanFolder(pluginFolder);

        Set<File> artifacts = new Resolver(getDataFolder()).resolveDependencies(dependencies);

        Field classLoaderField = JavaPlugin.class.getDeclaredField("classLoader");
        classLoaderField.setAccessible(true);
        ClassLoader pluginClassLoader = (ClassLoader) classLoaderField.get(this);
        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addUrlMethod.setAccessible(true);

        for (File artifact : artifacts) {
            addUrlMethod.invoke(pluginClassLoader, artifact.toURI().toURL());
        }
        getLogger().fine("End resolving dependencies");
    }
}
