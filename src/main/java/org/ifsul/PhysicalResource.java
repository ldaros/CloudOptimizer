package org.ifsul;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa um recurso físico, como um servidor.
 * Sua capacidade é medida em memória.
 */
@Getter
@Setter
public class PhysicalResource {
    /**
     * Identificador do recurso
     */
    String id;

    /**
     * Memória total em MB
     */
    int totalMemory;

    public PhysicalResource(String id, int totalMemory) {
        this.id = id;
        this.totalMemory = totalMemory;
    }

    @Override
    public String toString() {
        return "PhysicalResource{" + "id='" + id + "'}";
    }
}
