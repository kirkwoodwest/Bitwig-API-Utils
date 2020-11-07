package com.kirkwoodwest.utils.cvosc;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.channelfinder.ChannelFinder;

import java.io.IOException;

public class CVToOsc {
  private final OscConnection connection;
  CursorTrack cursor_track;
  CursorDevice cursor_device;
  private final ControllerHost             host;

  public CVToOsc(ControllerHost host, ChannelFinder channel_finder, OscConnection connection, int parameter_count) {
    this.host = host;
    this.connection = connection;
    CursorDeviceFollowMode follow_mode = CursorDeviceFollowMode.FIRST_DEVICE;

    cursor_track = host.createCursorTrack("CV To Osc Cursor Track", "CV To Osc Cursor Track", 0,0, false);
    cursor_device = cursor_track.createCursorDevice("CV To Osc Cursor Device", "CV To Osc Cursor Device",  0, follow_mode);
    channel_finder.add(cursor_track, "CVOSC");

    //Get parameters and store them in convenient lists.

    CursorRemoteControlsPage remote_control_page = cursor_device.createCursorRemoteControlsPage("CV To Osc Remote Page", parameter_count,"");
    Parameter[] parameters = new Parameter[parameter_count];
    for(int p=0;p<parameter_count;p++) {
      final int parameter_index = p;
      parameters[p] = remote_control_page.getParameter(p);
      parameters[p].value().addValueObserver(value -> this.parameterChanged(value, parameter_index));
      parameters[p].modulatedValue().addValueObserver(value -> this.modulatedValueChanged(value, parameter_index));
    }
  }

  private void parameterChanged(double value, int index) {
    //host.println("Value " + value +  "| index:" + index);
    //Only update with the modulated value change...
  }
  private void modulatedValueChanged(double value, int index) {
   // host.println("Modulated Value " + value +  "| index:" + index);
    String target = "Bitwig/CVToOsc/" + index;
    try {
      float v = (float) value;
      connection.sendMessage(target, v);
    } catch (IOException e) {
      host.println("modulatedValueChanged IO Exception:" + e);
    }

  }
}
