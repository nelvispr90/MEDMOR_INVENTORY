package com.medmor.inventory.repository;

import com.medmor.inventory.domain.Registry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Registry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegistryRepository extends R2dbcRepository<Registry, Long>, RegistryRepositoryInternal {
    Flux<Registry> findAllBy(Pageable pageable);

    @Query("SELECT * FROM registry entity WHERE entity.product_id = :id")
    Flux<Registry> findByProduct(Long id);

    @Query("SELECT * FROM registry entity WHERE entity.product_id IS NULL")
    Flux<Registry> findAllWhereProductIsNull();

    @Query("SELECT * FROM registry entity WHERE entity.section_id = :id")
    Flux<Registry> findBySection(Long id);

    @Query("SELECT * FROM registry entity WHERE entity.section_id IS NULL")
    Flux<Registry> findAllWhereSectionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Registry> findAll();

    @Override
    Mono<Registry> findById(Long id);

    @Override
    <S extends Registry> Mono<S> save(S entity);
}

interface RegistryRepositoryInternal {
    <S extends Registry> Mono<S> insert(S entity);
    <S extends Registry> Mono<S> save(S entity);
    Mono<Integer> update(Registry entity);

    Flux<Registry> findAll();
    Mono<Registry> findById(Long id);
    Flux<Registry> findAllBy(Pageable pageable);
    Flux<Registry> findAllBy(Pageable pageable, Criteria criteria);
}
