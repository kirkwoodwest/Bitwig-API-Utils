package com.kirkwoodwest.utils.clipdata;

import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.channelfinder.ChannelFinder;
import com.kirkwoodwest.utils.channelfinder.ChannelFinderSetting;
import com.kirkwoodwest.utils.clipdata.data.ClipData;
import com.kirkwoodwest.utils.clipdata.data.ClipDataMax;
import com.kirkwoodwest.utils.clipdata.data.ClipDataNote;
import com.kirkwoodwest.utils.clipdata.data.ClipDataVelocity;

public class ClipDataHandler {
  public enum DataType {
    Note, //Puts Data into Note Format. Simplest easy to read data format.
    Velocity, //Data fits into velocity but can use more notes. one one midi channel 1024 elements, 32768 total on all channels.(untested)
    Max, //Data fits into all data slots possible. Do not use.
  }
  private final ClipDataParent clip_data_parent;
  private       int            playing_slot_index = -1; //the current playing slot, where we pull data from.
  private       ClipData       clip_data;

  //Bitwig Objects
  private ControllerHost             host; //Hardware class
  private final TrackBank   track_bank;
  private final CursorTrack cursor_track;
  private final CursorClip  cursor_clip;


  //Tool for writing and receiving clip data in a clip...
  public ClipDataHandler(ControllerHost host, ChannelFinder channel_finder, ClipDataParent clip_data_parent, DataType clip_data_type, int num_scene_slots, int data_count){

    this.host = host;
    DocumentState document_state = host.getDocumentState();
    this.clip_data_parent = clip_data_parent;

    String track_name = clip_data_parent.getTrackName();
    track_bank = host.createTrackBank(1, 0, num_scene_slots);

    //TODO: set up channel finder...
    //TODO: update the cursor track for clip data name... this should come in via handler...
    //TODO: add channel finder thing to this...

    cursor_track = host.createCursorTrack(track_name + "_CURSOR_TRACK", track_name, 0, 1, true);
    cursor_clip = cursor_track.createLauncherCursorClip(track_name + "CURSOR_CLIP", "Data Clip", 16, 128);
    cursor_clip.isLoopEnabled().markInterested();
    cursor_track.isPinned().markInterested();

    //ChannelFinderSetting remote_handler_setting = new ChannelFinderSetting(document_state, channel_finder, cursor_track, track_name, track_name, track_name);
    //remote_handler_setting.add(track_bank);
    channel_finder.add(cursor_track, track_name);
    channel_finder.add(track_bank, track_name);

    //Determine data count and swap out Clip Data with an alternate one
    switch (clip_data_type) {
      case Note:
        clip_data = new ClipDataNote(cursor_clip, data_count);
        break;
      case Velocity:
        clip_data = new ClipDataVelocity(cursor_clip, data_count);
        break;
      case Max:
        clip_data = new ClipDataMax(host, cursor_clip, data_count);
        break;
    }

    Track                track     = track_bank.getItemAt(0);
    track.volume().setIndication(false);
    ClipLauncherSlotBank slot_bank = track.clipLauncherSlotBank();

    slot_bank.addIsPlayingObserver(this::playingStatusChanged);
    slot_bank.addIsPlaybackQueuedObserver(this::playingStatusQueued);
    slot_bank.setIndication(true);
  }

  private void playingStatusChanged(int slot_index, boolean is_playing) {
     if (is_playing == true){
      playing_slot_index = slot_index;
      int[] data = clip_data.readData();
      clip_data_parent.clipDataChanged(data);
    }

    if (is_playing == false && playing_slot_index == slot_index) {
      //This is true if the current playing slot is stopped.
      playing_slot_index = -1;
    }
  }

  public void writeData(int[] data){
    clip_data.writeData(data);
  }

  private void playingStatusQueued(int slot_index, boolean is_queued) {
    if (is_queued == true) {

      //clip_data.resetData();
      Track                track              = track_bank.getItemAt(0);
      ClipLauncherSlotBank launcher_slot_bank = track.clipLauncherSlotBank();
      ClipLauncherSlot     launcher_slot      = launcher_slot_bank.getItemAt(slot_index);

      //This forces the data to be updated and when its played it will be sent back.
      launcher_slot.select();
    }
  }
}
