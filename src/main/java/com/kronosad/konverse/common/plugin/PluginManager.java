package com.kronosad.konverse.common.plugin;

import net.xeoh.plugins.base.impl.PluginManagerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Russell Richardson.
 */
public class PluginManager {

    private List<IKonversePlugin> plugins = new ArrayList<>();

    net.xeoh.plugins.base.PluginManager pm = PluginManagerFactory.createPluginManager();

    public void loadPlugins(File directory, Side side) {
        if(!directory.exists()) directory.mkdirs();

        if(directory.isDirectory() && directory.listFiles() != null) {
            for(File file : directory.listFiles()) {
                if(file.getName().endsWith(".jar")) {
                    initPlugin(file, side);
                }
            }
        } else {
            System.err.println(directory.getAbsolutePath() + " is not a directory!");
        }
    }

    public void initPlugin(File plugin, Side side) {
        pm.addPluginsFrom(plugin.toURI());
        IKonversePlugin konversePlugin = pm.getPlugin(IKonversePlugin.class);

        konversePlugin.start(side);
        plugins.add(konversePlugin);
    }

}
