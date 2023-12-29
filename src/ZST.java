import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class ZST {
    public static void main(String[] args) throws Exception {
        
        
        if (args.length != 1)
        {
            System.out.println("Usage: Run with command line argument with file as an input for grid points");
            System.exit(1);
        }

        String inputPoints = args[0];

        List<int[]> sinks = getSinks(inputPoints);
        int numberOfSinks = sinks.size();
        
        List<List<Integer>> edges = new ArrayList<>();

        List<Integer> hValue = new ArrayList<>();
        sinks.forEach(s -> hValue.add(0));
        sinks.forEach(s -> edges.add(new ArrayList<>()));
        Set<Integer> indexOfRemovedPoints = new HashSet<>();

        while(numberOfSinks > 1)
        {
            int[] closest = findClosest(sinks, indexOfRemovedPoints);
            if(hValue.get(closest[0]) < hValue.get(closest[1]))
            {
                int temp = closest[0];
                closest[0] = closest[1];
                closest[1] = temp;
            }
            edges.get(closest[0]).add(closest[1]);
            hValue.set(closest[0], Math.max(hValue.get(closest[0]), calculateManhattanDistance(sinks, closest[0], closest[1]) + hValue.get(closest[1])));
            indexOfRemovedPoints.add(closest[1]);
            numberOfSinks--;
        }
        for(int i = 0; i < sinks.size(); i++)
        {
            if(!indexOfRemovedPoints.contains(i)){
                System.out.println("Delay: " + hValue.get(i));
                System.out.println("Root node: " + i);
            }
        }

        for(int i = 0; i < edges.size(); i++)
        {
            System.out.println("Root " + i);
            for(int j = 0; j < edges.get(i).size(); j++)
                System.out.println("          " + edges.get(i).get(j));
        }

        zeroSkewStretching(sinks, hValue, edges);

    }

    private static List<int[]> getSinks(String inputPoints) {
        List<int[]> sinks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputPoints)))
        {
            String point;
            while((point = br.readLine()) != null)
            {
                String[] coordinates = point.split("\\s+");
                if (coordinates.length == 2)
                {
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    sinks.add(new int[]{x, y});
                }
                else
                {
                    System.err.println("Error: Invalid input format in the file");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error: The file contains non integer values.");
            e.printStackTrace();
        }
        return sinks;
    }

    private static int calculateManhattanDistance(List<int[]> roots, int i, int j) {
        int distance = Math.abs(roots.get(i)[0] - roots.get(j)[0]) + Math.abs(roots.get(i)[1] - roots.get(j)[1]);
        return distance;
    }

    public static int[] findClosest(List<int[]> roots, Set<Integer> indexOfRemovedPoints)
    {
        int minDistance = Integer.MAX_VALUE;
        int[] closestPair = new int[2];
        for(int i = 0; i < roots.size() - 1; i++)
        {
            for(int j = i + 1; j < roots.size(); j++)
            {
                int distance = calculateManhattanDistance(roots, i, j);
                if(!indexOfRemovedPoints.contains(i) && !indexOfRemovedPoints.contains(j) && distance < minDistance)
                {
                    minDistance = distance;
                    closestPair[0] = i;
                    closestPair[1] = j;
                }
            }
        }
        return closestPair;
    }

    public static void zeroSkewStretching(List<int[]> sinks, List<Integer> hValue, List<List<Integer>> edges)
    {
        List<int[]> sinksCopy = new ArrayList<>(sinks);
        int sinkCounter = sinks.size();
        int sumOfSinkDelays = 0;
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
            if(k > 0) {
                sumOfSinkDelays += hValue.get(i);
                edges.get(i).add(sinkCounter);
                edges.add(new ArrayList<>());
                edges.get(sinkCounter++).add(edges.get(i).get(0));
                hValue.add(hValue.get(i));
                sinksCopy.add(new int[]{sinks.get(i)[0], sinks.get(i)[1]});
                for(int u = 1; u < k; u++) {
                    edges.add(new ArrayList<>());
                    edges.get(sinkCounter - 1).add(sinkCounter);
                    edges.get(sinkCounter++).add(edges.get(i).get(u));
                    hValue.add(hValue.get(i));
                    sinksCopy.add(new int[]{sinks.get(i)[0], sinks.get(i)[1]});
                }
                edges.get(i).subList(0, k).clear();
                int index = -1;
                outer:
                for(int u = 0; u < edges.size(); u++) {
                    for(int v = 0; v < edges.get(u).size(); v++) {
                        if(edges.get(u).get(v) == i)
                        {
                            index = v;
                            edges.get(u).set(index, sinkCounter - 1);
                            break outer;
                        }
                    }
                }

            }
        }
    
        try
        {
            File outputFile = new File("/Users/architpande/ZST/src/zero_skew_tree.txt");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            int lengthOfRootedSpanningTree = 0;
            for(int i = 0; i < edges.size(); i++)
            {
                for(int edge : edges.get(i))
                {
                    int lengthOfEdge = calculateManhattanDistance(sinksCopy, edge, i);
                    String e = "s" + String.valueOf(i + 1) 
                        + " --> s" + String.valueOf(edge + 1) + "    Length of Edge: " 
                        + String.valueOf(lengthOfEdge) + "\n";
                    byte[] eToBytes = e.getBytes();
                    outputStream.write(eToBytes);
                    lengthOfRootedSpanningTree += lengthOfEdge;
                }
            }
            String lengths = "Length of rooted spanning tree: " + String.valueOf(lengthOfRootedSpanningTree)
                    + "   Sum of sink delays: " + String.valueOf(sumOfSinkDelays) 
                    + "   Length of ZST: " + String.valueOf(lengthOfRootedSpanningTree + sumOfSinkDelays);
            byte[] lengthsToBytes = lengths.getBytes();
            outputStream.write(lengthsToBytes);
            outputStream.close();

        } catch (IOException e) {
            System.out.println("Error writing to file: " + e);
        }
    }
}
