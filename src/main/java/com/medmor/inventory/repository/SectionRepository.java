package com.medmor.inventory.repository;

import com.medmor.inventory.domain.Section;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Section entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SectionRepository extends R2dbcRepository<Section, Long>, SectionRepositoryInternal {
    Flux<Section> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Section> findAll();

    @Override
    Mono<Section> findById(Long id);

    @Override
    <S extends Section> Mono<S> save(S entity);
}

interface SectionRepositoryInternal {
    <S extends Section> Mono<S> insert(S entity);
    <S extends Section> Mono<S> save(S entity);
    Mono<Integer> update(Section entity);

    Flux<Section> findAll();
    Mono<Section> findById(Long id);
    Flux<Section> findAllBy(Pageable pageable);
    Flux<Section> findAllBy(Pageable pageable, Criteria criteria);
}
