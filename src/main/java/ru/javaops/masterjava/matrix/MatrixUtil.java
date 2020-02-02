package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * gkislin
 * 03.07.2016
 * Stanislav Popkov
 * 02.02.20
 */
public class MatrixUtil {


    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        int matrixSize = matrixA.length;
        int[][] matrixC = new int[matrixSize][matrixSize];
        int thatColumn[] = null;
        Future<int[]> taskArray = executor.submit(new GetArray(matrixB));
        while(!taskArray.isDone()) {
            thatColumn = taskArray.get();
        }

        List<Callable<Void>> callableList = new ArrayList<>();
        int bColumns = matrixB[0].length;
        int count = 0;
        for (int j = 0; j < bColumns; j++) {
            if (count == 1000000) {
                count = 0;
            }
            callableList.add(new WriteRow(matrixA, matrixC, thatColumn, count, j));
            count +=1000;
        }
        executor.invokeAll(callableList);
        return matrixC;
    }

    private static class WriteRow implements Callable<Void> {
        int[][] matrixA;
        int[][] matrixC;
        int thatColumn[];
        int count;
        int j;

        public WriteRow(int[][] matrixA, int[][] matrixC, int thatColumn[], int count, int j) {
            this.matrixA = matrixA;
            this.matrixC = matrixC;
            this.thatColumn = thatColumn;
            this.count = count;
            this.j = j;
        }

        @Override
        public Void call()  {
            int size = matrixC.length;
            for (int i = 0; i < size; i++) {
                int thisRow[] = matrixA[i];
                int summand = 0;
                for (int k = 0; k < size; k++) {
                    summand += thisRow[k] * thatColumn[count + k];
                }
                matrixC[i][j] = summand;
            }
            return null;
        }
    }

    private static class GetArray implements Callable<int []> {
        int[][] matrixB;

        public GetArray(int[][] matrixB) {
            this.matrixB = matrixB;
        }

        @Override
        public int[] call() throws Exception {
            int size = matrixB.length;
            int thatColumn[] = new int[size * size];
            int count = 0;
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    thatColumn[count + k] = matrixB[k][j];
                }
                count +=1000;
            }
            return thatColumn;
        }
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int aColumns = matrixA[0].length;
        int aRows = matrixA.length;
        int bColumns = matrixB[0].length;
        int bRows = matrixB.length;

        int[] thatColumn = new int[matrixSize];

        for (int j = 0; j < bColumns; j++) {
            for (int k = 0; k < aColumns; k++) {
                thatColumn[k] = matrixB[k][j];
            }

            for (int i = 0; i < aRows; i++) {
                int thisRow[] = matrixA[i];
                int summand = 0;
                for (int k = 0; k < aColumns; k++) {
                    summand += thisRow[k] * thatColumn[k];
                }
                matrixC[i][j] = summand;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
