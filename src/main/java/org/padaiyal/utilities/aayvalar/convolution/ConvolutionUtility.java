package org.padaiyal.utilities.aayvalar.convolution;

import java.util.Arrays;

public class ConvolutionUtility {

  /**
   * Generates the convolution of two dimensional arrays.
   *
   * @param f Something.
   * @param g Something.
   * @return The convolution of the two provided arrays.
   */
  public static double[][] convolveTwoDimensionalArrays(double[][] f, double [][] g) {

    int gColumnNumbers = g[1].length;
    int gRowNumbers = g.length;
    int columnNumbers = Math.abs(f[1].length - g[1].length) + 1;
    int rowNumbers = Math.abs(f.length - g.length) + 1;
    double[][] output = new double [rowNumbers][columnNumbers];
    for (int i = 0; i < rowNumbers; i++) {
      for (int j = 0; j < columnNumbers; j++) {
        double calculation = 0;
        for (int a = 0; a < gRowNumbers; a++) {
          for (int b = 0; b < gColumnNumbers; b++) {
            calculation += (f[i + a][j + b] * g[a][b]);
          }
        }
        output[i][j] = calculation;
      }
    }
    return output;

  }

  public static void main(String[] args) {
    double[][] f = {
        {255, 255, 255, 10, 10, 10},
        {255, 255, 255, 10, 10, 10},
        {255, 255, 255, 10, 10, 10},
        {255, 255, 255, 10, 10, 10},
        {255, 255, 255, 10, 10, 10},
        {255, 255, 255, 10, 10, 10},
        {255, 255, 255, 10, 10, 10},
        {255, 255, 255, 10, 10, 10}
    };

    double[][] g = {
        {1, 0, -1},
        {1, 0, -1},
        {1, 0, -1}
    };

    double[][] result = convolveTwoDimensionalArrays(f, g);
    for (double[] doubles : result) {
      System.out.println(Arrays.toString(doubles));
    }


  }
}
