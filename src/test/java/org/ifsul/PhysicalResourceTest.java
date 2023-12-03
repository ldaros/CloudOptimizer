package org.ifsul;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhysicalResourceTest {
    @Test
    void getEfficiency() {
        PhysicalResource resource = new PhysicalResource("1", 1024);
        assertEquals("1", resource.getId());
    }

}