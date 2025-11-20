import java.util.*;
import java.util.stream.Collectors;

/**
 * Projeto 6: Decisor de Escolha de Refeição.
 * Implementa o algoritmo ID3 principal para construir e usar a árvore de decisão.
 * * Depende das classes DataPoint, ID3Node e ID3Metrics.
 */
public class MealDecisor {

    private static final String[] ATTRIBUTE_NAMES = {"Fome", "Tempo", "Ingredientes", "Orçamento"};

    // --- Algoritmo ID3 e Construção da Árvore ---

    /**
     * Encontra o atributo com o maior Ganho de Informação dentre os disponíveis.
     */
    private static int findBestSplitAttribute(List<DataPoint> data, Set<Integer> availableAttributes) {
        double maxGain = -1.0;
        int bestAttrIndex = -1;

        for (int index : availableAttributes) {
            double gain = ID3Metrics.calculateGain(data, index);
            if (gain > maxGain) {
                maxGain = gain;
                bestAttrIndex = index;
            }
        }
        return bestAttrIndex;
    }

    /**
     * O núcleo recursivo do ID3: Constrói a Árvore de Decisão.
     */
    public static ID3Node buildTree(List<DataPoint> data, Set<Integer> availableAttributes) {
        // 1. Caso Base: Se todos os rótulos forem iguais, retorna um nó folha.
        Map<String, Long> counts = ID3Metrics.countLabels(data);
        if (counts.size() <= 1) { // Inclui caso de 0 rótulos (lista vazia)
            if (counts.isEmpty()) return new ID3Node("Decisão Desconhecida");
            return new ID3Node(counts.keySet().iterator().next());
        }

        // 2. Caso Base: Se não houver mais atributos ou o ganho for zero, retorna a classe majoritária.
        if (availableAttributes.isEmpty()) {
            String majorityLabel = counts.entrySet().stream()
                                         .max(Map.Entry.comparingByValue())
                                         .map(Map.Entry::getKey)
                                         .orElse("Decisão Desconhecida");
            return new ID3Node(majorityLabel);
        }

        // 3. Passo Recursivo: Encontra o melhor atributo.
        int bestAttrIndex = findBestSplitAttribute(data, availableAttributes);
        
        // Se o Ganho for zero ou negativo (não há mais redução de incerteza), retorna a classe majoritária.
        if (ID3Metrics.calculateGain(data, bestAttrIndex) <= 0.0) {
             String majorityLabel = counts.entrySet().stream()
                                         .max(Map.Entry.comparingByValue())
                                         .map(Map.Entry::getKey)
                                         .orElse("Decisão Desconhecida");
            return new ID3Node(majorityLabel);
        }
        
        String bestAttrName = ATTRIBUTE_NAMES[bestAttrIndex];
        ID3Node root = new ID3Node(bestAttrName); 

        // Remove o atributo escolhido para a próxima recursão.
        Set<Integer> remainingAttributes = new HashSet<>(availableAttributes);
        remainingAttributes.remove(bestAttrIndex);

        // Agrupa os dados pelo valor do atributo escolhido para criar as partições.
        Map<String, List<DataPoint>> partitions = data.stream()
                .collect(Collectors.groupingBy(p -> p.getFeature(bestAttrIndex)));

        // 4. Cria a sub-árvore para cada valor (partição).
        for (Map.Entry<String, List<DataPoint>> entry : partitions.entrySet()) {
            String value = entry.getKey(); 
            List<DataPoint> subset = entry.getValue();

            // Chamada recursiva para construir o nó filho.
            root.children.put(value, buildTree(subset, remainingAttributes));
        }

        return root;
    }


    // --- Classificação (Decisão) ---

    /**
     * Atravessa a árvore de decisão para classificar uma nova instância de solicitação.
     * * CORREÇÃO: Esta função estava incompleta na resposta anterior.
     */
    public static String classify(ID3Node tree, DataPoint newMealRequest) {
        // Se for um nó folha, retorna a decisão final.
        if (tree.label != null) {
            return tree.label;
        }

        // Encontra o índice do atributo de decisão no nó atual.
        int attrIndex = -1;
        for(int i = 0; i < ATTRIBUTE_NAMES.length; i++) {
            if (ATTRIBUTE_NAMES[i].equals(tree.attributeName)) {
                attrIndex = i;
                break;
            }
        }

        if (attrIndex == -1) return "Erro de Atributo: Nome não encontrado.";

        // Obtém o valor do atributo da nova instância (ex: "Muita" para Fome).
        String value = newMealRequest.getFeature(attrIndex);

        // Navega para o nó filho correspondente a esse valor.
        if (tree.children.containsKey(value)) {
            // Chamada recursiva para o próximo nível.
            return classify(tree.children.get(value), newMealRequest);
        } else {
            // Se o valor não foi visto no treinamento, retorna indefinido.
            return "Decisão Indefinida (Valor '" + value + "' não encontrado no treinamento)";
        }
    }


    // --- Método Principal e Testes ---

    public static void main(String[] args) {
        // Dataset de Treinamento (10 pontos: 6 "Pedir Delivery" / 4 "Cozinhar")
        List<DataPoint> dataset = Arrays.asList(
            new DataPoint(Arrays.asList("Muita", "Curto", "Poucos", "Médio"), "Pedir Delivery"),
            new DataPoint(Arrays.asList("Moderada", "Suficiente", "Muitos", "Alto"), "Cozinhar"),
            new DataPoint(Arrays.asList("Pouca", "Suficiente", "Poucos", "Baixo"), "Cozinhar"),
            new DataPoint(Arrays.asList("Muita", "Curto", "Nenhum", "Alto"), "Pedir Delivery"),
            new DataPoint(Arrays.asList("Moderada", "Curto", "Muitos", "Médio"), "Pedir Delivery"),
            new DataPoint(Arrays.asList("Pouca", "Suficiente", "Muitos", "Baixo"), "Cozinhar"),
            new DataPoint(Arrays.asList("Muita", "Suficiente", "Poucos", "Alto"), "Pedir Delivery"),
            new DataPoint(Arrays.asList("Moderada", "Curto", "Nenhum", "Médio"), "Pedir Delivery"),
            new DataPoint(Arrays.asList("Muita", "Curto", "Poucos", "Baixo"), "Pedir Delivery"),
            new DataPoint(Arrays.asList("Moderada", "Suficiente", "Nenhum", "Alto"), "Cozinhar")
        );

        Set<Integer> allIndices = new HashSet<>(Arrays.asList(0, 1, 2, 3));

        System.out.println("--- Análise de Ganho de Informação ---");

        // 1. Calcula e exibe o Ganho de Informação.
        String bestAttr = "";
        double maxGain = -1.0;
        for (int i = 0; i < ATTRIBUTE_NAMES.length; i++) {
            double gain = ID3Metrics.calculateGain(dataset, i);
            System.out.printf("Ganho(%s): %.4f%n", ATTRIBUTE_NAMES[i], gain);
            if (gain > maxGain) {
                maxGain = gain;
                bestAttr = ATTRIBUTE_NAMES[i];
            }
        }
        System.out.println("------------------------------------");
        System.out.println("Nó Raiz Escolhido (Maior Ganho): **" + bestAttr + "**");

        // 2. Constrói a Árvore de Decisão ID3.
        ID3Node decisionTree = buildTree(dataset, allIndices);
        // 

        System.out.println("\n--- Testes de Classificação ---");

        // Teste 1: Muita Fome, Tempo Curto, Poucos Ingredientes -> Esperado: Pedir Delivery
        DataPoint test1 = new DataPoint(Arrays.asList("Muita", "Curto", "Poucos", "Médio"), null);
        String result1 = classify(decisionTree, test1);
        System.out.println("Pedido 1 (Muita Fome, Curto): -> " + result1);
        
        // Teste 2: Pouca Fome, Tempo Suficiente, Muitos Ingredientes -> Esperado: Cozinhar
        DataPoint test2 = new DataPoint(Arrays.asList("Pouca", "Suficiente", "Muitos", "Alto"), null);
        String result2 = classify(decisionTree, test2);
        System.out.println("Pedido 2 (Pouca Fome, Suficiente): -> " + result2);

        // Teste 3: Moderada Fome, Tempo Curto, Nenhum Ingrediente -> Esperado: Pedir Delivery
        DataPoint test3 = new DataPoint(Arrays.asList("Moderada", "Curto", "Nenhum", "Médio"), null);
        String result3 = classify(decisionTree, test3);
        System.out.println("Pedido 3 (Moderada Fome, Curto): -> " + result3);
    }
}