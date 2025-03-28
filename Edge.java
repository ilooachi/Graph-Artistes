public class Edge {
    private Artist source;
    private Artist destination;
    private double weight;
    
    public Edge(Artist source, Artist destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
    
    public Artist getSource() {
        return source;
    }
    
    public Artist getDestination() {
        return destination;
    }
    
    public double getWeight() {
        return weight;
    }
}