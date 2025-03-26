// Artist.java
import java.util.ArrayList;
import java.util.List;

public class Artist {
    private String nom;
    private List<Edge> connections;

    public Artist(String nom) {
        this.nom = nom;
        this.connections = new ArrayList<>();
    }

    public void addConnection(Edge e) {
        connections.add(e);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Edge> getConnections() {
        return connections;
    }

    public void setConnections(List<Edge> connections) {
        this.connections = connections;
    }    
}