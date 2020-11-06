package com.kirkwoodwest.utils;

/**
 * Custom Math Class for remapping values
 */
public class Math {

  public static int doubleToRange(double v, double range){
    double new_v = v * range;
    return (int) java.lang.Math.round(new_v);
  }

  public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
  }

  public static double valueLimit(double x, double min, double max) {
    return (x > min) ? ((x < max) ? x : max) : min;
  }

}
