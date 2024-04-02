import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class Main {
    static ArrayList<String> lagrangeString = new ArrayList<>();
    static double round(double value)
    {
        return Math.round(value * 10000) / 10000.0;
    }
    static double function(double x)
    {
        return round(sin(x/2.0)-x*x+1.0);
    }
    final static double step = PI/5.0;
    final static double startPoint = 0.0;
    final static double endPoint = PI;
    final static double[] points = new double[]{0.81,1.44,2.81, 2*PI/5};
    static String addUntilSize(String start, int size)
    {
        StringBuilder result = new StringBuilder(start);
        int symbolsNeeded = size - start.length();
        for(int i = 0;i<symbolsNeeded;i++) result.insert(0," ");
        return result.toString();
    }
    static double[][] calculateYsNewton(double[] x, double[] startY)
    {
        double[][] layers = new double[5][];
        layers[0] = startY;
        for(int i = 1;i<layers.length;i++)
        {
            layers[i] = newtonStep(x, layers[i-1]);
        }
        return layers;
    }
    static double[] newtonStep(double[] x, double[] prevLayer)
    {
        double[] result = new double[prevLayer.length-1];
        for(int i = 0;i<result.length;i++) result[i] = round((prevLayer[i+1]-prevLayer[i])/(x[i+1]-x[i]));
        return result;
    }
    static double[] calculateNewtonXs(double[] x, int power, double value)
    {
        double[] result = new double[power];
        for(int i = 0;i<power;i++)
        {
            result[i] = value - x[i];
            if(i != 0) result[i] *= result[i-1];
            result[i] = round(result[i]);
        }
        return result;
    }
    static double newtonSubstitute(double[] xs, double[] ys)
    {
        double result = ys[0];
        for(int i = 1;i<ys.length;i++)
        {
            result += ys[i]*xs[i-1];
        }
        return round(result);
    }
    static int findStartIndex(double value, double[] values)
    {
        if(value < values[2]) return 0;
        if(value >= values[values.length-3]) return values.length-4;
        int betweenIndex = 0;
        for(int i = 2;i<values.length-2;i++)
        {
            if(values[i] < value && values[i+1] >= value)
            {
                betweenIndex = i;
                break;
            }
        }
        return betweenIndex -2;
    }
    static String insertValue(double value)
    {
        return "(x - " + value + ")";
    }
    static String[] createNewtonStrings(double[] xs, int size)
    {
        StringBuilder[] result = new StringBuilder[size];
        for(int i = 0;i<result.length;i++) result[i] = new StringBuilder();
        String[] stringResult = new String[size];
        for(int i = 0;i<size;i++) {
            for (int j = 0; j <= i; j++) {
                result[i].append(insertValue(xs[j]));
            }
        }
        for(int i = 0;i<size;i++) stringResult[i] = result[i].toString();
        return stringResult;
    }
    static double findNewton(double[][] layers, double value, double[] xs)
    {
        int index = findStartIndex(value, xs);
        double[] ys = new double[layers.length];
        for(int i = 0;i<layers.length;i++) ys[i] = layers[i][index];
        String[] xParts = createNewtonStrings(xs, 4);
        System.out.println("Полученный многочлен Ньютона:");
        for(int i = 0;i<ys.length;i++){
            System.out.print(ys[i]);
            if(i > 0) System.out.print(xParts[i-1]);
            if(i != ys.length-1) System.out.print(" + ");
        }
        System.out.println();
        double[] xxs = calculateNewtonXs(xs, 5, value);
        double result = newtonSubstitute(xxs, ys);
        return result;
    }

    static double lagrangePart(int part, double[] xs, double y, double x)
    {
        int first = 0;
        int second = 2;
        if(part == 0) first = 1;
        if(part==2) second = 1;
        double firstX = xs[part];
        double secondX = xs[first];
        double thirdX = xs[second];
        double l = round((firstX - thirdX)*(firstX - secondX));
        StringBuilder toAdd = new StringBuilder();
        toAdd.append("(((x - ").append(secondX).append(")(x - ").append(thirdX).append("))/").append(l).append(") * (").append(y).append(")");
        lagrangeString.add(toAdd.toString());
        return y*(x-secondX)*(x-thirdX)/l;
    }
    static double lagrange(double[] xs, double[] ys, double x)
    {
        double result = 0.0;
        for(int i = 0;i<3;i++) result += lagrangePart(i, xs, ys[i], x);
        return round(result);
    }
    static int[] findPoints(double point, double[] values)
    {
        if(point <= values[0]) return new int[]{0,1,2};
        if(point > values[values.length-1]) return new int[]{values.length-3,values.length-2, values.length-1};
        int first = 0;
        int second = 1;
        for(int i =0;i<values.length-1;i++)
        {
            if(point > values[i] && point <= values[i+1])
            {
                first = i;
                second = i+1;
                break;
            }
        }
        return new int[]{first, second, first == 0 ? 2 : first - 1};
    }
    static double[][] createXsAndYs(double[] xs, double[] ys, int[] indices)
    {
        double[] xsr = new double[indices.length];
        double[] ysr = new double[indices.length];
        for(int i = 0;i<indices.length;i++)
        {
            xsr[i] = xs[indices[i]];
            ysr[i] = ys[indices[i]];
        }
        return new double[][]{xsr,ysr};
    }
    static double findInPoint(double x, double[] xs, double[] ys)
    {
        double[][] xsAndYs = createXsAndYs(xs, ys, findPoints(x, xs));
        return lagrange(xsAndYs[0],xsAndYs[1], x);
    }
    static double[] toArray(ArrayList<Double> al)
    {
        double[] result = new double[al.size()];
        for(int i = 0;i<result.length;i++) result[i] = al.get(i);
        return result;
    }
    static double calculate_fs(double[] x, int k){
        double result = 0.0;
        System.out.print("(");
        for(int i = 0;i<k;i++){
            double fxi = function(x[i]);
            double mult = 1.0;
            for(int j =0;j<k;j++){
                if(j == i) continue;
                mult *= x[i] - x[j];
            }
            double val = round(fxi / mult);
            System.out.print(val +( k == 1 || i == k-1 ? "" : " + "));
            result += val;
        }
        System.out.print(")");
        if(k != 1) System.out.print(" * ");
        //System.out.println("Value from fs: " + result);
        return result;
    }
    static double calculate_multiply(int k, double x, double[] xs){
        double result = 1.0;
        for(int i =0;i<k-1;i++) {
            result*= x - xs[i];
            System.out.print("(x - " + xs[i] + ")");
        }
        //System.out.println("Multiplication value: " + result);
        return result;
    }
    static double calculate_sum(double[] xs, int n, double x){
        double sum = 0.0;
        for(int k = 1;k<=n;k++){
            double val = round(calculate_fs(xs, k) * calculate_multiply(k,x, xs));
            System.out.print(k == n ? "" : " + ");
            sum += val;
            //System.out.println("Sum now is: " + sum);
        }
        return round(sum);
    }
    static int find_right_points(double[] points, double point){
        if(point < points[2]) return 0;
        if(point > points[points.length-3]) return points.length-4;
        for(int i = 2;i<points.length-3;i++){
            if(points[i] >= point) return i-2;
        }
        return 0;
    }
    public static void main(String[] args)
    {
        int maxStringLength = 0;
        ArrayList<Double> xValues = new ArrayList<>();
        ArrayList<Double> yValues = new ArrayList<>();
        for(double i = startPoint;i<=endPoint;i+=step)
        {
            double x = round(i);
            int xLength = Double.toString(x).length();
            if(xLength > maxStringLength) maxStringLength = xLength;
            xValues.add(x);
            double y = function(x);
            int yLength = Double.toString(y).length();
            if(yLength > maxStringLength) maxStringLength = yLength;
            yValues.add(y);
        }
        StringBuilder iLine = new StringBuilder("i ");
        StringBuilder xLine = new StringBuilder("x ");
        StringBuilder yLine = new StringBuilder("y ");
        StringBuilder divLine = new StringBuilder("--");
        for(int i = 0;i<xValues.size()*(maxStringLength+3);i++) divLine.append("-");
        for(int i = 0;i<xValues.size();i++)
        {
            iLine.append(" | ").append(addUntilSize(Integer.toString(i),maxStringLength));
            xLine.append(" | ").append(addUntilSize(Double.toString(xValues.get(i)), maxStringLength));
            yLine.append(" | ").append(addUntilSize(Double.toString(yValues.get(i)),maxStringLength));
        }
        double[] xs = toArray(xValues);
        double[] ys = toArray(yValues);
        System.out.println(iLine);
        System.out.println(divLine);
        System.out.println(xLine);
        System.out.println(divLine);
        System.out.println(yLine);
        System.out.println("Значения с помощью многочлена Лагранжа:");
        for(int i = 0;i<points.length;i++){
            double value = findInPoint(points[i], xs, ys);
            System.out.println("Полином Лагранжа: ");
            for(int j = 0;j<lagrangeString.size()-1;j++) System.out.print(lagrangeString.get(j) + " + ");
            System.out.println(lagrangeString.get(lagrangeString.size()-1));
            System.out.println("Значение в точке " + points[i] + " равно " + value);
            lagrangeString.clear();
        }
        //double[][] layers = calculateYsNewton(xs, ys);
        System.out.println();
        for(int i = 0;i<points.length;i++)
        {
            //double result = findNewton(layers, points[i], xs);
            double result = calculate_sum(xs, 3, points[i]);
            System.out.println("\nЗначение в точке " + points[i] + " равно "+ result);
        }
    }
}
