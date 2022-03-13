package com.medmor.inventory.web.rest;

import com.medmor.inventory.domain.Registry;
import com.medmor.inventory.repository.RegistryRepository;
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
 * REST controller for managing {@link com.medmor.inventory.domain.Registry}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RegistryResource {

    private final Logger log = LoggerFactory.getLogger(RegistryResource.class);

    private static final String ENTITY_NAME = "registry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RegistryRepository registryRepository;

    public RegistryResource(RegistryRepository registryRepository) {
        this.registryRepository = registryRepository;
    }

    /**
     * {@code POST  /registries} : Create a new registry.
     *
     * @param registry the registry to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new registry, or with status {@code 400 (Bad Request)} if the registry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/registries")
    public Mono<ResponseEntity<Registry>> createRegistry(@Valid @RequestBody Registry registry) throws URISyntaxException {
        log.debug("REST request to save Registry : {}", registry);
        if (registry.getId() != null) {
            throw new BadRequestAlertException("A new registry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return registryRepository
            .save(registry)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/registries/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /registries/:id} : Updates an existing registry.
     *
     * @param id the id of the registry to save.
     * @param registry the registry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated registry,
     * or with status {@code 400 (Bad Request)} if the registry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the registry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/registries/{id}")
    public Mono<ResponseEntity<Registry>> updateRegistry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Registry registry
    ) throws URISyntaxException {
        log.debug("REST request to update Registry : {}, {}", id, registry);
        if (registry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, registry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return registryRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return registryRepository
                        .save(registry)
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
     * {@code PATCH  /registries/:id} : Partial updates given fields of an existing registry, field will ignore if it is null
     *
     * @param id the id of the registry to save.
     * @param registry the registry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated registry,
     * or with status {@code 400 (Bad Request)} if the registry is not valid,
     * or with status {@code 404 (Not Found)} if the registry is not found,
     * or with status {@code 500 (Internal Server Error)} if the registry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/registries/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Registry>> partialUpdateRegistry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Registry registry
    ) throws URISyntaxException {
        log.debug("REST request to partial update Registry partially : {}, {}", id, registry);
        if (registry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, registry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return registryRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Registry> result = registryRepository
                        .findById(registry.getId())
                        .map(
                            existingRegistry -> {
                                if (registry.getAmount() != null) {
                                    existingRegistry.setAmount(registry.getAmount());
                                }

                                return existingRegistry;
                            }
                        )
                        .flatMap(registryRepository::save);

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
     * {@code GET  /registries} : get all the registries.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of registries in body.
     */
    @GetMapping("/registries")
    public Mono<ResponseEntity<List<Registry>>> getAllRegistries(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Registries");
        return registryRepository
            .count()
            .zipWith(registryRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /registries/:id} : get the "id" registry.
     *
     * @param id the id of the registry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the registry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/registries/{id}")
    public Mono<ResponseEntity<Registry>> getRegistry(@PathVariable Long id) {
        log.debug("REST request to get Registry : {}", id);
        Mono<Registry> registry = registryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(registry);
    }

    /**
     * {@code DELETE  /registries/:id} : delete the "id" registry.
     *
     * @param id the id of the registry to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/registries/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRegistry(@PathVariable Long id) {
        log.debug("REST request to delete Registry : {}", id);
        return registryRepository
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
