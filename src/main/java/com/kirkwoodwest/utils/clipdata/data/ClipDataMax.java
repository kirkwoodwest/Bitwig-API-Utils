package com.kirkwoodwest.utils.clipdata.data;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorClip;
import com.bitwig.extension.controller.api.NoteStep;

import java.util.Arrays;

//Just an alternate class to try to pack in as much data into little amount of notes.
//This is really difficult and this class doesn't function properly.
public class ClipDataMax implements ClipData {
  private int data_size;
  private CursorClip cursor_clip;
  private int data[];
  private long time;
  private ControllerHost host;
  //TODO: Organize field vars.

  public ClipDataMax(ControllerHost host, CursorClip cursor_clip, int data_size){
    this.host = host;
    data_size = 1024;
    this.data_size = data_size;
    this.data = new int[data_size];
    resetData();
    this.cursor_clip = cursor_clip;
    cursor_clip.addNoteStepObserver(this::noteStepObserver);
    this.time = System.currentTimeMillis();
  }

  private void noteStepObserver(NoteStep note_step) {
    //Translate note step data into data here...
    int[][] data_pack = unpackDataFromNote(note_step);

    if (note_step.state() == NoteStep.State.Empty || note_step.state() == NoteStep.State.NoteSustain){
      //Data should be empty now so we empty it...
      data_pack[1][0] = 0;
      data_pack[1][1] = 0;
      data_pack[1][2] = 0;
      data_pack[1][3] = 0;
    }

    //insert data pack into data.
    int data_index = data_pack[0][0];
    int[] data_pack_data = data_pack[1];
    for(int i = 0; i < data_pack_data.length; i++) {
      int index = data_index + i;
      data[index] = data_pack[1][i];
    }
  }

  private void resetData() {
    for (int i=0; i < data.length;i++) {
      data[i] = 0;
    }

    //this.time = System.currentTimeMillis();
  }

  public void writeEmptySteps(){
    int channel = 0;
    for(int x=0;x<16;x++) {
      for(int y=0;y<64;y++){
        cursor_clip.setStep(channel,x,y,127);
      }
    }
  }
  public void writeData(int[] data){

    //Determine if steps exist
    NoteStep note_step = cursor_clip.getStep(0,0,0);
    if(note_step.state()==NoteStep.State.Empty) {
      cursor_clip.setStepSize(0.25); //16th notes.
      //No notes so write a bunch of empty notes
      writeEmptySteps();
    }


    int[] data_pack = {0,0,0,0};

    int data_pack_iterator = 0;
    int data_pack_id = 0;

    for(int data_count=0; data_count < data.length; data_count++) {
      if (data_pack_iterator == 3 || data_count == data.length -1) {
        packDataIntoNote(data_pack_id, data_pack);

        //Reset datapack values to -1;
        for(int d=0; d < data_pack.length; d++) {
          data_pack[d] = 0;
        }

        //Increment ID and reset iterator
        data_pack_id = data_pack_id + 1;
        data_pack_iterator = 0;
      }
      data_pack[data_pack_iterator] = data[data_count];
      data_pack_iterator++;
    }
  }

  private void packDataIntoNote(int data_pack_id, int[] data_pack){
    int midi_channel = 0;
    int note_y = (int) Math.floor(data_pack_id/16);
    int note_x = (int) data_pack_id - (note_y * 16);

      cursor_clip.setStep(midi_channel, note_x, note_y, 127, 0.25);
      final NoteStep note_step = cursor_clip.getStep(midi_channel,note_x,note_y);

      final double data_pack_0 = (double) ((double) data_pack[0]) / 127.0;
      final double data_pack_1 = (double) ((double) data_pack[1]) / 127.0;
      final double data_pack_2 = (double) ((double) data_pack[2]) / 127.0;
      final double data_pack_3 = (double) ((double) data_pack[3]) / 127.0;

      note_step.setReleaseVelocity(data_pack_0);
      note_step.setGain(data_pack_1);
      note_step.setTimbre(data_pack_2);
      note_step.setPressure(data_pack_3);


//      host.scheduleTask ( () -> {
//        note_step.setReleaseVelocity(data_pack_0);
//        note_step.setGain(data_pack_1);
//        note_step.setTimbre(data_pack_2);
//        note_step.setPressure(data_pack_3);
////        this.updateStepVelocity (channel, step, row, noteStep.getVelocity ());
////        this.updateStepDuration (channel, step, row, noteStep.getDuration ());
////        this.updateStepGain (channel, step, row, noteStep.getGain ());
////        this.updateStepPan (channel, step, row, noteStep.getPan ());
////        this.updateStepPressure (channel, step, row, noteStep.getPressure ());
////        this.updateStepReleaseVelocity (channel, step, row, noteStep.getReleaseVelocity ());
////        this.updateStepTimbre (channel, step, row, noteStep.getTimbre ());
////        this.updateStepTranspose (channel, step, row, noteStep.getTranspose ());
//
//      }, 100);

  }
  private int[][] unpackDataFromNote(NoteStep note_step){
    int[][] data_pack = new int[2][];
    int[] data_pack_id = new int[1];
    int[] data_pack_data = new int[4];

    //Get pack id
    int midi_channel = 0;
    int note_x = note_step.x();
    int note_y = note_step.y();

    data_pack_id[0] = (int) (note_x + (note_y * 16));
    //Unpack data
    double release_velocity = note_step.releaseVelocity();
    double gain = note_step.gain();
    double timbre = note_step.timbre();
    double pressure = note_step.pressure();

    double release_velocity_midi = (double) (release_velocity * 127);
    double gain_midi = (double) (gain * 127);
    double timbre_midi = (double) (timbre * 127);
    double pressure_midi = (double) (pressure * 127);


    data_pack_data[0] = (int) Math.round(release_velocity_midi);
    data_pack_data[1] = (int) Math.round(gain_midi);
    data_pack_data[2] = (int) Math.round(timbre_midi);
    data_pack_data[3] = (int) Math.round(pressure_midi);
    data_pack[0] = data_pack_id;
    data_pack[1] = data_pack_data;

    return data_pack;
  }
  public int[] readData(){
    //Send a copy of internal data back...
    int[] copy_data = Arrays.copyOf(data, data.length);
    return copy_data;
  }
}
