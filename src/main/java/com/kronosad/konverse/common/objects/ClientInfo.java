package com.kronosad.konverse.common.objects;

/**
 * Allows other Users to see what type of Client you're using, and it's {@link com.kronosad.konverse.common.objects.Version}.
 * @author Russell Richardson
 */
public class ClientInfo {

    private String cilentName;
    private Version clientVersion;

    /**
     * Instantiates an instance of {@link com.kronosad.konverse.common.objects.ClientInfo} with the following information:
     * @param name The name of the Client.
     * @param version The {@link com.kronosad.konverse.common.objects.Version} of the Client.
     */
    public ClientInfo(String name, Version version) {
        this.cilentName = name;
        this.clientVersion = version;
    }

    /**
     * @return The name of the Client.
     */
    public String getCilentName() {
        return cilentName;
    }

    /**
     * @return The {@link com.kronosad.konverse.common.objects.Version} of the Client.
     * @see com.kronosad.konverse.common.objects.Version
     */
    public Version getClientVersion() {
        return clientVersion;
    }
}
