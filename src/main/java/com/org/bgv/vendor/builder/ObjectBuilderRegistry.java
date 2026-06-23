package com.org.bgv.vendor.builder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.org.bgv.dto.CheckCategoryEnum;

@Component
public class ObjectBuilderRegistry {

    private final Map<CheckCategoryEnum,
            ObjectBuilderStrategy> strategies;

    public ObjectBuilderRegistry(
            List<ObjectBuilderStrategy> builders) {

        this.strategies =
                builders.stream()
                        .collect(Collectors.toMap(
                                ObjectBuilderStrategy::supportedCategory,
                                Function.identity()));
    }

    public ObjectBuilderStrategy getStrategy(
            CheckCategoryEnum category) {

        ObjectBuilderStrategy strategy =
                strategies.get(category);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Unsupported category : " + category);
        }

        return strategy;
    }
}