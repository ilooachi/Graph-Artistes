public class Main {
    public static void main(String[] args) {
        try {
            Graph graph = new Graph("artists.txt", "mentions.txt");

            // Test avec "The Beatles" -> "Kendji Girac"
            graph.trouverCheminLePlusCourt("The Beatles", "Kendji Girac");
            System.out.println("--------------------------");
            graph.trouverCheminMaxMentions("The Beatles", "Kendji Girac");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}