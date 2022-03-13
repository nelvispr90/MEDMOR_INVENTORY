package com.medmor.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.medmor.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RegistryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Registry.class);
        Registry registry1 = new Registry();
        registry1.setId(1L);
        Registry registry2 = new Registry();
        registry2.setId(registry1.getId());
        assertThat(registry1).isEqualTo(registry2);
        registry2.setId(2L);
        assertThat(registry1).isNotEqualTo(registry2);
        registry1.setId(null);
        assertThat(registry1).isNotEqualTo(registry2);
    }
}
