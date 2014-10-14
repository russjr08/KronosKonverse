package com.kronosad.konverse.common.plugin;

import net.xeoh.plugins.base.Plugin;

/**
 * Created by Russell Richardson.
 */
public interface IKonversePlugin extends Plugin {

    public void start();
    public void stop();

    public String getName();

    public Double getVersion();


}
