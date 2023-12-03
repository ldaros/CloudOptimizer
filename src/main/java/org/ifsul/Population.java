package org.ifsul;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Population {
    private static final Logger logger = LoggerFactory.getLogger(Population.class);

    private List<Arrangement> arrangements;

    public Population() {
        this.arrangements = new ArrayList<>();
    }

    /**
     * Gera uma população inicial de arranjos.
     *
     * @param size      O tamanho da população.
     * @param resources Lista de recursos físicos disponíveis.
     * @param instances Lista de instâncias virtuais a serem alocadas.
     */
    public void generateInitialPopulation(int size, List<PhysicalResource> resources, List<VirtualInstance> instances) {
        int numResources = resources.size();
        int numInstances = instances.size();
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            Arrangement arrangement = new Arrangement(resources, instances);

            for (int j = 0; j < numInstances; j++) {
                int allocatedResource = random.nextInt(numResources);
                arrangement.setBit(allocatedResource, j, true);
            }

            arrangements.add(arrangement);
        }
    }

    /**
     * Avalia a eficiência de cada arranjo na população.
     *
     * @return Uma lista de eficiências correspondente aos arranjos.
     */
    public List<Double> evaluateFitness() {
        List<Double> fitnessScores = new ArrayList<>();
        for (Arrangement arrangement : arrangements) {
            double efficiency = arrangement.calculateSetEfficiency();
            fitnessScores.add(efficiency);
        }
        return fitnessScores;
    }

    /**
     * Seleciona os X melhores arranjos da população.
     *
     * @param quantity O tamanho do torneio.
     * @return Uma lista de arranjos selecionados.
     */
    public List<Arrangement> select(int quantity) {
        // Ordena os arranjos pela eficiência em ordem decrescente
        List<Arrangement> sortedArrangements = arrangements.stream().sorted(Comparator.comparing(Arrangement::calculateSetEfficiency).reversed()).toList();

        // Seleciona os X melhores arranjos
        List<Arrangement> selected = new ArrayList<>();
        for (int i = 0; i < quantity && i < sortedArrangements.size(); i++) {
            selected.add(sortedArrangements.get(i));
        }

        return selected;
    }

    /**
     * Combina arranjos para criar novos.
     *
     * @return Uma lista de novos arranjos.
     */
    public Arrangement crossover(@NotNull Arrangement parent1, @NotNull Arrangement parent2) {
        // Supõe-se que parent1 e parent2 têm o mesmo tamanho de cromossomo
        int numResources = parent1.getResources().size();
        int numInstances = parent1.getInstances().size();
        Random random = new Random();

        Arrangement child = new Arrangement(parent1.getResources(), parent1.getInstances());

        for (int i = 0; i < numInstances; i++) {
            for (int j = 0; j < numResources; j++) {
                boolean bit = random.nextBoolean() ? parent1.getBit(j, i) : parent2.getBit(j, i);

                // verica se esta instancia já foi alocada em outro recurso
                for (int k = 0; k < numResources; k++) {
                    if (child.getBit(k, i) && k != j) {
                        bit = false;
                    }
                }

                child.setBit(j, i, bit);
            }
        }

        // Se o filho não tiver alocado todas as instâncias, aloca as restantes aleatoriamente
        for (int i = 0; i < numInstances; i++) {
            boolean allocated = false;
            for (int j = 0; j < numResources; j++) {
                if (child.getBit(j, i)) {
                    allocated = true;
                    break;
                }
            }

            if (!allocated) {
                int allocatedResource = random.nextInt(numResources);
                child.setBit(allocatedResource, i, true);
            }
        }

        return child;
    }

    /**
     * Aplica mutações aleatórias.
     */
    public Arrangement mutate(@NotNull Arrangement arrangement, double mutationRate) {
        int numResources = arrangement.getResources().size();
        int numInstances = arrangement.getInstances().size();
        Random random = new Random();

        for (int i = 0; i < numInstances; i++) {
            if (random.nextDouble() < mutationRate) {
                // Encontra o recurso atual em que a instância está alocada
                int currentResource = -1;
                for (int j = 0; j < numResources; j++) {
                    if (arrangement.getBit(j, i)) {
                        currentResource = j;
                        break;
                    }
                }

                // Escolhe um novo recurso aleatório diferente do atual para alocar a instância
                int newResource;
                do {
                    newResource = random.nextInt(numResources);
                } while (newResource == currentResource);

                // Desaloca da posição antiga e aloca na nova posição
                if (currentResource != -1) {
                    arrangement.setBit(currentResource, i, false);
                }
                arrangement.setBit(newResource, i, true);
            }
        }

        return arrangement;
    }

}
