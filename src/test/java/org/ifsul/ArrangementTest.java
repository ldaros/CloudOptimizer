package org.ifsul;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrangementTest {
    List<PhysicalResource> resources;
    List<VirtualInstance> instances;
    Arrangement arrangement;

    @BeforeEach
    void setUp() {
        resources = Arrays.asList(new PhysicalResource("p0", 1024), new PhysicalResource("p1", 1024));

        instances = Arrays.asList(
                new VirtualInstance("v0", 512),
                new VirtualInstance("v1", 512),
                new VirtualInstance("v2", 512)
        );

        arrangement = new Arrangement(resources, instances);
    }

    @Test
    void testAllocate() {
        // Testa se a alocação foi bem-sucedida
        arrangement.setBit(0, 0, true);
        arrangement.setBit(0, 1, true);
        arrangement.setBit(1, 2, true);

        // Teste se os genes foram atualizados corretamente
        assertEquals("110" + "001", String.valueOf(arrangement.getGenes()));
        assertTrue(arrangement.getBit(1, 2));
    }

    @Test
    void testCalculateSetEfficiency() {
        // Como os dois recursos não estão ativos, a eficiência do arranjo deve ser 2
        assertEquals(2, arrangement.calculateSetEfficiency());

        arrangement.setBit(0, 0, true);

        // Eficiência do arranjo deve ser 1.5, pois o primeiro recurso está com 50% de uso e o segundo está inativo
        assertEquals(1.5, arrangement.calculateSetEfficiency());

        arrangement.setBit(1, 2, true);

        // Eficiência do arranjo deve ser 1, pois os dois recursos estão com 50% de uso
        assertEquals(1, arrangement.calculateSetEfficiency());

        arrangement.setBit(0, 1, true);
        assertEquals(1.5, arrangement.calculateSetEfficiency());
    }

    @Test
    void testGetAllocatedVirtualInstances() {
        arrangement.setBit(0, 0, true);
        arrangement.setBit(0, 1, true);
        arrangement.setBit(1, 2, true);

        // Testa se as instâncias alocadas são as mesmas que foram passadas para o método allocate
        assertEquals(instances, arrangement.getAllocatedVirtualInstances());
    }
}