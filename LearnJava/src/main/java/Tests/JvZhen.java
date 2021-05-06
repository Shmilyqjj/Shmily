package Tests; /**
 * 给出二维矩阵中最长的递增路径
 *例如二维数组如下：
 *
 * [
 *
 *   [9,9,4],
 *
 *   [6,6,8],
 *
 *   [2,1,1]
 *
 * ]
 *
 * 输出
 * 最长的递增路径为4，是[1, 2, 6, 9]
 *
 *
 * 样例输入
 * 3
 * 3
 * 9 9 4
 * 6 6 8
 * 2 1 1
 * 样例输出
 * 4
 *
 */

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class JvZhen {


/*请完成下面这个函数，实现题目要求的功能
当然，你也可以不按照下面这个模板来作答，完全按照自己的想法来 ^-^
******************************开始写代码******************************/

    static int longpath(int[][] matrix) {
        if(matrix.length ==0 || matrix[0].length == 0){
            return 0;
        }
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        int[][] len = new int[matrix.length][matrix[0].length];
        int max = 0;
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                max = Math.max(max,find(matrix,visited,len,i,j));
            }
        }
        return max;
    }
    private static  int[] r = {-1,1,0,0};
    private static int[] c = {0,0,-1,1};
    private static int find(int[][] matrix,boolean[][] visited,int[][] len,int x,int y){
        if(visited[x][y])
            return len[x][y];
        len[x][y] = 1;
        for(int i=0;i<4;i++){
            int dqX = x + r[i];
            int dqY = y + c[i];
            if(dqX >=0 && dqX < matrix.length && dqY >=0 && dqY<matrix[0].length && matrix[dqX][dqY] < matrix[x][y]){
                len[x][y] = Math.max(len[x][y],find(matrix,visited,len,dqX,dqY)+1);
            }
        }
        visited[x][y] = true;
        return len[x][y];
    }
    /******************************结束写代码******************************/


    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        int res;

        int _matrix_rows = 0;
        int _matrix_cols = 0;
        _matrix_rows = Integer.parseInt(in.nextLine().trim());
        _matrix_cols = Integer.parseInt(in.nextLine().trim());

        int[][] _matrix = new int[_matrix_rows][_matrix_cols];
        for(int _matrix_i=0; _matrix_i<_matrix_rows; _matrix_i++) {
            for(int _matrix_j=0; _matrix_j<_matrix_cols; _matrix_j++) {
                _matrix[_matrix_i][_matrix_j] = in.nextInt();

            }
        }

        if(in.hasNextLine()) {
            in.nextLine();
        }

        res = longpath(_matrix);
        System.out.println(String.valueOf(res));

    }
}
