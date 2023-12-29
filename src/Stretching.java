import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Stretching {
    public static void main(String[] args)
    {
        int zstDelay = 0;

        List<int[]> sinks = new ArrayList<>();
        sinks.add(new int[]{8, 12});//s1 - 0
        sinks.add(new int[]{5, 12});//s2 - 1
        sinks.add(new int[]{5, 8});//s3 - 2
        sinks.add(new int[]{0, 12});//s4 - 3
        sinks.add(new int[]{7, 8});//s5 - 4
        sinks.add(new int[]{0, 8});//s6 - 5
        sinks.add(new int[]{7, 2});//s7 - 6
        sinks.add(new int[]{8, 8});//s8 - 7
        sinks.add(new int[]{17, 12});//s9 - 8
        List<int[]> sinksCopy = new ArrayList<>();
        sinksCopy.add(new int[]{8, 12});//s1 - 0
        sinksCopy.add(new int[]{5, 12});//s2 - 1
        sinksCopy.add(new int[]{5, 8});//s3 - 2
        sinksCopy.add(new int[]{0, 12});//s4 - 3
        sinksCopy.add(new int[]{7, 8});//s5 - 4
        sinksCopy.add(new int[]{0, 8});//s6 - 5
        sinksCopy.add(new int[]{7, 2});//s7 - 6
        sinksCopy.add(new int[]{8, 8});//s8 - 7
        sinksCopy.add(new int[]{17, 12});//s9 - 8
        int sinkCounter = 9;
        List<List<Integer>> edges = new ArrayList<>();
        for(int i = 0; i <= 8; i++)
            edges.add(new ArrayList<>());
        edges.get(1).add(0);
        edges.get(1).add(2);
        edges.get(1).add(3);
        edges.get(0).add(8);
        edges.get(0).add(7);
        edges.get(2).add(5);
        edges.get(2).add(4);
        edges.get(4).add(6);

        List<Integer> hValue = new ArrayList<>();
        hValue.add(9);
        hValue.add(12);
        hValue.add(8);
        hValue.add(0);
        hValue.add(6);
        hValue.add(0);
        hValue.add(0);
        hValue.add(0);
        hValue.add(0);
        for(int i = 0; i < sinks.size(); i++)
        {
            int j = i;
            Collections.sort(edges.get(i), (x, y) -> {
                int distanceAndDelayOne = Math.abs(sinksCopy.get(j)[0] - sinksCopy.get(x)[0]) + 
                    Math.abs(sinksCopy.get(j)[1] - sinksCopy.get(x)[1]) + hValue.get(x);
    
                int distanceAndDelayTwo = Math.abs(sinksCopy.get(j)[0] - sinksCopy.get(y)[0]) + 
                    Math.abs(sinksCopy.get(j)[1] - sinksCopy.get(y)[1]) + hValue.get(y);
                if(distanceAndDelayOne == distanceAndDelayTwo)
                    return 0;
                return distanceAndDelayOne < distanceAndDelayTwo ? -1 : 1;
            });
            int k = edges.get(i).size();
            if(k > 0)
            {
                zstDelay += hValue.get(i);
                edges.get(i).add(sinkCounter);
                edges.add(new ArrayList<>());
                edges.get(sinkCounter++).add(edges.get(i).get(0));
                hValue.add(hValue.get(i));
                sinksCopy.add(new int[]{sinks.get(i)[0], sinks.get(i)[1]});
                for(int u = 1; u < k; u++)
                {
                    edges.add(new ArrayList<>());
                    edges.get(sinkCounter - 1).add(sinkCounter);
                    edges.get(sinkCounter++).add(edges.get(i).get(u));
                    hValue.add(hValue.get(i));
                    sinksCopy.add(new int[]{sinks.get(i)[0], sinks.get(i)[1]});
                }
                edges.get(i).subList(0, k).clear();
                int index = -1;
                outer:
                for(int u = 0; u < edges.size(); u++)
                {
                    for(int v = 0; v < edges.get(u).size(); v++)
                    {
                        if(edges.get(u).get(v) == i)
                        {
                            index = v;
                            edges.get(u).set(index, sinkCounter - 1);
                            break outer;
                        }
                    }
                }
                // int index = edges.stream()
                //     .flatMap(List::stream)
                //     .collect(Collectors.toList())
                //     .indexOf(i);
                //     edges.get(index / edges.size()).set(index % edges.size(), sinkCounter - 1);

            }
        }
    
        try
        {
            File outputFile = new File("/Users/architpande/ZST/src/zero_skew_tree.txt");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            for(int i = 0; i < edges.size(); i++)
            {
                for(int edge : edges.get(i))
                {
                    String e = "s" + String.valueOf(i + 1) 
                        + " --> s" + String.valueOf(edge + 1) + "    Length of Edge: " 
                        + String.valueOf(calculateManhattanDistance(i, edge, sinksCopy)) + "\n";
                    byte[] eToBytes = e.getBytes();
                    outputStream.write(eToBytes);
                }
            }
            outputStream.close();

        } catch (IOException e) {
            System.out.println("Error writing to file: " + e);
        }
    }

    private static int calculateManhattanDistance(int i, int edge, List<int[]> sinksCopy) {
        int distance = Math.abs(sinksCopy.get(i)[0] - sinksCopy.get(edge)[0]) + 
                    Math.abs(sinksCopy.get(i)[1] - sinksCopy.get(edge)[1]);
        return distance;
            
    }
}
