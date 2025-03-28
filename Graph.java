import java.util.*;
import java.io.*;

public class Graph {
    // Map pour retrouver un artiste à partir de son nom (pour le Main)
    private Map<String, Artist> artistsByName;
    // Liste d'adjacence : pour chaque artiste, la liste des arêtes sortantes (toutes les arêtes sont conservées)
    private Map<Artist, List<Edge>> adjacencyList;
    
    public Graph(String artistsFile, String mentionsFile) {
        // Parse des artistes
        Map<Integer, Artist> artistsById = Parser.parseArtists(artistsFile);
        artistsByName = new HashMap<>();
        for (Artist artist : artistsById.values()) {
            artistsByName.put(artist.getName(), artist);
        }
        // Construction de la liste d'adjacence sans filtrer les arêtes
        adjacencyList = new HashMap<>();
        List<Edge> edges = Parser.parseMentions(mentionsFile, artistsById);
        for (Edge edge : edges) {
            Artist source = edge.getSource();
            adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(edge);
        }
    }
    
    /**
     * Recherche du chemin le plus court (en nombre d'arcs) via un BFS classique.
     * On enregistre simplement le parent (la première découverte) pour chaque sommet.
     * Ensuite, lors de la reconstruction, pour chaque segment (parent -> enfant), on choisit
     * l'arête ayant le coût minimal parmi toutes les arêtes reliant ces deux sommets.
     */
    public void trouverCheminLePlusCourt(String startName, String endName) {
        Artist start = artistsByName.get(startName);
        Artist end = artistsByName.get(endName);
        if (start == null || end == null) {
            throw new RuntimeException("Artiste non trouvé.");
        }
        
        // BFS standard pour construire le chemin (parent map)
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
        
        // Reconstruction du chemin en remontant via la map parent
        List<Artist> path = new ArrayList<>();
        for (Artist cur = end; cur != null; cur = parent.get(cur)) {
            path.add(0, cur);
        }
        
        // Calcul du coût total : pour chaque segment, on prend l'arête de coût minimal parmi toutes celles reliant les deux artistes
        double totalCost = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Artist from = path.get(i);
            Artist to = path.get(i + 1);
            totalCost += getEdgeWeight(from, to);
        }
        
        printPath(path, totalCost);
    }
    
    /**
     * Renvoie le poids minimal parmi toutes les arêtes allant de "from" vers "to".
     */
    private double getEdgeWeight(Artist from, Artist to) {
        List<Edge> edges = adjacencyList.get(from);
        if (edges != null) {
            double best = Double.POSITIVE_INFINITY;
            for (Edge edge : edges) {
                if (edge.getDestination().equals(to)) {
                    if (edge.getWeight() < best) {
                        best = edge.getWeight();
                    }
                }
            }
            if (best < Double.POSITIVE_INFINITY) {
                return best;
            }
        }
        throw new RuntimeException("Aucune arête entre " + from.getName() + " et " + to.getName());
    }
    
    /**
     * Recherche du chemin "le plus fortement connecté" via Dijkstra.
     */
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