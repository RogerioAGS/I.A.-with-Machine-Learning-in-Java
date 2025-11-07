import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.lang.Math;
import java.util.Comparator;
import java.util.Arrays; // Importação necessária para compatibilidade

/**
 * Implementação do Algoritmo ID3 para o projeto "Detector de Spam de E-mail".
 */
public class ID3Algorithm {

    // --- VARIÁVEIS GLOBAIS DE SETUP ---
    public static final String TARGET_ATTRIBUTE = "Decisão"; // Atributo alvo: "Spam" ou "Não Spam"
    // Correção: Usando Arrays.asList() + ArrayList para compatibilidade
    public static final List<String> ATTRIBUTES = new ArrayList<>(Arrays.asList("Assunto", "Remetente", "Anexos", "Pontuação")); 

    /**
     * Método auxiliar para criar uma instância de dado (Map) de forma compatível (substitui Map.of).
     */
    private static Map<String, String> createMap(String assunto, String remetente, String anexos, String pontuacao, String decisao) {
        Map<String, String> map = new HashMap<>();
        map.put("Assunto", assunto);
        map.put("Remetente", remetente);
        map.put("Anexos", anexos);
        map.put("Pontuação", pontuacao);
        map.put(TARGET_ATTRIBUTE, decisao);
        return map;
    }
    
    // Conjunto de dados (Dataset) de TREINO - 14 Instâncias do projeto "Detector de Spam"
    public static final List<Map<String, String>> DATA = new ArrayList<>(Arrays.asList(
        createMap("Com Oferta", "Desconhecido", "Não", "Exclamações", "Spam"), // 1
        createMap("Com Oferta", "Desconhecido", "Sim", "Exclamações", "Spam"), // 2
        createMap("Sem Oferta", "Conhecido", "Não", "Normal", "Não Spam"), // 3
        createMap("Sem Oferta", "Desconhecido", "Não", "Normal", "Não Spam"), // 4
        createMap("Sem Oferta", "Conhecido", "Não", "Normal", "Não Spam"), // 5
        createMap("Sem Oferta", "Conhecido", "Sim", "Exclamações", "Spam"), // 6
        createMap("Com Oferta", "Desconhecido", "Não", "Normal", "Spam"), // 7
        createMap("Com Oferta", "Conhecido", "Não", "Exclamações", "Spam"), // 8
        createMap("Sem Oferta", "Desconhecido", "Não", "Normal", "Não Spam"), // 9
        createMap("Sem Oferta", "Conhecido", "Não", "Normal", "Não Spam"), // 10
        createMap("Sem Oferta", "Desconhecido", "Sim", "Exclamações", "Não Spam"), // 11
        createMap("Com Oferta", "Desconhecido", "Sim", "Normal", "Spam"), // 12
        createMap("Sem Oferta", "Conhecido", "Sim", "Normal", "Não Spam"), // 13
        createMap("Com Oferta", "Conhecido", "Sim", "Exclamações", "Spam") // 14
    ));

    // Dados de TESTE (Instâncias NUNCA VISTAS) do projeto
    public static final List<Map<String, String>> TEST_DATA = new ArrayList<>(Arrays.asList(
        // 1. Sem Oferta, Conhecido, Sim, Exclamações (Esperado: Spam)
        createMap("Sem Oferta", "Conhecido", "Sim", "Exclamações", "Spam"),
        // 2. Com Oferta, Conhecido, Não, Normal (Esperado: Spam)
        createMap("Com Oferta", "Conhecido", "Não", "Normal", "Spam"),
        // 3. Sem Oferta, Desconhecido, Não, Exclamações (Esperado: Não Spam)
        createMap("Sem Oferta", "Desconhecido", "Não", "Exclamações", "Não Spam")
    ));


    // --- MÉTODOS DE CÁLCULO ---

    /** * Calcula a Entropia de um conjunto de dados. */
    public static double calculateEntropy(List<Map<String, String>> data, String targetAttribute) {
        if (data.isEmpty()) { return 0.0; }
        
        Map<String, Long> countByClass = data.stream()
            .collect(Collectors.groupingBy(instance -> instance.get(targetAttribute), Collectors.counting()));
        
        double entropy = 0.0;
        int totalInstances = data.size();

        for (Long count : countByClass.values()) {
            double probability = (double) count / totalInstances;
            if (probability > 0) { 
                entropy -= probability * (Math.log(probability) / Math.log(2)); 
            }
        }
        return entropy;
    }

    /** * Calcula o Ganho de Informação para um atributo. */
    public static double calculateGain(List<Map<String, String>> data, String attribute, String targetAttribute) {
        double totalEntropy = calculateEntropy(data, targetAttribute); 
        int totalInstances = data.size();

        Map<String, List<Map<String, String>>> splitData = data.stream()
            .collect(Collectors.groupingBy(instance -> instance.get(attribute)));
        
        double weightedEntropy = 0.0; 

        for (List<Map<String, String>> subset : splitData.values()) {
            double probability = (double) subset.size() / totalInstances; 
            weightedEntropy += probability * calculateEntropy(subset, targetAttribute); 
        }
        
        return totalEntropy - weightedEntropy;
    }


    // --- MÉTODOS DE CONSTRUÇÃO E CLASSIFICAÇÃO ---

    /** * Algoritmo Recursivo ID3 para Construir a Árvore. */
    public static TreeNode buildTree(List<Map<String, String>> data, List<String> availableAttributes, String targetAttribute) {
        
        // CASO BASE 1: Pureza (Todos os exemplos têm a mesma classe).
        String firstClass = data.get(0).get(targetAttribute);
        if (data.stream().allMatch(instance -> instance.get(targetAttribute).equals(firstClass))) {
            return new TreeNode(firstClass, true); 
        }
        
        // CASO BASE 2: Não há mais atributos ou conjunto vazio.
        if (availableAttributes.isEmpty() || data.isEmpty()) {
            String majorityClass = data.stream().collect(Collectors.groupingBy(instance -> instance.get(targetAttribute), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(firstClass);
            return new TreeNode(majorityClass, true);
        }

        // LÓGICA RECURSIVA: Encontrar o atributo com o MAIOR Ganho (Nó Raiz)
        String bestAttribute = availableAttributes.stream()
            .max(Comparator.comparingDouble(attribute -> calculateGain(data, attribute, targetAttribute))) 
            .orElseThrow(() -> new IllegalStateException("Erro: Não foi possível encontrar o melhor atributo."));

        TreeNode root = new TreeNode(bestAttribute, false);
        List<String> remainingAttributes = new ArrayList<>(availableAttributes);
        remainingAttributes.remove(bestAttribute);

        // Chamada Recursiva
        Map<String, List<Map<String, String>>> splitData = data.stream()
            .collect(Collectors.groupingBy(instance -> instance.get(bestAttribute)));

        for (Map.Entry<String, List<Map<String, String>>> entry : splitData.entrySet()) {
            root.addChild(entry.getKey(), buildTree(entry.getValue(), remainingAttributes, targetAttribute));
        }

        return root;
    }

    /** * Imprime a Árvore de Decisão usando Recursão (Visualização). */
    public static void printTree(TreeNode node, String prefix) {
        if (node.isLeaf) { // Caso Base: Nó Folha
            System.out.println(prefix + "-> DECISÃO: " + node.attribute); 
            return;
        }
        
        System.out.println(prefix + "-> TESTE: " + node.attribute + "?"); 
        
        for (Map.Entry<String, TreeNode> entry : node.children.entrySet()) {
            String newPrefix = prefix + "   [Se " + node.attribute + " é " + entry.getKey() + "] ";
            printTree(entry.getValue(), newPrefix);
        }
    }

    /** * Usa a Árvore para Classificar uma nova instância. */
    public static String classify(TreeNode node, Map<String, String> instance) {
        if (node.isLeaf) {
            return node.attribute;
        }
        
        String attributeValue = instance.get(node.attribute);
        
        if (node.children.containsKey(attributeValue)) {
            return classify(node.children.get(attributeValue), instance);
        } else {
            System.err.println("Aviso: Valor não visto ('" + attributeValue + "') no atributo '" + node.attribute + "'. Não foi possível classificar.");
            return "DESCONHECIDO"; 
        }
    }


    public static void main(String[] args) {
        // --- ETAPA 1: SETUP ---
        System.out.println("Setup concluído! Total de Instâncias de Treino: " + DATA.size());

        // --- ETAPA 2: ENTROPIA INICIAL ---
        double initialEntropy = calculateEntropy(DATA, TARGET_ATTRIBUTE);
        System.out.printf("\nEntropia Inicial (total): %.4f\n", initialEntropy);

        // --- ETAPA 3: CONSTRUÇÃO E IMPRESSÃO DA ÁRVORE ID3 ---
        System.out.println("\n--- Construção da Árvore ID3 ---");
        
        TreeNode decisionTree = buildTree(DATA, ATTRIBUTES, TARGET_ATTRIBUTE);
        
        printTree(decisionTree, ""); 

        // --- ETAPA 4: AVALIAÇÃO DO MODELO E ACURÁCIA ---
        System.out.println("\n--- Avaliação do Modelo (Testando Classificação de Spam) ---");
        
        int correctPredictions = 0;

        for (Map<String, String> testInstance : TEST_DATA) {
            String actualClass = testInstance.get(TARGET_ATTRIBUTE);
            Map<String, String> instanceToClassify = new HashMap<>(testInstance);
            instanceToClassify.remove(TARGET_ATTRIBUTE);

            String prediction = classify(decisionTree, instanceToClassify);

            if (prediction.equals(actualClass)) {
                correctPredictions++;
                System.out.print( "✅ " );
            } else {
                System.out.print( "❌ " );
            }
            System.out.println("Previsto: " + prediction + " | Real: " + actualClass);
        }

        double accuracy = (double) correctPredictions / TEST_DATA.size() * 100;
        System.out.printf("\nACURÁCIA (Precisão) do Modelo: %d/%d = %.2f%%\n",
            correctPredictions, TEST_DATA.size(), accuracy);
    }
}