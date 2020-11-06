package com.kirkwoodwest.utils.channelfinder;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.TrackBank;

public class ChannelFinderData {


  public enum Type {
    BANK_SETTING,
    BANK_STRING,
    CURSOR_SETTING,
    CURSOR_STRING,
  }
  public SettableStringValue channel_name;
  public String channel_string_name;
  public CursorTrack         cursor_track;
  public TrackBank           track_bank;
  public Type                 type;
  public boolean search_complete = false;
  public boolean search_success = false;


  public ChannelFinderData(Type type, CursorTrack cursor_track,  String channel_name){
    this.type = type;
    this.cursor_track = cursor_track;
    this.channel_string_name = channel_name;
  }

  public ChannelFinderData(Type type, CursorTrack cursor_track, SettableStringValue channel_name){
    this.type = type;
    this.cursor_track = cursor_track;
    this.channel_name = channel_name;
  }

  public ChannelFinderData(Type type, TrackBank track_bank, SettableStringValue channel_name){
    this.type = type;
    this.track_bank = track_bank;
    this.channel_name = channel_name;
  }

  public ChannelFinderData(Type type, TrackBank track_bank, String channel_name){
    this.type = type;
    this.track_bank = track_bank;
    this.channel_string_name = channel_name;
  }
}
