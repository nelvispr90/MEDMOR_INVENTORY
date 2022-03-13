package com.medmor.inventory.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.medmor.inventory.domain.Registry;
import com.medmor.inventory.repository.rowmapper.ProductRowMapper;
import com.medmor.inventory.repository.rowmapper.RegistryRowMapper;
import com.medmor.inventory.repository.rowmapper.SectionRowMapper;
import com.medmor.inventory.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Registry entity.
 */
@SuppressWarnings("unused")
class RegistryRepositoryInternalImpl implements RegistryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductRowMapper productMapper;
    private final SectionRowMapper sectionMapper;
    private final RegistryRowMapper registryMapper;

    private static final Table entityTable = Table.aliased("registry", EntityManager.ENTITY_ALIAS);
    private static final Table productTable = Table.aliased("product", "product");
    private static final Table sectionTable = Table.aliased("section", "section");

    public RegistryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductRowMapper productMapper,
        SectionRowMapper sectionMapper,
        RegistryRowMapper registryMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productMapper = productMapper;
        this.sectionMapper = sectionMapper;
        this.registryMapper = registryMapper;
    }

    @Override
    public Flux<Registry> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Registry> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Registry> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RegistrySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        columns.addAll(SectionSqlHelper.getColumns(sectionTable, "section"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable))
            .leftOuterJoin(sectionTable)
            .on(Column.create("section_id", entityTable))
            .equals(Column.create("id", sectionTable));

        String select = entityManager.createSelect(selectFrom, Registry.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Registry> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Registry> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Registry process(Row row, RowMetadata metadata) {
        Registry entity = registryMapper.apply(row, "e");
        entity.setProduct(productMapper.apply(row, "product"));
        entity.setSection(sectionMapper.apply(row, "section"));
        return entity;
    }

    @Override
    public <S extends Registry> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Registry> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Registry with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Registry entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RegistrySqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("amount", table, columnPrefix + "_amount"));

        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        columns.add(Column.aliased("section_id", table, columnPrefix + "_section_id"));
        return columns;
    }
}
