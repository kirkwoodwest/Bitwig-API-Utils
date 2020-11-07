package com.kirkwoodwest.utils.cvosc;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.channelfinder.ChannelFinder;

import java.io.IOException;

public class CVToOsc {
  private final OscConnection connection;
  private       boolean       log_output;
  private       boolean       only_update_if_value_changed;
  private boolean update_during_flush;

  CursorTrack cursor_track;
  CursorDevice cursor_device;
  private final ControllerHost             host;
  Parameter[] parameters;
  double[] parameters_value;

  public CVToOsc(ControllerHost host, ChannelFinder channel_finder, OscConnection connection, int parameter_count, boolean update_during_flush, boolean only_update_if_value_changed, boolean log_output) {
    this.host = host;
    this.connection = connection;
    this.update_during_flush = update_during_flush;
    this.only_update_if_value_changed = only_update_if_value_changed;
    this.log_output = log_output;
    CursorDeviceFollowMode follow_mode = CursorDeviceFollowMode.FIRST_DEVICE;

    cursor_track = host.createCursorTrack("CV To Osc Cursor Track", "CV To Osc Cursor Track", 0,0, false);
    cursor_device = cursor_track.createCursorDevice("CV To Osc Cursor Device", "CV To Osc Cursor Device",  0, follow_mode);
    channel_finder.add(cursor_track, "CVOSC");

    //Get parameters and store them in convenient lists.
    CursorRemoteControlsPage remote_control_page = cursor_device.createCursorRemoteControlsPage("CV To Osc Remote Page", parameter_count,"");

    parameters = new Parameter[parameter_count];
    parameters_value = new double[parameter_count];
    for(int p=0;p<parameter_count;p++) {
      final int parameter_index = p;
      parameters[p] = remote_control_page.getParameter(p);
      if (update_during_flush) {
        parameters[p].value().markInterested();
        parameters[p].modulatedValue().markInterested();
        parameters[p].value().addValueObserver(value -> this.parameterChanged(value, parameter_index));
        parameters[p].modulatedValue().addValueObserver(value -> this.modulatedValueChanged(value, parameter_index));
        parameters_value[p] = -1;
      }
    }
  }

  public void flush(){
    if (this.update_during_flush) {
      for (int i = 0; i < parameters.length; i++) {
        double value = parameters[i].modulatedValue().get();
        sendModulatedValue(value, i);
      }
    }
  }

  private void parameterChanged(double value, int index) {
    //host.println("Value " + value +  "| index:" + index);
    //Only update with the modulated value change...
  }
  private void modulatedValueChanged(double value, int index) {
    if (!this.update_during_flush) {
      sendModulatedValue(value, index);
    }
  }

  private void sendModulatedValue(double value, int index) {
    //
    String target = "Bitwig/CVToOsc/" + index;
    if (only_update_if_value_changed == true && parameters_value[index] == value) {
      return;
    }
    try {
      float v = (float) value;
      connection.sendMessage(target, v);
      parameters_value[index] = value;

      if(log_output) host.println("Send OSC[ Target: Bitwig/CVToOsc/" + index + " | Value: " + value);
    } catch (IOException e) {
      host.println("modulatedValueChanged IO Exception:" + e);
    }
  }

  public void setUpdateDuringFlush(boolean update_during_flush) {
    this.update_during_flush = update_during_flush;
  }

  public void setUpdateOnlyChanged(boolean update) {
    only_update_if_value_changed = update;
  }

  public void setLogOutput(boolean log_output) {
    this.log_output = log_output;
  }
}
