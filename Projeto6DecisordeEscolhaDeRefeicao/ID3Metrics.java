import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe utilitária para calcular as métricas do algoritmo ID3,
 * especificamente Entropia e Ganho de Informação.
 */
public class ID3Metrics {

    /**
     * Implementa a função log base 2, essencial para o cálculo da Entropia.
     */
    private static double log2(double x) {
        if (x <= 0) return 0.0;
        return Math.log(x) / Math.log(2);
    }

    /**
     * Conta as ocorrências de cada rótulo de decisão no conjunto de dados.
     */
    public static Map<String, Long> countLabels(List<DataPoint> data) {
        return data.stream()
                   .filter(p -> p.getLabel() != null)
                   .collect(Collectors.groupingBy(DataPoint::getLabel, Collectors.counting()));
    }

    /**
     * Calcula a Entropia do conjunto de dados (S).
     */
    public static double calculateEntropy(List<DataPoint> data) {
        if (data.isEmpty()) return 0.0;
        
        Map<String, Long> counts = countLabels(data);
        long total = data.size();
        double entropy = 0.0;

        for (long count : counts.values()) {
            double probability = (double) count / total;
            entropy -= probability * log2(probability);
        }
        return entropy;
    }

    /**
     * Calcula o Ganho de Informação para um atributo dado um dataset.
     */
    public static double calculateGain(List<DataPoint> data, int attributeIndex) {
        double totalEntropy = calculateEntropy(data);
        
        // Agrupa os dados por valor do atributo (particiona o dataset).
        Map<String, List<DataPoint>> partitions = data.stream()
                .collect(Collectors.groupingBy(p -> p.getFeature(attributeIndex)));

        double weightedEntropy = 0.0;
        // Soma a Entropia de cada partição, ponderada pelo seu tamanho.
        for (List<DataPoint> partition : partitions.values()) {
            double weight = (double) partition.size() / data.size();
            weightedEntropy += weight * calculateEntropy(partition);
        }
        
        return totalEntropy - weightedEntropy;
    }
}