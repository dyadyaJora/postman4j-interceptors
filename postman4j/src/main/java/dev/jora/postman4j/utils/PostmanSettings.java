package dev.jora.postman4j.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostmanSettings {
    @lombok.Builder.Default
    private boolean all = true;

    @lombok.Builder.Default
    private String baseCollectionName = "Generated Collection";

    @lombok.Builder.Default
    private String baseCollectionDescription = "Generated Collection Description";

    @lombok.Builder.Default
    private SchemaVersion schemaVersion = SchemaVersion.V2_1_0;

    @lombok.Builder.Default
    private ItemNamingStrategy itemNamingStrategy = ItemNamingStrategy.COUNTER;

    @lombok.Builder.Default
    private OutputLocation outputLocation = OutputLocation.CONSOLE;

    @lombok.Builder.Default
    private List<Integer> selectedStatuses = new ArrayList<>();

    @lombok.Builder.Default
    private List<String> selectedHeaders = new ArrayList<>();

    @lombok.Builder.Default
    private List<String> selectedExceptions = new ArrayList<>();

    @lombok.Builder.Default
    private String headerWithRequestName = null;
}
