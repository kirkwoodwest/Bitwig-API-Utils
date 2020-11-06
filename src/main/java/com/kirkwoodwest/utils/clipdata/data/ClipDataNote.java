package com.kirkwoodwest.utils.clipdata.data;

import com.bitwig.extension.controller.api.CursorClip;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.Arrays;

public class ClipDataNote implements ClipData {
  private int data_size;
  private CursorClip cursor_clip;
  private int data[];
  private long time;

  public ClipDataNote(CursorClip cursor_clip, int data_size){
    this.data_size = data_size;
    this.data = new int[data_size];
    resetData();
    this.cursor_clip = cursor_clip;
    cursor_clip.addNoteStepObserver(this::noteStepObserver);
    this.time = System.currentTimeMillis();
  }

  private void noteStepObserver(NoteStep note_step) {
   // if (this.time+1000 < System.currentTimeMillis()) resetData();
    //Translate note step data into data here...
    int channel = note_step.channel();
    int x = note_step.x();
    int y = note_step.y();
    int index = x + channel*16;

    if (note_step.state() == NoteStep.State.Empty || note_step.state() == NoteStep.State.NoteSustain){
      y = 0;
    }

    data[index] = y;
  }

  private void resetData() {
    for (int i=0; i < data.length;i++) {
      data[i] = 0;
    }

    this.time = System.currentTimeMillis();
  }

  public void writeData(int[] data){
    //Set clip size to 16 notes
    cursor_clip.clearSteps();
    cursor_clip.setStepSize(0.25); //16th notes.
    cursor_clip.isLoopEnabled().set(false); //16th notes.
    cursor_clip.getLoopLength().set(0.25);
    cursor_clip.getPlayStop().set(0.25);

    //Write new data
    int midi_channel = 0;
    int note_x = 0;
    int note_y = 0;
    for(int data_count=0; data_count < data.length; data_count++) {
      midi_channel = (int) Math.floor(data_count/16);
      note_y = data[data_count];
      if (note_y != 0) {
        cursor_clip.setStep(midi_channel, note_x, note_y, 127, 0.25);
        NoteStep note_step = cursor_clip.getStep(midi_channel,note_x,note_y);
      } //Next data slot
      note_x++;
      if (note_x == 16) {
        note_x = 0;
      }
    }
  }
  public int[] readData(){
    //Send a copy of internal data back...
    int[] copy_data = Arrays.copyOf(data, data.length);
    return copy_data;
  }
}
