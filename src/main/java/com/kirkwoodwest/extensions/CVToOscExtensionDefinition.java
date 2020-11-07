package com.kirkwoodwest.extensions;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;

public class CVToOscExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("e8061fc9-e473-4670-8975-0b9be81a0312");
   
   public CVToOscExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "CV To Osc";
   }
   
   @Override
   public String getAuthor()
   {
      return "kirkwoodwest";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "Kirkwood West";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "Kirkwoods Utils";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 12;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 0;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 0;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
   }

   @Override
   public CVToOscExtension createInstance(final ControllerHost host)
   {
      return new CVToOscExtension(this, host);
   }
}
