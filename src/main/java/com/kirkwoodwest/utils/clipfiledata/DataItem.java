package com.kirkwoodwest.utils.clipfiledata;

import java.util.HashMap;

public class DataItem {
  public String             track_id = "";

  public HashMap<String,int[]> data = new HashMap();

  public void addData(String data_id, int[] data) {
    this.data.put(data_id, data);
  }
}
