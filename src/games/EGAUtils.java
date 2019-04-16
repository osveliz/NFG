package games;

import util.MTRandom;
import util.RandomNumberGenerator;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Contains several global static utilities, including random number generation, string builders, formatters, etc.
 */
public class EGAUtils {

  // used for creating output; don't need to be replicated everywhere
  public static final NumberFormat nf = new DecimalFormat("0.000");

  // random number generator used for standard random generation
  // this is a Mersenne Twister generator, which has a very long period and other advangtages oover
  // the standard java Random implementation.
  // this implementation is an extension of the base java.Random class
  public static final Random rand = new Random();

  // a different implementation of the Mersenne Twister random number generator
  // this version does not extend the java.Random, so it is a bit less useable
  // however, this version has utilities for generating samples from additioal distributions
  // (exp, gamma, chi-squared, poisson, beta...)
  public static final RandomNumberGenerator rng = new RandomNumberGenerator();

  // keep a collection of these available for general use so that we don't recreate them so much
  private static final List<StringBuilder> stringBuilders = new ArrayList<StringBuilder>();

  // get an availabe string builder
  // if none are available, create a new one
  public static StringBuilder getSB() {
    if (stringBuilders.isEmpty()) {
      return new StringBuilder();
    }
    StringBuilder tmpSB = stringBuilders.remove(stringBuilders.size() - 1);
    tmpSB.setLength(0);
    return tmpSB;
  }

  // return the stringbuilder for future use
  public static String returnSB(StringBuilder sb) {
    stringBuilders.add(sb);
    return sb.toString();
  }

  // Copies src file to dst file.
  // If the dst file does not exist, it is created
  public static void copy(File src, File dst) throws IOException {
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst);
    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
  }

  // private constructor
  private EGAUtils() {
  }
}
