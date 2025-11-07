import java.util.HashMap;
import java.util.Map;

/**
 * Representa um nó na Árvore de Decisão ID3.
 * O nó pode ser um Nó de Teste (interno) ou um Nó de Decisão/Folha (final).
 */
public class TreeNode {
    // Para nós de teste, armazena o nome do atributo (ex: "Assunto").
    // Para nós folha, armazena a decisão final (ex: "Spam").
    public String attribute;
    
    // Indica se é um nó folha (Decisão) ou um nó interno (Teste).
    public boolean isLeaf;
    
    // Mapeia o valor do atributo (ex: "Com Oferta") para o próximo TreeNode.
    public Map<String, TreeNode> children;

    /**
     * Construtor para um novo nó.
     */
    public TreeNode(String attribute, boolean isLeaf) {
        this.attribute = attribute;
        this.isLeaf = isLeaf;
        
        // Inicializa o mapa de filhos apenas se for um nó de teste (não-folha).
        if (!isLeaf) {
            this.children = new HashMap<>();
        }
    }

    /**
     * Adiciona um filho ao nó atual.
     */
    public void addChild(String value, TreeNode child) {
        if (!isLeaf && children != null) {
            this.children.put(value, child);
        }
    }
}