package com.kirkwoodwest.utils;

public class Array {
  public static int indexOfIntArray(int[] array, int key) {
    int returnvalue = -1;
    for (int i = 0; i < array.length; ++i) {
      if (key == array[i]) {
        returnvalue = i;
        break;
      }
    }
    return returnvalue;
  }
}
