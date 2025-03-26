import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    // Attribut pour stocker les artistes (clé = ID)
    private Map<String, Artist> artistes;

    public Graph(String fileArtists, String fileMentions) {
        // Charger les artistes depuis le fichier
        artistes = Parser.parseArtists(fileArtists);

        // Charger les connexions depuis le fichier mentions.txt
        List<Edge> edges = Parser.parseMentions(fileMentions, artistes);

        // Ajouter chaque edge aux deux artistes (graphe non orienté)
        for (Edge edge : edges) {
            edge.getSource().addConnection(edge);
            edge.getDestination().addConnection(edge);
        }
    }

    /**
     * Méthode utilisant BFS pour trouver le chemin le plus court (en nombre d'arêtes)
     * entre deux artistes. Le coût total est calculé en sommant le poids (1/mentions)
     * de chaque arête empruntée.
     */
    public void trouverCheminLePlusCourt(String depart, String arrivee) {
        // Recherche des artistes par leur nom
        Artist artisteDepart = null;
        Artist artisteArrivee = null;
        for (Artist artist : artistes.values()) {
            if (artist.getNom().equals(depart))
                artisteDepart = artist;
            if (artist.getNom().equals(arrivee))
                artisteArrivee = artist;
        }
        if (artisteDepart == null || artisteArrivee == null) {
            throw new RuntimeException("Artiste non trouvé");
        }

        // Initialisation des structures pour le BFS
        Queue<Artist> file = new LinkedList<>();
        Map<Artist, Artist> precedent = new HashMap<>();   // pour reconstituer le chemin
        Map<Artist, Edge> prevEdge = new HashMap<>();        // pour mémoriser l'edge utilisée
        Set<Artist> visites = new HashSet<>();

        file.add(artisteDepart);
        visites.add(artisteDepart);

        Artist trouve = null;
        // Parcours en largeur
        while (!file.isEmpty()) {
            Artist courant = file.poll();
            if (courant.equals(artisteArrivee)) {
                trouve = courant;
                break;
            }
            for (Edge edge : courant.getConnections()) {
                // getOtherEnd retourne l'autre artiste de l'edge
                Artist voisin = edge.getOtherEnd(courant);
                if (!visites.contains(voisin)) {
                    visites.add(voisin);
                    precedent.put(voisin, courant);
                    prevEdge.put(voisin, edge);  // mémoriser l'arête utilisée pour atteindre 'voisin'
                    file.add(voisin);
                }
            }
        }

        if (trouve == null) {
            throw new IllegalArgumentException("Aucun chemin trouvé");
        }

        // Reconstruction du chemin depuis l'artiste d'arrivée
        List<Artist> chemin = new ArrayList<>();
        for (Artist a = trouve; a != null; a = precedent.get(a)) {
            chemin.add(a);
        }
        Collections.reverse(chemin);

        // Calcul du coût total en sommant les poids des edges utilisées
        double coutTotal = 0;
        for (int i = 1; i < chemin.size(); i++) {
            Edge edgeUtilisee = prevEdge.get(chemin.get(i));
            coutTotal += edgeUtilisee.getPoids();
        }

        // Affichage du résultat
        System.out.println("Longueur du chemin : " + (chemin.size() - 1));
        System.out.println("Coût total du chemin : " + coutTotal);
        System.out.println("Chemin :");
        for (Artist artist : chemin) {
            System.out.println(artist.getNom() + " (" + artist.getCategorie() + ")");
        }
    }

    /**
     * Méthode utilisant l'algorithme de Dijkstra pour trouver le chemin à coût minimum,
     * c'est-à-dire le chemin qui maximise les mentions (puisque le poids = 1/mentions).
     */
    public void trouverCheminMaxMentions(String depart, String arrivee) {
        // Recherche des artistes par leur nom
        Artist artisteDepart = null;
        Artist artisteArrivee = null;
        for (Artist artist : artistes.values()) {
            if (artist.getNom().equals(depart))
                artisteDepart = artist;
            if (artist.getNom().equals(arrivee))
                artisteArrivee = artist;
        }
        if (artisteDepart == null || artisteArrivee == null) {
            throw new RuntimeException("Artiste non trouvé");
        }

        // Initialisation des distances : 0 pour le départ, +∞ pour les autres
        Map<Artist, Double> distances = new HashMap<>();
        for (Artist a : artistes.values()) {
            distances.put(a, Double.POSITIVE_INFINITY);
        }
        distances.put(artisteDepart, 0.0);

        // Map pour mémoriser le prédécesseur de chaque artiste (pour reconstituer le chemin)
        Map<Artist, Artist> precedent = new HashMap<>();

        // PriorityQueue pour traiter les artistes selon le coût cumulé
        PriorityQueue<Pair> queue = new PriorityQueue<>(Comparator.comparingDouble(Pair::getCost));
        queue.add(new Pair(artisteDepart, 0.0));

        while (!queue.isEmpty()) {
            Pair currentPair = queue.poll();
            Artist courant = currentPair.getArtist();
            double coutCourant = currentPair.getCost();

            if (courant.equals(artisteArrivee)) {
                break;
            }

            for (Edge edge : courant.getConnections()) {
                Artist voisin = edge.getOtherEnd(courant);
                double nouveauCost = coutCourant + edge.getPoids();
                if (nouveauCost < distances.get(voisin)) {
                    distances.put(voisin, nouveauCost);
                    precedent.put(voisin, courant);
                    queue.add(new Pair(voisin, nouveauCost));
                }
            }
        }

        if (distances.get(artisteArrivee) == Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("Aucun chemin trouvé");
        }

        // Reconstruction du chemin depuis l'artiste d'arrivée
        List<Artist> chemin = new ArrayList<>();
        for (Artist a = artisteArrivee; a != null; a = precedent.get(a)) {
            chemin.add(a);
        }
        Collections.reverse(chemin);

        // Affichage du résultat
        System.out.println("Longueur du chemin : " + (chemin.size() - 1));
        System.out.println("Coût total du chemin : " + distances.get(artisteArrivee));
        System.out.println("Chemin :");
        for (Artist artist : chemin) {
            System.out.println(artist.getNom() + " (" + artist.getCategorie() + ")");
        }
    }

    /**
     * Classe auxiliaire pour associer un artiste à son coût cumulé dans l'algorithme de Dijkstra.
     */
    private static class Pair {
        private Artist artist;
        private double cost;

        public Pair(Artist artist, double cost) {
            this.artist = artist;
            this.cost = cost;
        }

        public Artist getArtist() {
            return artist;
        }

        public double getCost() {
            return cost;
        }
    }
}