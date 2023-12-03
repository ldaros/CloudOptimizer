package org.ifsul;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PopulationTest {

    final List<PhysicalResource> resources = Arrays.asList(
            new PhysicalResource("p0", 1024),
            new PhysicalResource("p1", 1024)
    );

    final List<VirtualInstance> instances = Arrays.asList(
            new VirtualInstance("v0", 512),
            new VirtualInstance("v1", 512),
            new VirtualInstance("v2", 512)
    );

    @Test
    void generateInitialPopulationTest() {
        // Teste se a população foi gerada corretamente
        Population population = new Population();

        population.generateInitialPopulation(2, resources, instances);
        assertEquals(2, population.getArrangements().size());

        // Testa se os arranjos são validos
        // Um arranjo valido contem todos os recursos virtuais alocados
        for (Arrangement arrangement : population.getArrangements()) {
            assertEquals(3, arrangement.getAllocatedVirtualInstances().size());
        }
    }

    @Test
    void evaluateFitnessTest() {
        Population population = new Population();

        population.generateInitialPopulation(2, resources, instances);

        // Testa se a avaliação de fitness foi feita corretamente
        assertEquals(2, population.evaluateFitness().size());
    }

    @Test
    void selectTest() {
        Population population = new Population();

        population.generateInitialPopulation(5, resources, instances);

        // Testa se a seleção foi feita corretamente
        assertEquals(2, population.select(2).size());

        // Testa se a seleção contem os melhores arranjos
        List<Double> fitnessScores = population.evaluateFitness();
        List<Arrangement> selectedArrangements = population.select(2);

        for (Arrangement arrangement : selectedArrangements) {
            assertTrue(fitnessScores.contains(arrangement.calculateSetEfficiency()));
        }
    }

    @Test
    void crossoverTest() {
        Population population = new Population();

        population.generateInitialPopulation(2, resources, instances);

        // Pega os dois primeiros arranjos da população
        Arrangement arrangement1 = population.getArrangements().get(0);
        Arrangement arrangement2 = population.getArrangements().get(1);

        // Testa se o filho gerado contem o mesmo numero de instancias alocadas
        assertEquals(3, population.crossover(arrangement1, arrangement2).getAllocatedVirtualInstances().size());
    }

    @Test
    void mutateTest() {
        Population population = new Population();
        double numberOfBits = resources.size() * instances.size();
        double mutationRate = 1 / numberOfBits;

        population.generateInitialPopulation(2, resources, instances);

        // Pega o primeiro arranjo da população
        Arrangement arrangement = population.getArrangements().get(0);

        // Testa se o arranjo após mutação contem o mesmo numero de instancias alocadas
        assertEquals(3, population.mutate(arrangement, mutationRate).getAllocatedVirtualInstances().size());
    }
}