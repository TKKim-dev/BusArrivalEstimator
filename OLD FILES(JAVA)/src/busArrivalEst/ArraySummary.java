package busArrivalEst;

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//This file is part of dLife
//Copyright (c) 2010 Grant Braught. All rights reserved.
//
//  dLife is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published
//  by the Free Software Foundation, either version 3 of the License,
//  or (at your option) any later version.
//
//  dLife is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty
//  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
//  See the GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public
//  License along with dLife. 
//  If not, see <http: www.gnu.org="" licenses="">.
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
import java.util.Arrays;

/**
* @author Grant Braught
* @author Dickinson College
* @version May 3, 2005
*/
public class ArraySummary {

public static double mean(double[] arr) {
    double total = 0;
    for (int i = 0; i < arr.length; i++) {
         total = total + arr[i];
    }
    return total / arr.length;
}

public static double max(double[] arr) {
    int maxIndex = 0;
    for (int i = 1; i < arr.length; i++) {
        if (arr[i] > arr[maxIndex]) {
            maxIndex = i;
        }
    }
    return arr[maxIndex];
}

public static double min(double[] arr) {
    int minIndex = 0;
    for (int i = 1; i < arr.length; i++) {
        if (arr[i] < arr[minIndex]) {
            minIndex = i;
        }
    }
    return arr[minIndex];
}

public static double median(double[] arr) {
    double[] arrClone = new double[arr.length];
    System.arraycopy(arr, 0, arrClone, 0, arr.length);
    Arrays.sort(arrClone);
    return arrClone[(int) (Math.round(arrClone.length / 2.0) - 1)];
}
  
public static double variance(double[] arr) {
    return variance(arr, mean(arr));
}

public static double variance(double[] arr, double mean) {
    double totalDev = 0;
    for (int i = 0; i < arr.length; i++) {
        totalDev = totalDev + (mean - arr[i]) * (mean - arr[i]);
    }
    // Sample estimate of variance so divide by n-1.
    return totalDev / (arr.length - 1);
}


public static double stdDev(double variance) {
    return Math.sqrt(variance);
}

public static double stdDev(double[] arr) {
    return stdDev(variance(arr));
}

}
