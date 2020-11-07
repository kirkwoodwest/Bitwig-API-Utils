package com.kirkwoodwest.utils.channelfinder;

import com.bitwig.extension.controller.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChannelFinder {

  private static ChannelFinder channel_finder_instance = null;
  private SettableStringValue status_setting;

  private static final int CHANNEL_FINDER_TRACK_COUNT = 128;
  private static final int SEARCH_INTERVAL            = 200;
  private static final int SEARCH_RANDOM_OFFSET       = 200;
  private static final int INIT_TIME = 1000;


  boolean init = false;

  private Channel[]      channels        = new Channel[CHANNEL_FINDER_TRACK_COUNT];
  private Map            channel_names   = new HashMap(CHANNEL_FINDER_TRACK_COUNT);
  private TrackBank      track_bank      = null;
  private ControllerHost controller_host = null;

  private ArrayList<ChannelFinderData> channel_finder_data;
  private ChannelFinderSearchBank[] searches;
  private int                       searches_index;

  //public static final Function<Integer, Integer> doubleFunction = input -> input * 2;
  //public static Integer fresh = doubleFunction(1);

  public ChannelFinder(ControllerHost host) {
    controller_host = host;
    searches = new ChannelFinderSearchBank[256];
    searches_index = 0;

    //Set up track bank for matching track names
    track_bank = host.createTrackBank(CHANNEL_FINDER_TRACK_COUNT, 0, 0, true);
    track_bank.scrollPosition().markInterested();

    //Mark names as interested.
    for (int i = 0; i < CHANNEL_FINDER_TRACK_COUNT - 1; i++) {
      Channel channel = track_bank.getItemAt(i);
      channel.name().markInterested();

      final int index = i;
      channel.name().addValueObserver(name -> this.name_update(name, index));
      channel.volume().setIndication(false);

      SettableStringValue name = channel.name();
      channels[i] = channel;
      channel_names.put(name, channel);
    }

    //Set up channel finder data...
    channel_finder_data = new ArrayList<ChannelFinderData>();

    //Delay this thing...
    Runnable task = () -> this.set_init_flag();
    controller_host.scheduleTask(task, INIT_TIME);
  }

  public void add(TrackBank track_bank, SettableStringValue track_bank_setting){
    ChannelFinderData data = new ChannelFinderData(ChannelFinderData.Type.BANK_SETTING, track_bank, track_bank_setting);
    channel_finder_data.add(data);

    track_bank.channelCount().markInterested();
    int bank_size = track_bank.getSizeOfBank();
    for (int i = 0; i < bank_size; i++) {
      Channel channel = track_bank.getItemAt(i);
      channel.name().markInterested();
    }
  }

  public void add(TrackBank track_bank, String channel_name){
    ChannelFinderData data = new ChannelFinderData(ChannelFinderData.Type.BANK_STRING, track_bank, channel_name);
    channel_finder_data.add(data);

    track_bank.channelCount().markInterested();
    int bank_size = track_bank.getSizeOfBank();
    for (int i = 0; i < bank_size; i++) {
      Channel channel = track_bank.getItemAt(i);
      channel.name().markInterested();
    }
  }


  public void add(CursorTrack cursor_track, SettableStringValue cursor_track_setting){
    ChannelFinderData data = new ChannelFinderData(ChannelFinderData.Type.CURSOR_SETTING, cursor_track, cursor_track_setting);
    channel_finder_data.add(data);

    cursor_track.isPinned().markInterested();
    cursor_track.isPinned().set(true);
    cursor_track.name().markInterested();
  }

  public void add(CursorTrack cursor_track, String channel_name){
    ChannelFinderData data = new ChannelFinderData(ChannelFinderData.Type.CURSOR_STRING, cursor_track, channel_name);
    channel_finder_data.add(data);

    cursor_track.isPinned().markInterested();
    cursor_track.isPinned().set(true);
    cursor_track.name().markInterested();
  }

  private void name_update(String s, Integer index) {
    //TODO: Delete me... we just gonna look at the names directly
    // controller_host.println("name: " + s + " : " + String.valueOf(index));
  }
  public void doFind() {
    status_setting.set("Searching...");
    //Reset flag on channel data
    for(ChannelFinderData data : channel_finder_data) {
      data.search_complete = false;
      data.search_success = false;
    }
    for(ChannelFinderData data : channel_finder_data) {
      if (data.type == ChannelFinderData.Type.BANK_SETTING) {
        findTrackBank(data.channel_name.get(), data);
      } else if (data.type == ChannelFinderData.Type.CURSOR_SETTING) {
        findCursorTrack(data.channel_name.get(), data);
      } else if (data.type == ChannelFinderData.Type.BANK_STRING) {
        findTrackBank(data.channel_string_name, data);
      } else if (data.type == ChannelFinderData.Type.CURSOR_STRING) {
        findCursorTrack(data.channel_string_name, data);
      }
    }
  }

  private void set_init_flag() {
    init = true;
  }

  public void init() {
    DocumentState document_state = controller_host.getDocumentState();
    status_setting = document_state.getStringSetting("Status", "Channel Finder", 12, "Initialized...");
    Signal rescan_settings = document_state.getSignalSetting("Rescan", "Channel Finder", "Rescan Tracks");
    rescan_settings.addSignalObserver(this::doFind);
    doFind();
  }

  public void updateSetting(SettableStringValue setting) {
    for(ChannelFinderData data : channel_finder_data) {
      if (data.channel_name == setting) {
        if (data.type == ChannelFinderData.Type.BANK_SETTING) {
          findTrackBank(data.channel_name.get(), data);
        } else if (data.type == ChannelFinderData.Type.CURSOR_SETTING) {
          findCursorTrack(data.channel_name.get(), data);
        }
      }
    }
  }

  public void findCursorTrack(String name, ChannelFinderData data) {
    track_bank.scrollPosition().set(0);

    if (!init) {
      //Delay this thing...
      Runnable task = () -> this.findCursorTrack(name, data);
      controller_host.scheduleTask(task, INIT_TIME);
    }

    CursorTrack cursor_track = data.cursor_track;

    for (int i = 0; i < CHANNEL_FINDER_TRACK_COUNT - 1; i++) {
      Channel channel      = track_bank.getItemAt(i);
      String  channel_name = channel.name().get();

      if (channel_name.equals(name)) {
        cursor_track.selectChannel(channel);
        cursor_track.isPinned().set(true);
        data.search_complete = true;
        data.search_success = true;
        reportChannels();
        return;
      }
    }

    data.search_complete = true;
    data.search_success = false;
    reportChannels();
    return;
  }

  private void reportChannels() {
    boolean all_channels_found = true;
    for (ChannelFinderData data : channel_finder_data) {
      if (data.search_complete == false) {
        all_channels_found = false;
        return;
      }
    }

    String channels_missing = "";
    for (ChannelFinderData data : channel_finder_data) {
      if (data.search_success == false) {
        if (data.type == ChannelFinderData.Type.BANK_SETTING || data.type == ChannelFinderData.Type.CURSOR_SETTING) {
          channels_missing = channels_missing + data.channel_name.get() + " ";
        } else {
          channels_missing = channels_missing + data.channel_string_name + " ";
        }
      }
    }

    if (channels_missing.isEmpty()) {
      //TODO: Print List of found channels
      controller_host.println("ALL CHANNELS FOUND.");
      status_setting.set("Complete.");
    } else {
      //TODO: Print List of found channels
      controller_host.println("ALL CHANNELS NOT FOUND... " + channels_missing);
      status_setting.set("MISSING: " + channels_missing);
    }
    //Build missing channels string


  }

  // TODO: Fix the Channel finder by not using a target bank. When it does the find it should do a looping search.
  // But also first search the current name to start out with.
  public void findTrackBank(String name, ChannelFinderData data) {
    final int               index  = searches_index;
    ChannelFinderSearchBank search = new ChannelFinderSearchBank();

    TrackBank track_bank = data.track_bank;

    //setup search object
    search.track_bank = data.track_bank;
    search.name = name;
    search.position = 0;
    search.data = data;

    searches[index] = search;

    //reset the track bank to position 0.
    track_bank.scrollPosition().set(search.position);

    //Start Search
    Runnable task = () -> this.trackBankFindNext(index);
    controller_host.scheduleTask(task, getSearchInterval());

    //Increment Search Index for next search
    searches_index++;
    if (searches_index >= searches.length) {
      searches_index = 0;
    }
  }

  private void trackBankFindNext(int index) {
    ChannelFinderSearchBank search              = searches[index];
    TrackBank               track_bank          = search.track_bank;
    String                  target_channel_name = search.name;
    int                     position            = search.position;
    ChannelFinderData       data                = search.data;

    Channel channel      = track_bank.getItemAt(0);
    String  channel_name = channel.name().get();

    //Get the channel count so we can determine if Bitwig actually has the tracks ready.
    int channel_count = track_bank.channelCount().get();

    if (channel_count != 0) {
      //Its initialized so we can compare things and this and that...
      controller_host.println("Channel Search[" + String.valueOf(index) + "]: " + channel_name + " -> " + target_channel_name);

      if (channel_name.equals(target_channel_name)) {
        //Last repositioning of the scrollPosition Matched so just exit.
        //clear out search data.
        searches[index] = null;
        controller_host.println("Channel Found: [" + String.valueOf(index) + "]: " + channel_name + " -> " + target_channel_name);

        data.search_complete = true;
        data.search_success = true;
        reportChannels();
        return;

      } else if (position >= channel_count) {
        //Target Bank position exceeds our channel count so we can just stop.
        searches[index] = null;
        controller_host.println("Channel Not Found: [" + String.valueOf(index) + "]: " + channel_name + " -> " + target_channel_name);

        track_bank.scrollPosition().set(0);
        data.search_complete = true;
        data.search_success = false;
        reportChannels();
        return;
      } else {
        //Advance to Next Position and schedule to evaluate again.
        track_bank.scrollPosition().set(position);
        position++;
        searches[index].position = position;
      }
    }

    //Run the Search again...
    Runnable task = () -> this.trackBankFindNext(index);
    controller_host.scheduleTask(task, getSearchInterval());
  }
  private int getSearchInterval(){
    int interval = (int) (((float) SEARCH_INTERVAL) + Math.random() * (float) SEARCH_RANDOM_OFFSET);
    return interval;
  }
}