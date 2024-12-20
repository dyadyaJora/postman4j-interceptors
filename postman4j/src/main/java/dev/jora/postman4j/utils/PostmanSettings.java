package dev.jora.postman4j.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostmanSettings {
    public static final String DEFAULT_COLLECTION_NAME = "Generated Collection";
    public static final String DEFAULT_COLLECTION_DESCRIPTION = "Generated Collection Description";

    @lombok.Builder.Default
    private final String baseCollectionName = DEFAULT_COLLECTION_NAME;

    @lombok.Builder.Default
    private final String baseCollectionDescription = DEFAULT_COLLECTION_DESCRIPTION;

    @lombok.Builder.Default
    private final SchemaVersion schemaVersion = SchemaVersion.V2_1_0;

    @lombok.Builder.Default
    private final RequestResponseMode requestResponseMode = RequestResponseMode.REQUEST_AND_RESPONSE;

    @lombok.Builder.Default
    private final boolean enableRequestBody = true;

    @lombok.Builder.Default
    private final boolean enableResponseBody = true;

    @lombok.Builder.Default
    private final boolean logWhenChanged = true;

    @lombok.Builder.Default
    private final ItemNamingStrategy itemNamingStrategy = ItemNamingStrategy.COUNTER;

    @lombok.Builder.Default
    private final OutputLocation outputLocation = OutputLocation.CONSOLE;

    @lombok.Builder.Default
    private final List<Integer> selectedStatuses = new ArrayList<>();

    @lombok.Builder.Default
    private final List<String> selectedHeaders = new ArrayList<>();

    @lombok.Builder.Default
    private final List<String> selectedExceptions = new ArrayList<>();

    @lombok.Builder.Default
    private final String headerName = null;

    @lombok.Builder.Default
    private final Predicate<Integer> customStatusFilter = null;

    @lombok.Builder.Default
    private final FoldingStrategy foldingStrategy = FoldingStrategy.NO_FOLDERS;

    public boolean shouldSaveAll() {
        return selectedStatuses.isEmpty() && selectedHeaders.isEmpty() && selectedExceptions.isEmpty();
    }
}
