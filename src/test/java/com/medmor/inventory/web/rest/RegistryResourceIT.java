package com.medmor.inventory.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.medmor.inventory.IntegrationTest;
import com.medmor.inventory.domain.Product;
import com.medmor.inventory.domain.Registry;
import com.medmor.inventory.domain.Section;
import com.medmor.inventory.repository.RegistryRepository;
import com.medmor.inventory.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RegistryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RegistryResourceIT {

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final String ENTITY_API_URL = "/api/registries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RegistryRepository registryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Registry registry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Registry createEntity(EntityManager em) {
        Registry registry = new Registry().amount(DEFAULT_AMOUNT);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        registry.setProduct(product);
        // Add required entity
        Section section;
        section = em.insert(SectionResourceIT.createEntity(em)).block();
        registry.setSection(section);
        return registry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Registry createUpdatedEntity(EntityManager em) {
        Registry registry = new Registry().amount(UPDATED_AMOUNT);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        registry.setProduct(product);
        // Add required entity
        Section section;
        section = em.insert(SectionResourceIT.createUpdatedEntity(em)).block();
        registry.setSection(section);
        return registry;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Registry.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProductResourceIT.deleteEntities(em);
        SectionResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        registry = createEntity(em);
    }

    @Test
    void createRegistry() throws Exception {
        int databaseSizeBeforeCreate = registryRepository.findAll().collectList().block().size();
        // Create the Registry
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeCreate + 1);
        Registry testRegistry = registryList.get(registryList.size() - 1);
        assertThat(testRegistry.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    void createRegistryWithExistingId() throws Exception {
        // Create the Registry with an existing ID
        registry.setId(1L);

        int databaseSizeBeforeCreate = registryRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = registryRepository.findAll().collectList().block().size();
        // set the field null
        registry.setAmount(null);

        // Create the Registry, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRegistries() {
        // Initialize the database
        registryRepository.save(registry).block();

        // Get all the registryList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(registry.getId().intValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT));
    }

    @Test
    void getRegistry() {
        // Initialize the database
        registryRepository.save(registry).block();

        // Get the registry
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, registry.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(registry.getId().intValue()))
            .jsonPath("$.amount")
            .value(is(DEFAULT_AMOUNT));
    }

    @Test
    void getNonExistingRegistry() {
        // Get the registry
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRegistry() throws Exception {
        // Initialize the database
        registryRepository.save(registry).block();

        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();

        // Update the registry
        Registry updatedRegistry = registryRepository.findById(registry.getId()).block();
        updatedRegistry.amount(UPDATED_AMOUNT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRegistry.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRegistry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
        Registry testRegistry = registryList.get(registryList.size() - 1);
        assertThat(testRegistry.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    void putNonExistingRegistry() throws Exception {
        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();
        registry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, registry.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRegistry() throws Exception {
        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();
        registry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRegistry() throws Exception {
        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();
        registry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRegistryWithPatch() throws Exception {
        // Initialize the database
        registryRepository.save(registry).block();

        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();

        // Update the registry using partial update
        Registry partialUpdatedRegistry = new Registry();
        partialUpdatedRegistry.setId(registry.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegistry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRegistry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
        Registry testRegistry = registryList.get(registryList.size() - 1);
        assertThat(testRegistry.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    void fullUpdateRegistryWithPatch() throws Exception {
        // Initialize the database
        registryRepository.save(registry).block();

        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();

        // Update the registry using partial update
        Registry partialUpdatedRegistry = new Registry();
        partialUpdatedRegistry.setId(registry.getId());

        partialUpdatedRegistry.amount(UPDATED_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegistry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRegistry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
        Registry testRegistry = registryList.get(registryList.size() - 1);
        assertThat(testRegistry.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    void patchNonExistingRegistry() throws Exception {
        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();
        registry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, registry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRegistry() throws Exception {
        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();
        registry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRegistry() throws Exception {
        int databaseSizeBeforeUpdate = registryRepository.findAll().collectList().block().size();
        registry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(registry))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Registry in the database
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRegistry() {
        // Initialize the database
        registryRepository.save(registry).block();

        int databaseSizeBeforeDelete = registryRepository.findAll().collectList().block().size();

        // Delete the registry
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, registry.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Registry> registryList = registryRepository.findAll().collectList().block();
        assertThat(registryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
