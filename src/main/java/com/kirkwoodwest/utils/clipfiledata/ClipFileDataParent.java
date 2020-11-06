package com.kirkwoodwest.utils.clipfiledata;

public interface ClipFileDataParent {
  void clipFileDataSet(int[] data); //Method to update data from ClipRestoreData
  int[] clipFileDataGet(); //Method to get current data from the Listener
}
