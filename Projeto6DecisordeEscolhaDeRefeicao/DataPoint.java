import java.util.List;

/**
 * Representa um único ponto de dados (instância) no dataset,
 * incluindo seus valores de atributos (features) e o rótulo de decisão (label).
 */
public class DataPoint {
    private final List<String> features;
    private final String label; // A decisão: "Pedir Delivery" ou "Cozinhar"

    /**
     * Construtor para criar um novo ponto de dados.
     */
    public DataPoint(List<String> features, String label) {
        this.features = features;
        this.label = label;
    }

    /**
     * Obtém o valor de um atributo específico pelo seu índice.
     */
    public String getFeature(int index) {
        return features.get(index);
    }

    /**
     * Obtém o rótulo de decisão.
     */
    public String getLabel() {
        return label;
    }
}
