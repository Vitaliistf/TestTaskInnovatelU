import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The DocumentManager class manages the storage and retrieval of documents in memory.
 * It provides methods to save, search, and find documents by their ID.
 */
public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    /**
     * Saves a document to the storage. If the document does not have an ID, a unique one will be generated.
     *
     * @param document The document to be saved.
     * @return The saved document with a unique ID if it was generated.
     */
    public Document save(Document document) {
        Document documentToSave = Document.builder()
                .id(document.getId() != null ? document.getId() : generateId())
                .title(document.getTitle())
                .content(document.getContent())
                .author(document.getAuthor())
                .created(document.getCreated() != null ? document.getCreated() : Instant.now())
                .build();

        storage.put(documentToSave.getId(), documentToSave);
        return documentToSave;
    }

    /**
     * Generates a unique ID for a document.
     *
     * @return A unique ID as a string.
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Searches for documents that match the criteria specified in the search request.
     *
     * @param request The search request containing search criteria.
     * @return A list of documents that match the search criteria.
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(doc -> matchesSearchCriteria(doc, request))
                .collect(Collectors.toList());
    }

    /**
     * Checks if a document matches the search criteria.
     *
     * @param doc The document to be checked.
     * @param request The search request containing the criteria.
     * @return true if the document matches the criteria, false otherwise.
     */
    private boolean matchesSearchCriteria(Document doc, SearchRequest request) {
        return matchesTitlePrefixes(doc, request.getTitlePrefixes())
                && matchesContainsContents(doc, request.getContainsContents())
                && matchesAuthorIds(doc, request.getAuthorIds())
                && isWithinDateRange(doc, request.getCreatedFrom(), request.getCreatedTo());
    }

    /**
     * Checks if a document's title matches any of the specified prefixes.
     *
     * @param doc The document to be checked.
     * @param titlePrefixes The list of title prefixes.
     * @return true if the document's title matches any prefix, false otherwise.
     */
    private boolean matchesTitlePrefixes(Document doc, List<String> titlePrefixes) {
        return titlePrefixes == null || titlePrefixes.isEmpty() ||
                titlePrefixes.stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix));
    }

    /**
     * Checks if a document's content contains all the specified contents.
     *
     * @param doc The document to be checked.
     * @param containsContents The list of contents to check for.
     * @return true if the document's content contains all specified contents, false otherwise.
     */
    private boolean matchesContainsContents(Document doc, List<String> containsContents) {
        return containsContents == null || containsContents.isEmpty() ||
                containsContents.stream().allMatch(content -> doc.getContent().contains(content));
    }

    /**
     * Checks if a document's author ID matches any of the specified author IDs.
     *
     * @param doc The document to be checked.
     * @param authorIds The list of author IDs.
     * @return true if the document's author ID matches any specified ID, false otherwise.
     */
    private boolean matchesAuthorIds(Document doc, List<String> authorIds) {
        return authorIds == null || authorIds.isEmpty() ||
                authorIds.contains(doc.getAuthor().getId());
    }

    /**
     * Checks if a document's creation date is within the specified date range.
     *
     * @param doc The document to be checked.
     * @param createdFrom The start date of the range.
     * @param createdTo The end date of the range.
     * @return true if the document's creation date is within the range, false otherwise.
     */
    private boolean isWithinDateRange(Document doc, Instant createdFrom, Instant createdTo) {
        return (createdFrom == null || !doc.getCreated().isBefore(createdFrom)) &&
                (createdTo == null || !doc.getCreated().isAfter(createdTo));
    }

    /**
     * Finds a document by its ID.
     *
     * @param id The ID of the document to be found.
     * @return An Optional containing the document if found, or empty if not.
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * SearchRequest class represents the search criteria for searching documents.
     * It includes title prefixes, contents to search for, author IDs, and date range.
     */
    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    /**
     * Document class represents a document with an ID, title, content, author, and creation date.
     */
    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    /**
     * Author class represents an author with an ID and name.
     */
    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}