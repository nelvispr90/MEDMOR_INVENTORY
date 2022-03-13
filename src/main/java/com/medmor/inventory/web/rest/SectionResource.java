package com.medmor.inventory.web.rest;

import com.medmor.inventory.domain.Section;
import com.medmor.inventory.repository.SectionRepository;
import com.medmor.inventory.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.medmor.inventory.domain.Section}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SectionResource {

    private final Logger log = LoggerFactory.getLogger(SectionResource.class);

    private static final String ENTITY_NAME = "section";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SectionRepository sectionRepository;

    public SectionResource(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    /**
     * {@code POST  /sections} : Create a new section.
     *
     * @param section the section to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new section, or with status {@code 400 (Bad Request)} if the section has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sections")
    public Mono<ResponseEntity<Section>> createSection(@Valid @RequestBody Section section) throws URISyntaxException {
        log.debug("REST request to save Section : {}", section);
        if (section.getId() != null) {
            throw new BadRequestAlertException("A new section cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return sectionRepository
            .save(section)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/sections/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /sections/:id} : Updates an existing section.
     *
     * @param id the id of the section to save.
     * @param section the section to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated section,
     * or with status {@code 400 (Bad Request)} if the section is not valid,
     * or with status {@code 500 (Internal Server Error)} if the section couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sections/{id}")
    public Mono<ResponseEntity<Section>> updateSection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Section section
    ) throws URISyntaxException {
        log.debug("REST request to update Section : {}, {}", id, section);
        if (section.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, section.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sectionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return sectionRepository
                        .save(section)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /sections/:id} : Partial updates given fields of an existing section, field will ignore if it is null
     *
     * @param id the id of the section to save.
     * @param section the section to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated section,
     * or with status {@code 400 (Bad Request)} if the section is not valid,
     * or with status {@code 404 (Not Found)} if the section is not found,
     * or with status {@code 500 (Internal Server Error)} if the section couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sections/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Section>> partialUpdateSection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Section section
    ) throws URISyntaxException {
        log.debug("REST request to partial update Section partially : {}, {}", id, section);
        if (section.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, section.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sectionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Section> result = sectionRepository
                        .findById(section.getId())
                        .map(
                            existingSection -> {
                                if (section.getArea() != null) {
                                    existingSection.setArea(section.getArea());
                                }
                                if (section.getProductType() != null) {
                                    existingSection.setProductType(section.getProductType());
                                }

                                return existingSection;
                            }
                        )
                        .flatMap(sectionRepository::save);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /sections} : get all the sections.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sections in body.
     */
    @GetMapping("/sections")
    public Mono<ResponseEntity<List<Section>>> getAllSections(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Sections");
        return sectionRepository
            .count()
            .zipWith(sectionRepository.findAllBy(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /sections/:id} : get the "id" section.
     *
     * @param id the id of the section to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the section, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sections/{id}")
    public Mono<ResponseEntity<Section>> getSection(@PathVariable Long id) {
        log.debug("REST request to get Section : {}", id);
        Mono<Section> section = sectionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(section);
    }

    /**
     * {@code DELETE  /sections/:id} : delete the "id" section.
     *
     * @param id the id of the section to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sections/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSection(@PathVariable Long id) {
        log.debug("REST request to delete Section : {}", id);
        return sectionRepository
            .deleteById(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
