package com.kirkwoodwest.utils.led;

import com.bitwig.extension.controller.api.MidiOut;

import java.util.function.Supplier;

public class LedCCRanged implements Led {
  private final Supplier<Integer> results_supplier;
  MidiOut midi_out;
  int     status;
  int     data1;
  int internal_value;

  public LedCCRanged(MidiOut midi_out, int status, int data1, Supplier<Integer> results_func){
    this.midi_out = midi_out;
    this.status = status;
    this.data1 = data1;
    this.results_supplier = results_func;
  }

  @Override
  public void update(boolean force_update) {
    int value = results_supplier.get();
    if (value != internal_value || force_update == true) {
      midi_out.sendMidi(status, data1, value);
      internal_value = value;
    }
  }
}
