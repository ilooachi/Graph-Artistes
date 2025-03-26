public class Edge {
    private Artist source;
    private Artist destination;
    private double poids;
    private int mentions;

    public Edge(Artist source, Artist destination, int nombreMentions) {
        this.source = source;
        this.destination = destination;
        this.mentions = nombreMentions;
        // Le poids est l'inverse du nombre de mentions
        this.poids = 1.0 / nombreMentions;
    }

    public int getMentions() {
        return mentions;
    }

    public Artist getSource() {
        return source;
    }

    public Artist getDestination() {
        return destination;
    }

    public double getPoids() {
        return poids;
    }

    // Retourne l'autre extrémité de l'edge
    public Artist getOtherEnd(Artist artist) {
        return artist.equals(source) ? destination : source;
    }
}