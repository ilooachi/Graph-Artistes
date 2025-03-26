import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    // Méthode pour parser le fichier artists.txt
    public static Map<String, Artist> parseArtists(String filename) {
        Map<String, Artist> artistsMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 2) {  // On attend : ID, Nom, Catégorie
                    String artistId = parts[0].trim();
                    String artistName = parts[1].trim();
                    String categorie = parts[2].trim();
                    Artist artist = new Artist(artistName, categorie);
                    artistsMap.put(artistId, artist);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return artistsMap;
    }

    // Méthode pour parser le fichier mentions.txt
    public static List<Edge> parseMentions(String filename, Map<String, Artist> artistsMap) {
        List<Edge> edges = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String artist1Id = parts[0].trim();
                    String artist2Id = parts[1].trim();
                    int mentions = Integer.parseInt(parts[2].trim());

                    // Récupérer les artistes par leur ID
                    Artist artist1 = artistsMap.get(artist1Id);
                    Artist artist2 = artistsMap.get(artist2Id);

                    if (artist1 != null && artist2 != null) {
                        Edge edge = new Edge(artist1, artist2, mentions);
                        edges.add(edge);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return edges;
    }
}