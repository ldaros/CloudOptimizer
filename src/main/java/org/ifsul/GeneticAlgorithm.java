package org.ifsul;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GeneticAlgorithm {
    static final int NUM_GENERATIONS = 500;
    static final int POPULATION_SIZE = 100;
    static final int TOURNAMENT_SIZE = 5;
    final double mutationRate;
    final Population population;
    private final List<PhysicalResource> physicalResources;
    private final List<VirtualInstance> virtualInstances;
    @Getter
    private char[] solution;
    @Getter
    private double solutionFitness;

    public GeneticAlgorithm() {
        population = new Population();

        physicalResources = Arrays.asList(
                new PhysicalResource("p0", 4096),
                new PhysicalResource("p1", 2048),
                new PhysicalResource("p2", 1024),
                new PhysicalResource("p3", 1024),
                new PhysicalResource("p4", 1024)
        );

        virtualInstances = Arrays.asList(
                new VirtualInstance("v0", 2048),
                new VirtualInstance("v1", 1024),
                new VirtualInstance("v2", 512),
                new VirtualInstance("v3", 512),
                new VirtualInstance("v4", 512),
                new VirtualInstance("v7", 512)
        );

        int totalMemory = physicalResources.stream().mapToInt(PhysicalResource::getTotalMemory).sum();
        int totalMemoryRequired = virtualInstances.stream().mapToInt(VirtualInstance::getMemoryRequirement).sum();

        if (totalMemoryRequired > totalMemory) {
            System.out.println("AVISO: Não há memória suficiente para alocar todas as instâncias virtuais. O algoritmo pode não convergir.");
        }

        System.out.println("Memória total: " + totalMemory + "MB");
        System.out.println("Memória requerida: " + totalMemoryRequired + "MB");

        // Gera uma população inicial
        population.generateInitialPopulation(POPULATION_SIZE, physicalResources, virtualInstances);
        mutationRate = 1.0 / (physicalResources.size() * virtualInstances.size());
        solution = new char[physicalResources.size() * virtualInstances.size()];
    }

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.run();

        System.out.println("Melhor arranjo: " + ga.getSolutionString());
        System.out.println();
        System.out.println("Estatísticas: " + ga.getStatsString());
        System.out.println("Eficiência: " + ga.getSolutionFitness());
    }

    private void select() {
        population.setArrangements(population.select(TOURNAMENT_SIZE));
    }

    private void crossover() {
        List<Arrangement> newGeneration = new ArrayList<>();
        Random random = new Random();

        while (newGeneration.size() < POPULATION_SIZE) {
            int index1 = random.nextInt(population.getArrangements().size());
            int index2 = random.nextInt(population.getArrangements().size());

            Arrangement parent1 = population.getArrangements().get(index1);
            Arrangement parent2 = population.getArrangements().get(index2);

            Arrangement child = population.crossover(parent1, parent2);
            newGeneration.add(child);
        }

        population.setArrangements(newGeneration);
    }

    private void mutate() {
        for (Arrangement arrangement : population.getArrangements()) {
            population.mutate(arrangement, mutationRate);
        }
    }

    public void run() {
        for (int i = 0; i < NUM_GENERATIONS; i++) {
            select();
            crossover();
            mutate();

            // Encontrar o arranjo mais eficiente nesta geração
            Arrangement mostEfficientArrangement = population.getArrangements().stream()
                    .max(Comparator.comparing(Arrangement::calculateSetEfficiency))
                    .orElse(null);

            if (mostEfficientArrangement != null) {
                double mostEfficientArrangementFitness = mostEfficientArrangement.calculateSetEfficiency();

                if (mostEfficientArrangementFitness > solutionFitness) {
                    solutionFitness = mostEfficientArrangementFitness;
                    solution = mostEfficientArrangement.getGenes();
                }
            }

            log.info("Geração " + i + ": Melhor eficiência = " +
                    population.evaluateFitness().stream().max(Double::compare).orElse(0.0) + " | " +
                    "Pior eficiência = " + population.evaluateFitness().stream().min(Double::compare).orElse(0.0));
        }
    }

    public String getSolutionString() {
        int numResources = physicalResources.size();
        int numInstances = virtualInstances.size();

        StringBuilder sb = new StringBuilder("[\n");

        for (int i = 0; i < numResources; i++) {
            for (int j = 0; j < numInstances; j++) {
                sb.append(solution[i * numInstances + j]);
            }
            sb.append("\n");
        }

        sb.append("]");

        return sb.toString();
    }

    public String getStatsString() {
        int numResources = physicalResources.size();
        int numInstances = virtualInstances.size();

        StringBuilder sb = new StringBuilder("\n");

        for (int i = 0; i < numResources; i++) {
            int totalMemory = physicalResources.get(i).getTotalMemory();
            int usedMemory = 0;

            for (int j = 0; j < numInstances; j++) {
                if (solution[i * numInstances + j] == '1') {
                    usedMemory += virtualInstances.get(j).getMemoryRequirement();
                }
            }

            sb.append(physicalResources.get(i));
            sb.append(": ");
            sb.append(usedMemory);
            sb.append(" / ");
            sb.append(totalMemory);
            sb.append("MB\n");
        }

        return sb.toString();
    }
}
