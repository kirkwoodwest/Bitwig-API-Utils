package com.kirkwoodwest.utils;

import java.util.function.Function;


//This is just a place for me to write out ideas and things i come across to make life easy...
//Not for actual use, and if something is used it needs to be moved to an official class.
public class Sketchpad {
  //TODO: is this an example of a callback? i don't know... fuck. do i even need it?
  public int xfunc(Function<Integer, Integer> function){
    int v = function.apply(1);
    return v;
  }
}
