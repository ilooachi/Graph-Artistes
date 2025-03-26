// Graph.java
import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    // Attribut pour stocker les artistes
    private Map<String, Artist> artistes;

    public Graph(String fileArtists, String fileMentions) {
        // Lire le fichier artists.txt et créer les instances Artist
        artistes = Parser.parseArtists(fileArtists);

        // Lire le fichier mentions.txt et créer les connexions entre artistes
        List<Edge> edges = Parser.parseMentions(fileMentions, artistes);

        // Ajouter les connexions aux artistes
        for (Edge edge : edges) {
            edge.getSource().addConnection(edge);
            edge.getDestination().addConnection(edge); // Si le graphe est non orienté
        }
    }

    public void trouverCheminLePlusCourt(String depart, String arrivee) {
        // Vérifier que les deux artistes existent (si l'un est manquant, on lève une exception)
        if (!artistes.containsKey(depart) || !artistes.containsKey(arrivee)) {
            throw new IllegalArgumentException("Artiste non trouvé");
        }
        
        // Récupérer les instances de départ et d'arrivée depuis la map
        Artist artisteDepart = artistes.get(depart);
        Artist artisteArrivee = artistes.get(arrivee);
        
        // Initialisation des structures pour le BFS
        Queue<Artist> file = new LinkedList<>();
        Map<Artist, Artist> precedent = new HashMap<>();
        Set<Artist> visites = new HashSet<>();
        
        // Démarrage du parcours à partir de l'artiste de départ
        file.add(artisteDepart);
        visites.add(artisteDepart);
        
        // Variable pour stocker l'artiste d'arrivée dès qu'il est trouvé
        Artist trouve = null;
        
        // Parcours en largeur (BFS)
        while (!file.isEmpty()) {
            Artist artisteCourant = file.poll();
            
            // Si on atteint l'artiste d'arrivée, on peut arrêter la recherche
            if (artisteCourant.equals(artisteArrivee)) {
                trouve = artisteCourant;
                break;
            }
            
            // Pour chaque connexion (Edge) de l'artiste courant
            for (Edge edge : artisteCourant.getConnections()) {
                // Ici, on considère que le graphe est non orienté et que getDestination() renvoie le voisin
                Artist voisin = edge.getDestination();
                
                // S'assurer de ne pas revisiter un artiste déjà exploré
                if (!visites.contains(voisin)) {
                    visites.add(voisin);
                    precedent.put(voisin, artisteCourant);  // On enregistre le prédécesseur
                    file.add(voisin);
                }
            }
        }
        
        // Si aucun chemin n'a été trouvé, lever une exception
        if (trouve == null) {
            throw new IllegalArgumentException("Aucun chemin trouvé");
        }
        
        // Reconstruction du chemin en partant de l'artiste d'arrivée vers le départ
        List<Artist> chemin = new ArrayList<>();
        for (Artist artiste = trouve; artiste != null; artiste = precedent.get(artiste)) {
            chemin.add(artiste);
        }
        Collections.reverse(chemin);
        
        // Affichage du résultat
        System.out.println("Chemin: " + chemin.stream()
                                              .map(Artist::getNom)
                                              .collect(Collectors.joining(" -> ")));
        System.out.println("Longueur: " + (chemin.size() - 1));
        // Ici, le coût total est égal à la longueur si chaque connexion a un coût unitaire
        System.out.println("Coût total: " + (chemin.size() - 1));
    }

    public void trouverCheminMaxMentions(String depart, String arrivee) {
        // Utiliser par exemple l'algorithme de Dijkstra pour le chemin à coût minimum.
        // Afficher le chemin, la longueur et le coût total
        // Si aucun chemin, lancer une exception.
    }
}