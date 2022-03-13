package com.medmor.inventory.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.medmor.inventory.IntegrationTest;
import com.medmor.inventory.domain.Section;
import com.medmor.inventory.domain.enumeration.ProductType;
import com.medmor.inventory.repository.SectionRepository;
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
 * Integration tests for the {@link SectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class SectionResourceIT {

    private static final Float DEFAULT_AREA = 1F;
    private static final Float UPDATED_AREA = 2F;

    private static final ProductType DEFAULT_PRODUCT_TYPE = ProductType.Electric_Materials;
    private static final ProductType UPDATED_PRODUCT_TYPE = ProductType.Equipment;

    private static final String ENTITY_API_URL = "/api/sections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Section section;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Section createEntity(EntityManager em) {
        Section section = new Section().area(DEFAULT_AREA).productType(DEFAULT_PRODUCT_TYPE);
        return section;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Section createUpdatedEntity(EntityManager em) {
        Section section = new Section().area(UPDATED_AREA).productType(UPDATED_PRODUCT_TYPE);
        return section;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Section.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        section = createEntity(em);
    }

    @Test
    void createSection() throws Exception {
        int databaseSizeBeforeCreate = sectionRepository.findAll().collectList().block().size();
        // Create the Section
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeCreate + 1);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getArea()).isEqualTo(DEFAULT_AREA);
        assertThat(testSection.getProductType()).isEqualTo(DEFAULT_PRODUCT_TYPE);
    }

    @Test
    void createSectionWithExistingId() throws Exception {
        // Create the Section with an existing ID
        section.setId(1L);

        int databaseSizeBeforeCreate = sectionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkAreaIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectionRepository.findAll().collectList().block().size();
        // set the field null
        section.setArea(null);

        // Create the Section, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkProductTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectionRepository.findAll().collectList().block().size();
        // set the field null
        section.setProductType(null);

        // Create the Section, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSections() {
        // Initialize the database
        sectionRepository.save(section).block();

        // Get all the sectionList
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
            .value(hasItem(section.getId().intValue()))
            .jsonPath("$.[*].area")
            .value(hasItem(DEFAULT_AREA.doubleValue()))
            .jsonPath("$.[*].productType")
            .value(hasItem(DEFAULT_PRODUCT_TYPE.toString()));
    }

    @Test
    void getSection() {
        // Initialize the database
        sectionRepository.save(section).block();

        // Get the section
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, section.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(section.getId().intValue()))
            .jsonPath("$.area")
            .value(is(DEFAULT_AREA.doubleValue()))
            .jsonPath("$.productType")
            .value(is(DEFAULT_PRODUCT_TYPE.toString()));
    }

    @Test
    void getNonExistingSection() {
        // Get the section
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSection() throws Exception {
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();

        // Update the section
        Section updatedSection = sectionRepository.findById(section.getId()).block();
        updatedSection.area(UPDATED_AREA).productType(UPDATED_PRODUCT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSection.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testSection.getProductType()).isEqualTo(UPDATED_PRODUCT_TYPE);
    }

    @Test
    void putNonExistingSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, section.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSectionWithPatch() throws Exception {
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();

        // Update the section using partial update
        Section partialUpdatedSection = new Section();
        partialUpdatedSection.setId(section.getId());

        partialUpdatedSection.area(UPDATED_AREA).productType(UPDATED_PRODUCT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testSection.getProductType()).isEqualTo(UPDATED_PRODUCT_TYPE);
    }

    @Test
    void fullUpdateSectionWithPatch() throws Exception {
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();

        // Update the section using partial update
        Section partialUpdatedSection = new Section();
        partialUpdatedSection.setId(section.getId());

        partialUpdatedSection.area(UPDATED_AREA).productType(UPDATED_PRODUCT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testSection.getProductType()).isEqualTo(UPDATED_PRODUCT_TYPE);
    }

    @Test
    void patchNonExistingSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, section.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(section))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSection() {
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeDelete = sectionRepository.findAll().collectList().block().size();

        // Delete the section
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, section.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
