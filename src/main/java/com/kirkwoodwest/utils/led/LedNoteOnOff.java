package com.kirkwoodwest.utils.led;

import com.bitwig.extension.controller.api.MidiOut;

import java.util.function.Supplier;

public class LedNoteOnOff implements Led {
  private final Supplier<Boolean> results_supplier;
  MidiOut midi_out;
  int     status;
  int data1;
  boolean internal_value;

  public LedNoteOnOff(MidiOut midi_out, int status, int data1, Supplier<Boolean> results_func){
    this.midi_out = midi_out;
    this.status = status;
    this.data1 = data1;
    this.results_supplier = results_func;
  }

  @Override
  public void update(boolean force_update) {
    boolean value = results_supplier.get();
    if (value != internal_value || force_update == true) {
      int data2 = 0;
      if (value == true)  data2 = 127;
      midi_out.sendMidi(status, data1, data2);
      internal_value = value;
    }
  }
}
