package com.kirkwoodwest.utils.led;

import com.bitwig.extension.controller.api.MidiOut;

import java.util.ArrayList;
import java.util.function.Supplier;

public class LedHandler {
  ArrayList<Led> led_list;
  enum LedType {
    CC,
    NOTE,
    NOTE_MULTI_STATE,
    CC_RANGE,
  }
  public LedHandler(){
    led_list = new ArrayList<>();
  }

  public void add(Led led){
    led_list.add(led);
  }

  public void addCCRanged(MidiOut midi_out, int status, int data1, Supplier<Integer> supplier) {
    Led led = new LedCCRanged(midi_out, status, data1, supplier);
    led_list.add(led);
  }

  /**
   * Updates all Leds
   */
  public void updateLeds(boolean force_update){
    for(Led led : led_list){
      led.update(force_update);
    }
  }

}
