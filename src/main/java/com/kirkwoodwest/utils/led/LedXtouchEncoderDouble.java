package com.kirkwoodwest.utils.led;

import com.bitwig.extension.controller.api.MidiOut;
import com.kirkwoodwest.config.CentrifugeConfig;
import com.kirkwoodwest.utils.Math;

import java.util.function.Supplier;

public class LedXtouchEncoderDouble implements Led {
  private final Supplier<Double> results_supplier;
  MidiOut midi_out;
  int xtouch_led_min = CentrifugeConfig.XTOUCH_KNOB_LED_RANGE[0];
  int xtouch_led_max = CentrifugeConfig.XTOUCH_KNOB_LED_RANGE[1];

  int     status;
  int     data1;
  double  internal_value;

  public LedXtouchEncoderDouble(MidiOut midi_out, int status, int data1, Supplier<Double> results_func){
    this.midi_out = midi_out;
    this.status = status;
    this.data1 = data1;
    this.results_supplier = results_func;
  }

  @Override
  public void update(boolean force_update) {
    double value = results_supplier.get();
    if (value != internal_value || force_update == true) {
      int data2 = (int) java.lang.Math.round(Math.map(value, 0, 1, xtouch_led_min, xtouch_led_max));
      midi_out.sendMidi(status, data1, data2);
      internal_value = value;
    }
  }
}
