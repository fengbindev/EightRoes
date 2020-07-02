package com.ssrs.framework.core.modelmapper.jdk8;

import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import java.util.Optional;

/**
 * Converts  {@link Object} to {@link Optional}
 *
 * @author ssrs
 */
public class ToOptionalConverter implements ConditionalConverter<Object, Optional<Object>> {

    @Override
    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return (!Optional.class.equals(sourceType) && Optional.class.equals(destinationType))
                ? MatchResult.FULL
                : MatchResult.NONE;
    }

    @Override
    public Optional<Object> convert(MappingContext<Object, Optional<Object>> mappingContext) {
        if (mappingContext.getSource() == null) {
            return Optional.empty();
        }

        MappingContext<?, ?> propertyContext = mappingContext.create(
                mappingContext.getSource(), MappingContextHelper.resolveDestinationGenericType(mappingContext));
        Object destination = mappingContext.getMappingEngine().map(propertyContext);
        return Optional.ofNullable(destination);
    }
}
