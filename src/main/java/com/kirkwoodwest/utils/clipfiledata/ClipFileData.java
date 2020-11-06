package com.kirkwoodwest.utils.clipfiledata;

import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.FileChooser;
import com.kirkwoodwest.utils.channelfinder.ChannelFinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClipFileData {

  //Holds a list of listeners defined by ID.
  private final HashMap<String, ClipFileDataParent> data_parents = new HashMap<>();

  ArrayList<DataItem> data_items = new ArrayList<>();

  private String file_path;
  private String file_name;
  private String file_ext = ".txt";
  private String track_id;

  //Document Preference Settings
  private final SettableStringValue setting_data_file_path;
  private final SettableStringValue setting_data_file_name;
  private final Signal              setting_save_data;
  private final SettableStringValue setting_track_id;

  //Bitwig Objects
  private ControllerHost      host;
  private TrackBank track_bank;
  private ClipLauncherSlotBank track_slot_bank;
  private int playing_slot_index;
  private DataItem data_item;

  public ClipFileData(ControllerHost host, ChannelFinder channel_finder, int num_scene_slots) {
    this.host = host;
    Application app = host.createApplication();
    app.projectName().markInterested();

    //Document Settings
    DocumentState document_state = host.getDocumentState();
    setting_data_file_path = document_state.getStringSetting("Data File Path", "Clip File Data", 20, "/Users/slomocorp/Documents/Bitwig Studio/Centrifuge Settings/");
    setting_data_file_path.addValueObserver(this::filePathChanged);

    setting_data_file_name = document_state.getStringSetting("Data File", "Clip File Data", 20, "settings.txt");
    setting_data_file_name.addValueObserver(this::fileNameChanged);

    setting_track_id = document_state.getStringSetting("Track ID", "Clip File Data Track", 20, "Default Track ID");
    setting_track_id.addValueObserver(this::trackIdChanged);

    setting_save_data = document_state.getSignalSetting("Save Data", "Clip File Data Track", "SAVE DATA");
    setting_save_data.addSignalObserver(this::saveData);

    //Use defaults for settings
    file_path = setting_data_file_path.get();
    file_name = setting_data_file_name.get();
    track_id = setting_track_id.get();

    //Setup Track Bank and Observers.
    String track_name = "CLIP DATA";
    track_bank = host.createTrackBank(1, 0, num_scene_slots);
    channel_finder.add(track_bank, track_name);

    Track                track     = track_bank.getItemAt(0);
    track_slot_bank = track.clipLauncherSlotBank();

    track.volume().setIndication(false);
    ClipLauncherSlotBank slot_bank = track.clipLauncherSlotBank();
    slot_bank.addNameObserver(this::clipNameChange); //This doesn't get used but allows us to access names.

    slot_bank.addIsPlayingObserver(this::playingStatusChanged);
    slot_bank.addIsPlaybackQueuedObserver(this::playingStatusQueued);
    slot_bank.setIndication(true);

    readFromFile();
    //saveData();

    // app.projectName().addValueObserver(this::projectNameChanged);
    // String project_name = app.projectName().get();
    //  host.println("PROJECT NAME: " + project_name);
  }

  private void clipNameChange(int slot_index, String s) {
    host.println(s);
  }


  private void playingStatusChanged(int slot_index, boolean is_playing) {
    if (is_playing == true){
      playing_slot_index = slot_index;
      ClipLauncherSlot slot = track_slot_bank.getItemAt(slot_index);
      String name = slot.name().get();
      track_id = name;
      setting_track_id.set(name);
      updateParents();
    }

    if (is_playing == false && playing_slot_index == slot_index) {
      //This is true if the current playing slot is stopped.
      playing_slot_index = -1;
    }
  }

  private void playingStatusQueued(int slot_index, boolean is_queued) {
    if (is_queued == true) {

      //clip_data.resetData();
      ClipLauncherSlot     launcher_slot      = track_slot_bank.getItemAt(slot_index);

      //This forces the data to be updated and when its played it will be sent back.
      launcher_slot.select();
    }
  }

  public void addParent(ClipFileDataParent parent, String data_id) {
    data_parents.put(data_id, parent);
  }

  private void filePathChanged(String s) {
    file_path = s;
  }

  private void trackIdChanged(String s) {
    track_id = s;
  }

  private void saveData() {
    getDataFromParents();
    writeToFile();
    openFileDialog();
  }

  private void fileNameChanged(String s) {
    file_name = s;
  }
  
  private void getDataFromParents(){
    int size = data_items.size();
    data_item = null;
    for(int i = 0; i< size; i++) {
      DataItem data_item_test = data_items.get(i);
      if (data_item_test.track_id.equals(track_id)){
        data_item = data_item_test;
        break;
      }
    }
    if (data_item != null) {
      for(Map.Entry<String, ClipFileDataParent> entry : data_parents.entrySet()) {
        String             data_id = entry.getKey();
        ClipFileDataParent parent  = entry.getValue();
        int[]              data    = getData(track_id, data_id);
        data_item.data.put(data_id, data);
      }
    }
  }

  private void updateParents() {
    for(Map.Entry<String, ClipFileDataParent> entry : data_parents.entrySet()) {
      String  data_id   = entry.getKey();
      ClipFileDataParent parent = entry.getValue();

      int[] data = getData(track_id, data_id);
      if (data == null) {
        //Since there is no data what we do is get it from the parent and write it instead.
        if (!track_id.equals("")) {
          data = parent.clipFileDataGet();
          setData(track_id, data_id, data);
        }
        return;
      };

      parent.clipFileDataSet(data);
    }
  }

  private int[] getData(String track_id, String data_id) {
    int      data_items_length = data_items.size();
    DataItem data_item         = null;
    for (int i=0;i<data_items_length;i++) {
      DataItem data_item_test = data_items.get(i);
      if(data_item_test.track_id.equals(track_id)) {
        data_item = data_item_test;
        break;
      }
    }
    if (data_item != null) {
      if (data_item.data.containsKey(data_id)) {
        int[] data = data_item.data.get(data_id);
        int[] data_copy = data;// Lose Reference
        return data_copy;
      } else {
        return null;
      }
    }
    return null;
  }

  private void setData(String track_id, String data_id, int[] data) {
    int data_items_length = data_items.size();
    int[] data_copy = Arrays.copyOf(data, data.length); //Lose Reference.
    DataItem data_item = null;

    for(int i=0;i<data_items_length;i++) {
      //Find data item with the proper track id.
      DataItem data_item_test = data_items.get(i);
      if(data_item_test.track_id.equals(track_id)) {
        data_item = data_item_test;
        break;
      }
    }

    if (data_item == null) {
      //Data doesn't exist so we need to build it.
      data_item = new DataItem();
      data_item.track_id = track_id;
      data_item.addData(data_id, data_copy);
      data_items.add(data_item);
    }

    data_item.data.put(data_id, data);
  }

  private void readFromFile() {
    Charset charset = Charset.forName("US-ASCII");
    Path    path    = Paths.get(file_path + file_name);

    //Read From File
    try (BufferedReader reader = Files.newBufferedReader(path, charset)) {

      String   line      = null;
      DataItem data_item = new DataItem();

      while ((line = reader.readLine()) != null) {
        //Read New Track ID
        if (line.contains("#")) {
          data_item = new DataItem();
          data_item.track_id = line.substring(1);
          data_items.add(data_item);
        };
        if (line.contains(":")) {
          //Read data_group id
          String[] datas = line.split(":");
          String data_id = datas[0];
          String data_raw = datas[1];
          String[] data_string_split = data_raw.split(",");
          int data_count = data_string_split.length;
          int[] data_int_array = new int[data_count];

          for(int i =0;i<data_count;i++) {
            String data_string = data_string_split[i].replaceAll(" ", "");
            int data_int_value = Integer.valueOf(data_string);
            data_int_array[i] = data_int_value;
          }
          data_item.addData(data_id, data_int_array);
        }
      }
    } catch (IOException x) {
      host.println(String.format("ERROR %s%n", x));
    }
  }

  private void writeToFile(){
    Charset charset = Charset.forName("US-ASCII");
    Path    path    = Paths.get(file_path + file_name);

    String s = "";

    int data_items_length = data_items.size();
    DataItem data_item         = null;
    for (int i=0;i<data_items_length;i++) {
      DataItem data_item_test = data_items.get(i);
      s = s + "#" + data_item_test.track_id + "\n";

      for (Map.Entry<String, int[]> entry : data_item_test.data.entrySet()) {
        String track_id = entry.getKey();
        s = s + track_id + ":";
        int[]  data        = entry.getValue();
        String data_string = Arrays.toString(data);
        data_string = data_string.replaceAll("[\\[\\]]*", "");
        s = s + data_string + "\n";
      }
      s = s + "\n";
    }

    try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
      writer.write(s, 0, s.length());
    } catch (IOException x) {
      System.err.format("IOException: %s%n", x);
    }
  }

  private void openFileDialog(){
    //TODO: File Dialog
        /*
    JFileChooser chooser = new JFileChooser();
    // Note: source for ExampleFileFilter can be found in FileChooserDemo,
    // under the demo/jfc directory in the JDK.
    /*
    ExampleFileFilter filter = new ExampleFileFilter();
    filter.addExtension("jpg");
    filter.addExtension("gif");
    filter.setDescription("JPG & GIF Images");
    */
    // chooser.setFileFilter(filter);
    // */
//    int returnVal = chooser.showOpenDialog(this);
//    if(returnVal == JFileChooser.APPROVE_OPTION) {
//      System.out.println("You chose to open this file: " +
//              chooser.getSelectedFile().getName());
//    }
/*
    FileDialog fileChooser = new FileDialog(null);
    fileChooser.setMode(FileDialog.LOAD);
    fileChooser.setTitle("Select file or folder");
*/
// Setting this property, only folders are selectable
//// Unsetting the property, and only files are selectable
//    System.setProperty("apple.awt.fileDialogForDirectories", "true");
//
//    fileChooser.setVisible(true);
//
//    JFileChooser chooser = new JFileChooser();
//    chooser.setCurrentDirectory(new java.io.File("."));
//    chooser.setDialogTitle("choosertitle");
//    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//    chooser.setAcceptAllFileFilterUsed(false);
//
//    if (chooser.showOpenDialog(new JPanel()) == JFileChooser.APPROVE_OPTION) {
//      System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
//      System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
//    } else {
//      System.out.println("No Selection ");
//    }
    //NativeFileDialog dialog = new NativeFileDialog();
//    JFileChooser chooser = new JFileChooser();
//    JDialog wrapper = new JDialog((Window)null);
//    wrapper.setVisible(true);
//    chooser.showDialog(wrapper, "APPROVE");
    FileChooser file_chooser = new FileChooser();

    // System.out.println(f.getCurrentDirectory());
    // System.out.println(f.getSelectedFile());
  }
}
