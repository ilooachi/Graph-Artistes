import java.util.ArrayList;
import java.util.List;

public class Artist {
    private String nom;
    private String categorie;
    private List<Edge> connections;

    public Artist(String nom, String categorie) {
        this.nom = nom;
        this.categorie = categorie;
        this.connections = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public List<Edge> getConnections() {
        return connections;
    }

    public void addConnection(Edge e) {
        connections.add(e);
    }

    // Setters si nécessaire
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setConnections(List<Edge> connections) {
        this.connections = connections;
    }
}