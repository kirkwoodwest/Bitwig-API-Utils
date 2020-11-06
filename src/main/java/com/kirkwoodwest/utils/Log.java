package com.kirkwoodwest.utils;

import com.bitwig.extension.controller.api.ControllerHost;

/**
 * Simple Log tool to initialized in the main extension and then used in any class.
 */
public class Log {
  private static ControllerHost _host = null;
  public static void init(ControllerHost host){
    _host = host;
  }
  public static void print(String s){
    _host.println(s);
  }
  public static void println(String s){
    _host.println(s);
  }
}
