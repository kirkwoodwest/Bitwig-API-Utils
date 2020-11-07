package com.kirkwoodwest.extensions;

import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.SettableEnumValue;
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
   private SettableEnumValue settings_update_only_flush;
   private SettableEnumValue settings_update_on_flush;
   private SettableEnumValue settings_log_output;

   boolean update_during_flush = true;
   boolean only_update_if_value_changed = true;
   boolean log_output = false;

   static final String [] ON_OFF_OPTIONS = {"Off", "On" };

   protected CVToOscExtension(final CVToOscExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();

      //Create channel finder,
      channel_finder = new ChannelFinder(host);

      //OSC Module
      osc_module = host.getOscModule();
      addressSpace = osc_module.createAddressSpace();
      connection = osc_module.connectToUdpServer(sendHost, sendPort, addressSpace);

      //Second Init Channel Finder
      channel_finder.init();

      //CV To Osc Module
      cv_to_osc = new CVToOsc(host, channel_finder, connection, 3, update_during_flush, only_update_if_value_changed, log_output);

      //Document Settings
      DocumentState        document_settings = host.getDocumentState();
      settings_update_only_flush = document_settings.getEnumSetting("Flush Update ", "Update", ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
      settings_update_only_flush.markInterested();
      settings_update_only_flush.addValueObserver(this::settingsUpdateOnlyFlush);

      settings_update_on_flush = document_settings.getEnumSetting("Changes Only", "Update", ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
      settings_update_on_flush.markInterested();
      settings_update_on_flush.addValueObserver(this::settingsUpdateOnlyChanged);

      settings_log_output = document_settings.getEnumSetting("Log Output ", "Update", ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
      settings_log_output.markInterested();
      settings_log_output.addValueObserver(this::settingsLogOutputChanged);

      // TODO: Perform your driver initialization here.
      // For now just show a popup notification for verification that it is running.
      host.showPopupNotification("CV To OSC Initialized");
   }

   private void settingsLogOutputChanged(String value) {
      boolean log_update = false;
      if (value.equals(ON_OFF_OPTIONS[1])) log_update = true;
      cv_to_osc.setLogOutput(log_update);
   }

   private void settingsUpdateOnlyFlush(String value) {
      boolean update_during_flush = false;
      if (value.equals(ON_OFF_OPTIONS[1])) update_during_flush = true;
      cv_to_osc.setUpdateDuringFlush(update_during_flush);
   }

   private void settingsUpdateOnlyChanged(String value) {
      boolean update = false;
      if (value.equals(ON_OFF_OPTIONS[1])) update = true;
      cv_to_osc.setUpdateOnlyChanged(update);
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
      cv_to_osc.flush();
      // TODO Send any updates you need here.
   }


}
