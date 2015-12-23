package com.faforever.client.relay;

import com.faforever.client.connectivity.ConnectivityState;
import com.faforever.client.legacy.domain.MessageTarget;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class ConnectivityStateMessage extends GpgServerMessage {

  private static final int STATE_INDEX = 0;
  private static final int ADDRESS_INDEX = 1;

  public ConnectivityStateMessage(ConnectivityState state, InetSocketAddress socketAddress) {
    super(GpgServerMessageType.CONNECTIVITY_STATE, Arrays.asList(
        state, new Object[]{socketAddress.getHostName(), socketAddress.getPort()}
    ));
    setTarget(MessageTarget.CONNECTIVITY);
  }

  public ConnectivityState getState() {
    return (ConnectivityState) getArgs().get(STATE_INDEX);
  }

  public InetSocketAddress getSocketAddress() {
    @SuppressWarnings("unchecked")
    Object[] hostPort = (Object[]) getArgs().get(ADDRESS_INDEX);
    return new InetSocketAddress((String) hostPort[0], (int) hostPort[1]);
  }
}
