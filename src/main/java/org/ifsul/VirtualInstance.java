package org.ifsul;

import lombok.Getter;

@Getter
public class VirtualInstance {
    /**
     * Identificador da instância
     */
    final String id;

    /**
     * Memória necessária em MB
     */
    final int memoryRequirement;

    public VirtualInstance(String id, int memoryRequirement) {
        this.id = id;
        this.memoryRequirement = memoryRequirement;
    }

    @Override
    public String toString() {
        return "VirtualInstance{id='" + id + '}';
    }
}
