import java.util.*;
import java.io.*;

public class Graph {
    private Map<String, Artist> artistsByName;
    private Map<Artist, List<Edge>> adjacencyList;
    
    public Graph(String artistsFile, String mentionsFile) {
        // Parse des artistes
        Map<Integer, Artist> artistsById = Parser.parseArtists(artistsFile);
        artistsByName = new HashMap<>();
        for (Artist artist : artistsById.values()) {
            artistsByName.put(artist.getName(), artist);
        }
        
        // Construction de la liste d'adjacence en filtrant les doublons
        adjacencyList = new HashMap<>();
        List<Edge> edges = Parser.parseMentions(mentionsFile, artistsById);
        for (Edge edge : edges) {
            Artist source = edge.getSource();
            Artist dest = edge.getDestination();
            List<Edge> list = adjacencyList.computeIfAbsent(source, k -> new ArrayList<>());
            boolean found = false;
            for (int i = 0; i < list.size(); i++) {
                Edge existing = list.get(i);
                if (existing.getDestination().equals(dest)) {
                    // Remplacer si on trouve une arête avec un poids supérieur (moins forte)
                    if (edge.getWeight() < existing.getWeight()) {
                        list.set(i, edge);
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                list.add(edge);
            }
        }
    }
    
    public void trouverCheminLePlusCourt(String startName, String endName) {
        Artist start = artistsByName.get(startName);
        Artist end = artistsByName.get(endName);
        if (start == null || end == null) {
            throw new RuntimeException("Artiste non trouvé.");
        }
        
        // BFS standard en enregistrant le parent
        Map<Artist, Artist> parent = new HashMap<>();
        Queue<Artist> queue = new LinkedList<>();
        Set<Artist> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            Artist current = queue.poll();
            if (current.equals(end)) break;
            List<Edge> edges = adjacencyList.get(current);
            if (edges != null) {
                for (Edge edge : edges) {
                    Artist neighbor = edge.getDestination();
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parent.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }
        
        if (!visited.contains(end)) {
            throw new RuntimeException("Aucun chemin entre " + startName + " et " + endName);
        }
        
        // Reconstruction du chemin selon le parent découvert
        List<Artist> path = new ArrayList<>();
        for (Artist cur = end; cur != null; cur = parent.get(cur)) {
            path.add(0, cur);
        }
        
        // Calcul du coût total : pour chaque segment, utiliser l'unique arête filtrée
        double totalCost = 0.0;
        Artist current = end;
        while (!current.equals(start)) {
            Artist p = parent.get(current);
            double w = 0.0;
            for (Edge e : adjacencyList.get(p)) {
                if (e.getDestination().equals(current)) {
                    w = e.getWeight();
                    break;
                }
            }
            totalCost += w;
            current = p;
        }
        
        printPath(path, totalCost);
    }
    
    public void trouverCheminMaxMentions(String startName, String endName) {
        Artist start = artistsByName.get(startName);
        Artist end = artistsByName.get(endName);
        if (start == null || end == null) {
            throw new RuntimeException("Artiste non trouvé.");
        }
        
        Map<Artist, Double> dist = new HashMap<>();
        Map<Artist, Artist> prev = new HashMap<>();
        for (Artist a : artistsByName.values()) {
            dist.put(a, Double.POSITIVE_INFINITY);
        }
        dist.put(start, 0.0);
        
        PriorityQueue<Artist> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(start);
        
        while (!pq.isEmpty()) {
            Artist current = pq.poll();
            if (current.equals(end))
                break;
            List<Edge> edges = adjacencyList.get(current);
            if (edges != null) {
                for (Edge edge : edges) {
                    Artist neighbor = edge.getDestination();
                    double alt = dist.get(current) + edge.getWeight();
                    if (alt < dist.get(neighbor)) {
                        dist.put(neighbor, alt);
                        prev.put(neighbor, current);
                        pq.add(neighbor);
                    }
                }
            }
        }
        
        if (dist.get(end) == Double.POSITIVE_INFINITY) {
            throw new RuntimeException("Aucun chemin entre " + startName + " et " + endName);
        }
        
        List<Artist> path = new ArrayList<>();
        for (Artist at = end; at != null; at = prev.get(at)) {
            path.add(0, at);
        }
        
        double totalCost = dist.get(end);
        printPath(path, totalCost);
    }
    
    private void printPath(List<Artist> path, double totalCost) {
        System.out.println("Longueur du chemin : " + (path.size() - 1));
        System.out.println("Coût total du chemin : " + totalCost);
        System.out.println("Chemin :");
        for (Artist a : path) {
            System.out.println(a);
        }
    }
}