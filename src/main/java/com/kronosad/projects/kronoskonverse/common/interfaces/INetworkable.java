package com.kronosad.projects.kronoskonverse.common.interfaces;

/**
 * User: russjr08
 * Date: 1/17/14
 * Time: 4:12 PM
 */

import java.io.Serializable;

/**
 * An interface for transmitting data across the network. This data should be serializable in JSON form.
 */
public interface INetworkable extends Serializable{

    /**
     * This method should deserialize any objects sent over the network.
     * @param json The JSON-serialized version of this object.
     * @return An instance of {@link com.kronosad.projects.kronoskonverse.common.interfaces.INetworkable}
     */
    public INetworkable fromJSON(String json);
    public String toJSON();

}
