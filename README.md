# I.A.-With-Machine-Learning-in-Java

#Projeto: Classificador de Aprovação de Empréstimo Pessoal
#Dataset Utilizado (14 Instâncias)
#O objetivo é classificar se o empréstimo deve ser Aprovado (Sim/Não) com base em quatro atributos de risco:

<img width="366" height="772" alt="Captura de tela 2025-11-05 194922" src="https://github.com/user-attachments/assets/b6ccb9d0-9155-45e4-9871-abca8d4c0697" />

Variáveis Globais do Projeto:
TARGET_ATTRIBUTE: "Aprovar"
ATTRIBUTES: Histórico, Renda, Emprego, Garantia

#Exercício 1: Setup e Representação de Dados
#Foco: Criação de classes e organização do dataset.
#Código de Início (Setup do Projeto)
#O código da classe TreeNode.java é o mesmo (pois a estrutura de árvore não muda).
ID3Algorithm.java (Início):
Java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.lang.Math;
import java.util.Comparator;

public class ID3Algorithm {
    
    // VARIÁVEIS GLOBAIS DE SETUP
    public static final String TARGET_ATTRIBUTE = "Aprovar";
    
    // Lista de atributos
    public static final List<String> ATTRIBUTES = List.of("Histórico", "Renda", "Emprego", "Garantia");

    // Conjunto de dados (Dataset) - Deve ser preenchido no Exercício 1
    public static final List<Map<String, String>> DATA = new ArrayList<>();

    public static void main(String[] args) {
        // ------------------ EXERCÍCIO 1: PREENCHER O DATASET AQUI ------------------
        
        // Exemplo: DATA.add(Map.of("Histórico", "Ruim", "Renda", "Baixa", "Emprego", "Instável", "Garantia", "Não", TARGET_ATTRIBUTE, "Não"));

        System.out.println("Setup concluído! Total de Instâncias: " + DATA.size());
    }

    // MÉTODOS A SEREM IMPLEMENTADOS
    public static double calculateEntropy(List<Map<String, String>> data, String targetAttribute) { return 0.0; }
    // ... (assinaturas dos métodos do Ex. 3 e Des. 1/2)
    public static double calculateGain(List<Map<String, String>> data, String attribute, String targetAttribute) { return 0.0; }
    public static TreeNode buildTree(List<Map<String, String>> data, List<String> availableAttributes, String targetAttribute) { return null; }
    public static void printTree(TreeNode node, String prefix) { /* Implementar no Desafio 1 */ }
    public static String classify(TreeNode node, Map<String, String> instance) { return null; }
}


#Código Esperado (Exercício 1)
ID3Algorithm.java (Trecho de DATA preenchido):
Java
// ... (imports)

public class ID3Algorithm {
    // ... (TARGET_ATTRIBUTE e ATTRIBUTES)

    // Conjunto de dados (Dataset) - 14 Instâncias
    public static final List<Map<String, String>> DATA = List.of(
        Map.of("Histórico", "Ruim", "Renda", "Baixa", "Emprego", "Instável", "Garantia", "Não", TARGET_ATTRIBUTE, "Não"),
        Map.of("Histórico", "Ruim", "Renda", "Baixa", "Emprego", "Instável", "Garantia", "Sim", TARGET_ATTRIBUTE, "Não"),
        Map.of("Histórico", "Neutro", "Renda", "Alta", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Bom", "Renda", "Média", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Bom", "Renda", "Alta", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Bom", "Renda", "Alta", "Emprego", "Estável", "Garantia", "Sim", TARGET_ATTRIBUTE, "Não"),
        Map.of("Histórico", "Neutro", "Renda", "Baixa", "Emprego", "Instável", "Garantia", "Sim", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Ruim", "Renda", "Média", "Emprego", "Instável", "Garantia", "Não", TARGET_ATTRIBUTE, "Não"),
        Map.of("Histórico", "Ruim", "Renda", "Alta", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Bom", "Renda", "Média", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Ruim", "Renda", "Média", "Emprego", "Estável", "Garantia", "Sim", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Neutro", "Renda", "Média", "Emprego", "Instável", "Garantia", "Sim", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Neutro", "Renda", "Alta", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"),
        Map.of("Histórico", "Bom", "Renda", "Baixa", "Emprego", "Instável", "Garantia", "Sim", TARGET_ATTRIBUTE, "Não")
    );

    public static void main(String[] args) {
        System.out.println("Setup concluído! Total de Instâncias: " + DATA.size()); // Saída: 14
        // ...
    }
    // ...
}



#Exercício 2: O Coração Matemático – Entropia
#Foco: Implementar a fórmula da Entropia (calculateEntropy).
#Código Esperado (Exercício 2)
#ID3Algorithm.java (Método preenchido e Teste no main):
Java
// ... (imports)

public class ID3Algorithm {
    // ... (DATA e variáveis globais)

    public static void main(String[] args) {
        // ... (Verificação de setup do Ex. 1)

        // Teste do Exercício 2
        double initialEntropy = calculateEntropy(DATA, TARGET_ATTRIBUTE);
        System.out.printf("\nEntropia Inicial (total): %.4f\n", initialEntropy);
    }
    
    /**
     * Calcula a Entropia de um conjunto de dados.
     */
    public static double calculateEntropy(List<Map<String, String>> data, String targetAttribute) {
        if (data.isEmpty()) { return 0.0; }

        Map<String, Long> countByClass = data.stream()
            .collect(Collectors.groupingBy(instance -> instance.get(targetAttribute), Collectors.counting()));

        double entropy = 0.0;
        int totalInstances = data.size();

        for (Long count : countByClass.values()) {
            double probability = (double) count / totalInstances;
            if (probability > 0) {
                // Fórmula: -p * log2(p)
                entropy -= probability * (Math.log(probability) / Math.log(2)); 
            }
        }
        return entropy;
    }
    // ... (Outros métodos)
}


#Saída Esperada (Exercício 2):
#Total de Sim: 8
#Total de Não: 6
#Entropia: $\frac{8}{14}\cdot\log_2(\frac{14}{8}) + \frac{6}{14}\cdot\log_2(\frac{14}{6}) \approx 0.9852$
#Entropia Inicial (total): 0.9852



#Desafio 1: Visualização da Lógica (Testando Recursão)
#Foco: Implementar o método recursivo de percurso em árvore (printTree).
#Código Esperado (Desafio 1)
#ID3Algorithm.java (Método preenchido e Teste no main):
Java
// ... (imports, calculateEntropy, etc.)

public class ID3Algorithm {
    // ... (DATA, calculateEntropy, etc.)

    public static void main(String[] args) {
        // ... (Teste do Ex. 2)
        
        // ------------------ DESAFIO 1: TESTE DA FUNÇÃO RECURSIVA printTree ------------------
        
        // Cria uma micro-árvore manual para teste (Ex: Histórico -> Renda)
        TreeNode rootTest = new TreeNode("Histórico", false);
        TreeNode ruimTest = new TreeNode("Não", true);
        TreeNode neutroTest = new TreeNode("Renda", false);
        TreeNode bomTest = new TreeNode("Emprego", false);

        rootTest.addChild("Ruim", ruimTest);
        rootTest.addChild("Neutro", neutroTest);
        rootTest.addChild("Bom", bomTest);

        neutroTest.addChild("Alta", new TreeNode("Sim", true));
        neutroTest.addChild("Baixa", new TreeNode("Sim", true));
        bomTest.addChild("Estável", new TreeNode("Sim", true));

        System.out.println("\n--- Teste de Percurso em Árvore ---");
        printTree(rootTest, "");
    }
    
    /**
     * Imprime a Árvore de Decisão usando um Percurso em Pré-Ordem.
     */
    public static void printTree(TreeNode node, String prefix) {
        if (node.isLeaf) {
            System.out.println(prefix + "-> DECISÃO: " + node.attribute);
            return;
        }

        System.out.println(prefix + "-> TESTE: " + node.attribute + "?");

        for (Map.Entry<String, TreeNode> entry : node.children.entrySet()) {
            String attributeValue = entry.getKey();
            TreeNode childNode = entry.getValue();

            // Novo prefixo para mostrar o caminho tomado com recuo (indentação)
            String newPrefix = prefix + "   [Se " + node.attribute + " é " + attributeValue + "] ";
            printTree(childNode, newPrefix);
        }
    }
    // ... (Outros métodos)
}


#Saída Esperada (Desafio 1):
--- Teste de Percurso em Árvore ---
-> TESTE: Histórico?
   [Se Histórico é Ruim] -> DECISÃO: Não
   [Se Histórico é Neutro] -> TESTE: Renda?
      [Se Renda é Alta] -> DECISÃO: Sim
      [Se Renda é Baixa] -> DECISÃO: Sim
   [Se Histórico é Bom] -> TESTE: Emprego?
      [Se Emprego é Estável] -> DECISÃO: Sim



#Exercício 3: Seleção do Atributo e Construção Recursiva
#Foco: Implementar Ganho (calculateGain) e o algoritmo ID3 (buildTree).
#Código Esperado (Exercício 3)
#ID3Algorithm.java (Métodos preenchidos e Teste no main):
Java
// ... (imports e métodos anteriores)

public class ID3Algorithm {
    // ... (DATA, calculateEntropy, printTree, etc.)

    /**
     * Calcula o Ganho de Informação para um atributo.
     */
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

    /**
     * Algoritmo Recursivo ID3 para construir a Árvore de Decisão.
     */
    public static TreeNode buildTree(List<Map<String, String>> data, List<String> availableAttributes, String targetAttribute) {
        // Caso Base 1: Todos os exemplos são da mesma classe (Nó Folha puro)
        String firstClass = data.get(0).get(targetAttribute);
        if (data.stream().allMatch(instance -> instance.get(targetAttribute).equals(firstClass))) {
            return new TreeNode(firstClass, true);
        }

        // Caso Base 2: Não há mais atributos (ou não há dados). Retorna a classe majoritária.
        if (availableAttributes.isEmpty() || data.isEmpty()) {
            String majorityClass = data.stream()
                .collect(Collectors.groupingBy(instance -> instance.get(targetAttribute), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(firstClass); 
            return new TreeNode(majorityClass, true);
        }

        // 1. Encontrar o atributo com o MAIOR Ganho
        String bestAttribute = availableAttributes.stream()
            .max(Comparator.comparingDouble(attribute -> calculateGain(data, attribute, targetAttribute)))
            .orElseThrow(() -> new IllegalStateException("Não foi possível encontrar o melhor atributo."));

        // 2. Criar o nó raiz para o melhor atributo
        TreeNode root = new TreeNode(bestAttribute, false);
        
        // 3. Remover o melhor atributo
        List<String> remainingAttributes = new ArrayList<>(availableAttributes);
        remainingAttributes.remove(bestAttribute);
        
        // 4. Chamada Recursiva para cada valor do melhor atributo
        Map<String, List<Map<String, String>>> splitData = data.stream()
            .collect(Collectors.groupingBy(instance -> instance.get(bestAttribute)));

        for (Map.Entry<String, List<Map<String, String>>> entry : splitData.entrySet()) {
            String attributeValue = entry.getKey();
            List<Map<String, String>> subset = entry.getValue();

            TreeNode childNode = buildTree(subset, remainingAttributes, targetAttribute);
            root.addChild(attributeValue, childNode);
        }

        return root;
    }

    public static void main(String[] args) {
        // ... (Teste do Ex. 2)

        // ------------------ EXERCÍCIO 3: CONSTRUÇÃO E IMPRESSÃO DA ÁRVORE ------------------

        System.out.println("\n--- Construção da Árvore ID3 ---");
        TreeNode decisionTree = buildTree(DATA, ATTRIBUTES, TARGET_ATTRIBUTE);
        printTree(decisionTree, ""); // Usa o Desafio 1 para visualizar o resultado!
    }
}


#Saída Esperada (Exercício 3 - Lógica da Árvore):
A árvore mais provável construída com este dataset começa com Histórico.
--- Construção da Árvore ID3 ---
-> TESTE: Histórico?
   [Se Histórico é Ruim] -> TESTE: Renda?
      [Se Renda é Baixa] -> DECISÃO: Não
      [Se Renda é Média] -> TESTE: Emprego?
         [Se Emprego é Instável] -> DECISÃO: Não
         [Se Emprego é Estável] -> DECISÃO: Sim
      [Se Renda é Alta] -> DECISÃO: Sim
   [Se Histórico é Neutro] -> DECISÃO: Sim
   [Se Histórico é Bom] -> TESTE: Garantia?
      [Se Garantia é Não] -> DECISÃO: Sim
      [Se Garantia é Sim] -> DECISÃO: Não



#Desafio 2: Generalização e Avaliação do Modelo
Foco: Implementar a classificação (classify) e calcular a acurácia.
Código Esperado (Desafio 2)
ID3Algorithm.java (Métodos preenchidos e Teste no main):
Java
// ... (imports e todos os métodos anteriores)

public class ID3Algorithm {
    // ... (todos os métodos de Entropia, Ganho, buildTree, printTree)

    /**
     * Usa a Árvore de Decisão para classificar uma nova instância.
     */
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
        // ... (Construção da Árvore do Ex. 3)

        // ------------------ DESAFIO 2: TESTE DE INFERÊNCIA E ACURÁCIA ------------------

        // Conjunto de dados de TESTE (Instâncias NUNCA VISTAS)
        List<Map<String, String>> TEST_DATA = List.of(
            // 1. Histórico: Ruim, Renda: Média, Emprego: Estável, Garantia: Não (Esperado: Sim)
            Map.of("Histórico", "Ruim", "Renda", "Média", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"), 
            // 2. Histórico: Neutro, Renda: Baixa, Emprego: Estável, Garantia: Não (Esperado: Sim - Por Histórico=Neutro)
            Map.of("Histórico", "Neutro", "Renda", "Baixa", "Emprego", "Estável", "Garantia", "Não", TARGET_ATTRIBUTE, "Sim"), 
            // 3. Histórico: Bom, Renda: Alta, Emprego: Instável, Garantia: Sim (Esperado: Não)
            Map.of("Histórico", "Bom", "Renda", "Alta", "Emprego", "Instável", "Garantia", "Sim", TARGET_ATTRIBUTE, "Não")
        );
        
        int correctPredictions = 0;
        
        System.out.println("\n--- Avaliação do Modelo ---");

        for (Map<String, String> testInstance : TEST_DATA) {
            String actualClass = testInstance.get(TARGET_ATTRIBUTE);
            
            Map<String, String> instanceToClassify = new HashMap<>(testInstance);
            instanceToClassify.remove(TARGET_ATTRIBUTE); 
            
            String prediction = classify(decisionTree, instanceToClassify);
            
            if (prediction.equals(actualClass)) {
                correctPredictions++;
                System.out.print("✅ ");
            } else {
                System.out.print("❌ ");
            }
            System.out.println("Previsto: " + prediction + " | Real: " + actualClass + " | Dados: " + instanceToClassify);
        }

        double accuracy = (double) correctPredictions / TEST_DATA.size() * 100;
        System.out.printf("\nACURÁCIA (Precisão) do Modelo: %d/%d = %.2f%%\n", 
                          correctPredictions, TEST_DATA.size(), accuracy);
    }
}


#Saída Esperada (Desafio 2 - Teste de Acurácia):
--- Avaliação do Modelo ---
✅ Previsto: Sim | Real: Sim | Dados: {Histórico=Ruim, Renda=Média, Emprego=Estável, Garantia=Não}
✅ Previsto: Sim | Real: Sim | Dados: {Histórico=Neutro, Renda=Baixa, Emprego=Estável, Garantia=Não}
✅ Previsto: Não | Real: Não | Dados: {Histórico=Bom, Renda=Alta, Emprego=Instável, Garantia=Sim}

#ACURÁCIA (Precisão) do Modelo: 3/3 = 100.00%




