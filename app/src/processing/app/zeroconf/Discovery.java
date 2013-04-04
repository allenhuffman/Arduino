package processing.app.zeroconf;

import processing.app.zeroconf.jmdns.ArduinoDNSTaskStarter;

import javax.jmdns.JmDNS;
import javax.jmdns.NetworkTopologyDiscovery;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.impl.DNSTaskStarter;
import java.io.IOException;
import java.net.InetAddress;

public class Discovery implements ServiceListener {

  private final BoardListener listener;

  static {
    DNSTaskStarter.Factory.setClassDelegate(new ArduinoDNSTaskStarter());
  }

  public Discovery(BoardListener listener) throws IOException {
    this.listener = listener;
    for (InetAddress addr : NetworkTopologyDiscovery.Factory.getInstance().getInetAddresses()) {
      JmDNS jmDNS = JmDNS.create(addr);
      jmDNS.addServiceListener("_arduino._tcp.local.", this);
    }
  }

  @Override
  public void serviceAdded(ServiceEvent serviceEvent) {
    serviceEvent.getDNS().requestServiceInfo(serviceEvent.getInfo().getServer(), serviceEvent.getName());
  }

  @Override
  public void serviceRemoved(ServiceEvent serviceEvent) {
    listener.boardOffline(serviceEvent);
  }

  @Override
  public void serviceResolved(ServiceEvent serviceEvent) {
    listener.boardOnline(serviceEvent);
  }

}