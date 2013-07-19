package ca.kijiji.contest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class TicketStatsCalculations
{
    static Set<String> uselessWords = new HashSet<String>();
    static
    {
        uselessWords.addAll(Arrays.asList(new String[] { "EAST", "WEST", "NORTH", "SOUTH", "WALK", "E", "W", "N", "S", "ST", "STR", "STREET", "TR", "AV", "AVE", "AVENUE", "CIR", "CIRCLE", "CT", "CRT", "COURT", "CR", "CRESCENT", "CRE", "RD", "ROAD", "DR", "DRIVE", "BLVD", "BOULEVARD", "BL", "BULVD", "LANE", "PL", "CRES", "TER", "TERR", "PARK", "PARKWAY", "WAY", "GDNS", "GARDEN", "GARDENS", "TRL", "TRAIL", "MALL" }));
    }
    
    public static void map(String line, LineMappingResult result)
    {
        if (line == null)
            return;
        String[] split = line.split(",");
        result.roadName = cleanRoadName(split[7]);
        result.amount = Integer.parseInt(split[4]);
    }
    
    private static String cleanRoadName(String roadName)
    {
        // Generally, addresses follow the format: NUMBER NAME SUFFIX DIRECTION
        // NUMBER (optional) = digits, ranges of digit (e.g. 1531-1535), letters, characters like ! or ? or % or /
        // NAME (required) = the name you need to extract, mostly uppercase letters, sometimes spaces (e.g. ST CLAIR), rarely numbers (e.g. 16TH)
        // SUFFIX (optional) = the type of street such as ST, STREET, AV, AVE, COURT, CRT, CT, RD ...
        // DIRECTION (optional) = one of EAST, WEST, E, W, N, S
        roadName = roadName.toUpperCase().trim();
        // if (roadName.contains("ST CLAIR"))
        // {
        // return roadName;
        // }
        String[] tokens = roadName.split(" ");
        // One word incomings
        if (tokens.length == 1)
        {
            return roadName;
        }
        // Multi word
        
        int start = 0;
        for (; start < 1; start++)
        {
            if (isAlpha(tokens[start]))
            {
                break;
            }
        }
        
        int end = tokens.length - 1;
        for (; end > start; end--)
        {
            if (!uselessWords.contains(tokens[end]))
            {
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        
        for (int i = start; i <= end; i++)
        {
            if (tokens[i].isEmpty())
            {
                continue;
            }
            sb.append(tokens[i]).append(" ");
        }
        String result = sb.toString().trim();
        if (result.isEmpty())
        {
            return tokens[1];
        }
        return result;
    }
    
    public static class LineMappingResult
    {
        public String roadName;
        public int amount;
    }
    
    public static void combine(String key, int value, Map<String, Integer> map)
    {
        if (map.containsKey(key))
        {
            value += map.get(key);
        }
        map.put(key, value);
    }
    
    public static void reduce(Map<String, Integer> input, Map<String, Integer> result)
    {
        for (Entry<String, Integer> entry : input.entrySet())
        {
            combine(entry.getKey(), entry.getValue(), result);
        }
    }
    
    public static SortedMap<String, Integer> sort(Map<String, Integer> input)
    {
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(new ValueComparator(input));
        result.putAll(input);
        return result;
    }
    
    public static int getPartition(int hashCode, int partitions)
    {
        return ((hashCode % partitions) + partitions) % partitions;
    }
    
    private static class ValueComparator implements Comparator<String>
    {
        Map<String, Integer> base;
        
        public ValueComparator(Map<String, Integer> base)
        {
            this.base = base;
        }
        
        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(String a, String b)
        {
            if (base.get(a) > base.get(b))
            {
                return -1;
            }
            else if (base.get(a) < base.get(b))
            {
                return 1;
            }
            else
            {
                // Returning 0 would merge keys
                return a.compareTo(b);
            }
        }
    }
    
    private static boolean isAlpha(String word)
    {
        if (word.isEmpty())
            return false;
        char[] chars = word.toCharArray();
        
        for (char c : chars)
        {
            if (!Character.isLetter(c))
            {
                return false;
            }
        }
        
        return true;
    }
}
