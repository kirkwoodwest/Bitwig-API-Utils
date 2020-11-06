package com.kirkwoodwest.utils.clipdata.data;

public interface ClipData {
  public enum DataType {
    Note, //Puts Data into Note Format. Simplest easy to read data format.
    Velocity, //Data fits into velocity but can use more notes. one one midi channel 1024 elements, 32768 total on all channels.(untested)
    Max, //Data fits into all data slots possible. Do not use.
  }
  public void writeData(int[] data);
  public int[] readData();
}
