import java.util.Map;
import java.util.HashMap;

/**
 * Representa um nó na Árvore de Decisão ID3.
 */
public class ID3Node {
    public String attributeName; // Nome do atributo de divisão (ex: "Fome")
    public String label;         // Rótulo de decisão se for uma folha (ex: "Cozinhar")
    
    // Mapeia valor do atributo (ex: "Muita") para o nó filho correspondente.
    public Map<String, ID3Node> children = new HashMap<>(); 

    /**
     * Construtor para um Nó Folha (Terminal).
     */
    public ID3Node(String label) { 
        this.label = label;
    }
    
    /**
     * Construtor para um Nó Interno (de Decisão).
     */
    public ID3Node(String attributeName) { 
        this.attributeName = attributeName;
    }
}
