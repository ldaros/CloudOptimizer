package org.ifsul;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representa um arranjo de instâncias virtuais em recursos físicos.
 */
@Getter
public class Arrangement {
    private final List<PhysicalResource> resources;
    private final List<VirtualInstance> instances;
    private final char[] genes;

    public Arrangement(@NotNull List<PhysicalResource> resources, @NotNull List<VirtualInstance> instances) {
        this.resources = resources;
        this.instances = instances;

        genes = new char[resources.size() * instances.size()];
        Arrays.fill(genes, '0');
    }

    /**
     * Aloca uma instância virtual a um recurso físico.
     *
     * @param resourceIndex Índice do recurso físico.
     * @param instanceIndex Índice da instância virtual.
     */
    public void setBit(int resourceIndex, int instanceIndex, boolean value) {
        if (resourceIndex >= resources.size() || instanceIndex >= instances.size() || resourceIndex < 0 || instanceIndex < 0) {
            throw new IllegalArgumentException("Índice inválido");
        }

        genes[resourceIndex * instances.size() + instanceIndex] = value ? '1' : '0';
    }

    public boolean getBit(int j, int i) {
        return genes[j * instances.size() + i] == '1';
    }

    /**
     * Calcula a eficiência do arranjo.
     *
     * @return Eficiência do arranjo.
     */
    public double calculateSetEfficiency() {
        double PENALTY_FACTOR = -1;
        double total = 0.0;

        for (PhysicalResource resource : resources) {
            int availableMemory = resource.getTotalMemory();
            int totalMemory = resource.getTotalMemory();

            for (VirtualInstance instance : instances) {
                if (getBit(resources.indexOf(resource), instances.indexOf(instance))) {
                    availableMemory -= instance.getMemoryRequirement();
                }
            }

            double efficiency = (double) (totalMemory - availableMemory) / totalMemory;

            // Se o recurso não tem memória suficiente, aplica a penalidade
            if (availableMemory < 0) {
                efficiency *= PENALTY_FACTOR;
            }

            // Se o recurso não foi utilizado, a eficiência é 1
            if (availableMemory == totalMemory) {
                efficiency = 1;
            }

            total += efficiency;
        }

        return total;
    }

    public List<VirtualInstance> getAllocatedVirtualInstances() {
        List<VirtualInstance> instances = new ArrayList<>();

        for (int i = 0; i < resources.size(); i++) {
            for (int j = 0; j < this.instances.size(); j++) {
                if (genes[i * this.instances.size() + j] == '1') {
                    instances.add(this.instances.get(j));
                }
            }
        }

        return instances;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Arrangement{\n");

        for (int i = 0; i < resources.size(); i++) {
            for (int j = 0; j < instances.size(); j++) {
                sb.append(genes[i * instances.size() + j]);
            }
            sb.append("\n");
        }

        sb.append("}");

        return sb.toString();
    }

}
