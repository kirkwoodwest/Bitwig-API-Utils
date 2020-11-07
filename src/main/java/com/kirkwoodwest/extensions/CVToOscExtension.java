package com.kirkwoodwest.extensions;

import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.kirkwoodwest.utils.channelfinder.ChannelFinder;
import com.kirkwoodwest.utils.cvosc.CVToOsc;

public class CVToOscExtension extends ControllerExtension
{
   private ChannelFinder channel_finder;
   private CVToOsc cv_to_osc;
   private OscModule osc_module;

   private int receivePort = 8000;
   private String sendHost = "127.0.0.1"; //"192.168.86.23";
   private int sendPort    = 9000;
   private OscAddressSpace addressSpace;
   private OscConnection connection;

   protected CVToOscExtension(final CVToOscExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();

      osc_module = host.getOscModule();
      addressSpace = osc_module.createAddressSpace();
      connection = osc_module.connectToUdpServer(sendHost, sendPort, addressSpace);

      //Create channel finder,
      channel_finder = new ChannelFinder(host);

      //CV To Osc Module
      cv_to_osc = new CVToOsc(host, channel_finder, connection, 3);

      //Second Init Channel Finder
      channel_finder.init();

      // TODO: Perform your driver initialization here.
      // For now just show a popup notification for verification that it is running.
      host.showPopupNotification("CV To OSC Initialized");
   }

   @Override
   public void exit()
   {
      // TODO: Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer running.
      getHost().showPopupNotification("Kirkwoods Utils Exited");
   }

   @Override
   public void flush()
   {
      // TODO Send any updates you need here.
   }


}
