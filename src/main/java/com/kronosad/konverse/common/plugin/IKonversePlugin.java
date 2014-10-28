package com.kronosad.konverse.common.plugin;

import net.xeoh.plugins.base.Plugin;

/**
 * @author Russell Richardson
 */
public interface IKonversePlugin extends Plugin {

    public void start(Side side);
    public void stop();

    public String getName();

    public Double getVersion();


}
