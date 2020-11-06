package com.kirkwoodwest.utils.channelfinder;

import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.channelfinder.ChannelFinder;

/*
  Creates a setting for a track in the document and sets up a relationship to that setting with the channel finder.
 */
public class ChannelFinderSetting {
  private SettableStringValue setting;
  private ChannelFinder channel_finder;

  public ChannelFinderSetting(DocumentState document_state, ChannelFinder channel_finder, CursorTrack cursor_track, String setting_id, String setting_name, String default_value) {
    createSetting(document_state,setting_id,setting_name,default_value);

    //Setup cursor Track in the Channel Finder
    channel_finder.add(cursor_track, setting);
    this.channel_finder = channel_finder;
  }

  public ChannelFinderSetting(DocumentState document_state, ChannelFinder channel_finder, TrackBank track_bank, String setting_id, String setting_name, String default_value) {
    createSetting(document_state,setting_id,setting_name,default_value);

    //Setup cursor Track in the Channel Finder
    channel_finder.add(track_bank, setting);
    this.channel_finder = channel_finder;
  }

  //Method to add additional cursor track to the track finder
  public void add(CursorTrack cursor_track) {
    channel_finder.add(cursor_track, setting);
  }

  //Method to add addition track bank to the track finder...
  public void add(TrackBank track_bank) {
    channel_finder.add(track_bank, setting);
  }

  private void createSetting(DocumentState document_state, String setting_id, String setting_name, String default_value) {
    final int num_chars = 8;
    setting = document_state.getStringSetting(setting_name, setting_id, num_chars, default_value);
    setting.markInterested();
    setting.addValueObserver(this::settingChanged);
  }

  private void settingChanged(String s) {
    channel_finder.updateSetting(setting);
  }

  public SettableStringValue getSetting() {
    return setting;
  }
}
