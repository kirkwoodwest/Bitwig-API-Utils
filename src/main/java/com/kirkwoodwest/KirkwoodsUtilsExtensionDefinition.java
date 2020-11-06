package com.kirkwoodwest;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class KirkwoodsUtilsExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("638ac240-c0cc-4961-a405-460620706826");
   
   public KirkwoodsUtilsExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "Kirkwoods Utils";
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
   public KirkwoodsUtilsExtension createInstance(final ControllerHost host)
   {
      return new KirkwoodsUtilsExtension(this, host);
   }
}
