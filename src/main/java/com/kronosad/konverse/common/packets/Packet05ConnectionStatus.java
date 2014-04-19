package com.kronosad.konverse.common.packets;

/**
 * Author russjr08
 * Created at 4/19/14
 */
public class Packet05ConnectionStatus extends Packet {

    public final static int CONNECTION_SUCCESSFUL = 0;
    public final static int CONNECTION_DISCONNECT = 1;

    public final static int NICK_IN_USE = 2;
    public final static int VERSION_MISMATCH = 3;
    public final static int BANNED = 4;


    private int status;

    private Packet04Disconnect disconnectPacket;

    /**
     * All Packets should be constructed with an {@link com.kronosad.konverse.common.packets.Packet.Initiator} as the parameter.
     *
     * @param initiator Initiator of said Packet.
     * @param id
     */
    public Packet05ConnectionStatus(Initiator initiator, int id, int status) {
        super(initiator, id);
        this.status = status;

        if (!assertInitiator(initiator, Initiator.SERVER)) {
            throw new IllegalArgumentException("The initiator of this packet MUST be the Server!");
        }


    }

    public void setDisconnectPacket(Packet04Disconnect disconnect) {
        this.disconnectPacket = disconnect;
    }

    public int getStatus() {
        return status;
    }
}
