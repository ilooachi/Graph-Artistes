import java.io.*;
import java.util.*;

public class Parser {
    
    public static List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++; // sauter le guillemet échappé
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields;
    }
    
    private static String cleanField(String field) {
        return field.trim();
    }
    
    public static Map<Integer, Artist> parseArtists(String filename) {
        Map<Integer, Artist> artistsById = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> parts = parseCSVLine(line);
                if (parts.size() < 3) continue;
                int id = Integer.parseInt(cleanField(parts.get(0)));
                String name = cleanField(parts.get(1));
                String description = cleanField(parts.get(2));
                Artist artist = new Artist(id, name, description);
                artistsById.put(id, artist);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return artistsById;
    }
    
    public static List<Edge> parseMentions(String filename, Map<Integer, Artist> artistsById) {
        List<Edge> edges = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> parts = parseCSVLine(line);
                if (parts.size() < 3) continue;
                int sourceId = Integer.parseInt(cleanField(parts.get(0)));
                int destId = Integer.parseInt(cleanField(parts.get(1)));
                int mentions = Integer.parseInt(cleanField(parts.get(2)));
                Artist source = artistsById.get(sourceId);
                Artist dest = artistsById.get(destId);
                if (source == null || dest == null) continue;
                double weight = 1.0 / mentions;
                // Ajout de toutes les arêtes, sans filtrage
                edges.add(new Edge(source, dest, weight));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return edges;
    }
}