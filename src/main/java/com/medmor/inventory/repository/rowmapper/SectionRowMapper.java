package com.medmor.inventory.repository.rowmapper;

import com.medmor.inventory.domain.Section;
import com.medmor.inventory.domain.enumeration.ProductType;
import com.medmor.inventory.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Section}, with proper type conversions.
 */
@Service
public class SectionRowMapper implements BiFunction<Row, String, Section> {

    private final ColumnConverter converter;

    public SectionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Section} stored in the database.
     */
    @Override
    public Section apply(Row row, String prefix) {
        Section entity = new Section();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setArea(converter.fromRow(row, prefix + "_area", Float.class));
        entity.setProductType(converter.fromRow(row, prefix + "_product_type", ProductType.class));
        return entity;
    }
}
