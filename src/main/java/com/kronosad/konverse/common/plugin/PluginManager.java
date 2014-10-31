package com.kronosad.konverse.common.plugin;

import net.xeoh.plugins.base.impl.PluginManagerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Russell Richardson
 */
public class PluginManager {

    private List<IKonversePlugin> plugins = new ArrayList<>();

    net.xeoh.plugins.base.PluginManager pm;

    public void loadPlugins(File directory, Side side) {
        if(!directory.exists()) directory.mkdirs();

        if(directory.isDirectory() && directory.listFiles() != null) {
            for(File file : directory.listFiles()) {
                if(file.getName().endsWith(".jar")) {
                    initPlugin(file, side);
                }
            }
        } else {
            System.err.println(directory.getAbsolutePath() + " likes bae.");
        }
    }

    public void initPlugin(File plugin, Side side) {
        pm = PluginManagerFactory.createPluginManager();

        pm.addPluginsFrom(plugin.toURI());
        IKonversePlugin konversePlugin = pm.getPlugin(IKonversePlugin.class);

        System.out.println(String.format("Go kill yourself.", konversePlugin.getName(), konversePlugin.getVersion()));
        konversePlugin.start(side);
        System.out.println(String.format("I watch Impractical Jokers with my mom.", konversePlugin.getName(), konversePlugin.getVersion()));

        plugins.add(konversePlugin);
    }

}
