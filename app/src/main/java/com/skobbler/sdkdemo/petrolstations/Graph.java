package com.skobbler.sdkdemo.petrolstations;

/**
 * Created by marcinsendera on 06.12.2016.
 */
public class Graph {


    private int stationNumber;
    private GasStationList stationList;

    private double tankVolume;
    private double startVolume;

    private double infinity = Double.POSITIVE_INFINITY;

    /* adjacency matrix
     matrix size is n+1 x n+1, because we add the first station - s'
      to simulate the situation in which we have amount of fuel greater than 0

    matrix construction:
    | inf | inf | inf | inf |... | inf |
    |(1,2)| inf | inf | inf |... | inf |
    |(1,3)|(2,3)|  |  |... |  |
    |     |  |  |  |... |  |
    .
    .
    .
    |  |  |  |  |... |  |
    */

    public double[][] matrix;


    public Graph(int stationNumber, double volume, double startVolume, GasStationList list){

        this.stationList = list;
        this.stationNumber = stationNumber;
        this.tankVolume = volume;
        this.startVolume = startVolume;

        this.matrix = new double[stationNumber+1][stationNumber+1];

        // adding infinity at exact positions
        for (int i = 0; i <stationNumber+1; i++){
            for (int j = 0; j<=i; j++){
                matrix[i][j] = infinity;
            }
        }

        // setting distance from s' -> s (from 0 -> 1) for tankVolume - startVolume
        // so in fact set matrix[0][1] to tankVolume - startVolume and the rest matrix[0][i] to infinity

        for(int i = 0; i < stationNumber+1; i++){
            matrix[0][i] = infinity;
        }

        matrix[0][1] = tankVolume - startVolume;

        // setting other distances to the matrix

        for(int i = 1; i <stationNumber; i++){
            for(int j = i+1; j< stationNumber+1; j++){
                matrix[i][j] = list.distances.get(j - 1) - list.distances.get(i -1);
            }
        }

        // matrix is created!!!
    }



}

