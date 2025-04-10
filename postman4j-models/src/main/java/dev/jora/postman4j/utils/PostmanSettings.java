package dev.jora.postman4j.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostmanSettings {
    public static final String DEFAULT_COLLECTION_NAME = "Generated Collection";
    public static final String DEFAULT_COLLECTION_DESCRIPTION = "Generated Collection Description";

    /**
     * The default name of the collection
     * Used when the collection name is not provided by annotation
     */
    @lombok.Builder.Default
    private final String baseCollectionName = DEFAULT_COLLECTION_NAME;

    /**
     * The default description of the collection.
     * Used when the collection description is not provided by annotation
     */
    @lombok.Builder.Default
    private final String baseCollectionDescription = DEFAULT_COLLECTION_DESCRIPTION;

    /**
     * The schema version of the collection
     */
    @lombok.Builder.Default
    private final SchemaVersion schemaVersion = SchemaVersion.V2_1_0;

    /**
     * The mode of the request-response logging: request, response or both
     */
    @lombok.Builder.Default
    private final RequestResponseMode requestResponseMode = RequestResponseMode.REQUEST_AND_RESPONSE;

    /**
     * Whether to log the request body
     */
    @lombok.Builder.Default
    private final boolean enableRequestBody = true;

    /**
     * Whether to log the response body
     */
    @lombok.Builder.Default
    private final boolean enableResponseBody = true;

    /**
     * Enable logging of current collection to stdout when in changed
     */
    @lombok.Builder.Default
    private final boolean logWhenChanged = false;

    /**
     * The naming strategy for the items
     */
    @lombok.Builder.Default
    private final ItemNamingStrategy itemNamingStrategy = ItemNamingStrategy.COUNTER;

    /**
     * The custom item name generator function
     * Use request URI as input parameter
     * @return the request item name
     */
    private UnaryOperator<String> itemNameGenerator = null;

    /**
     * The output location for the collection
     */
    @lombok.Builder.Default
    private final OutputLocation outputLocation = OutputLocation.CONSOLE;

    /**
     * The selected statuses to save when filtering responses
     */
    @lombok.Builder.Default
    private final List<Integer> selectedStatuses = new ArrayList<>();

    /**
     * The selected headers to save when filtering responses
     */
    @lombok.Builder.Default
    private final List<String> selectedHeaders = new ArrayList<>();

    /**
     * The selected exceptions to save when filtering responses
     */
    @lombok.Builder.Default
    private final List<String> selectedExceptions = new ArrayList<>();

    /**
     * The header name to use for the request name
     */
    @lombok.Builder.Default
    private final String headerName = null;

    /**
     * The custom status filter function to use when filtering responses, receives the status code as an argument
     */
    @lombok.Builder.Default
    private final Predicate<Integer> customStatusFilter = null;

    /**
     * The folding strategy to use when folding items
     */
    @lombok.Builder.Default
    private final FoldingStrategy foldingStrategy = FoldingStrategy.NO_FOLDERS;

    /**
     * Whether to disable the interceptor when actuator endpoints are called
     */
    @lombok.Builder.Default
    private final boolean disableOnActuator = true;

    // TODO: contextId

    public boolean shouldSaveAll() {
        return selectedStatuses.isEmpty() && selectedHeaders.isEmpty() && selectedExceptions.isEmpty();
    }
}
