import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSave() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("This is a test content")
                .author(DocumentManager.Author.builder().id("1").name("John Doe").build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
        assertEquals(document.getTitle(), savedDocument.getTitle());
        assertEquals(document.getContent(), savedDocument.getContent());
        assertEquals(document.getAuthor(), savedDocument.getAuthor());
    }

    @Test
    void testSaveWithExistingId() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("existing-id")
                .title("Test Document")
                .content("This is a test content")
                .author(DocumentManager.Author.builder().id("1").name("John Doe").build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertEquals("existing-id", savedDocument.getId());
    }

    @Test
    void testFindById() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("This is a test content")
                .author(DocumentManager.Author.builder().id("1").name("John Doe").build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument, foundDocument.get());
    }

    @Test
    void testSearch() {
        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Java Programming")
                .content("Java is a popular programming language")
                .author(DocumentManager.Author.builder().id("1").name("John Doe").build())
                .created(Instant.parse("2023-01-01T00:00:00Z"))
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("Python Basics")
                .content("Python is easy to learn")
                .author(DocumentManager.Author.builder().id("2").name("Jane Smith").build())
                .created(Instant.parse("2023-02-01T00:00:00Z"))
                .build();

        doc1 = documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Java"))
                .containsContents(List.of("popular"))
                .authorIds(List.of("1"))
                .createdFrom(Instant.parse("2022-01-01T00:00:00Z"))
                .createdTo(Instant.parse("2023-12-31T23:59:59Z"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals(doc1, results.get(0));
    }

    @Test
    void testSearchNoResults() {
        DocumentManager.Document doc = DocumentManager.Document.builder()
                .title("Java Programming")
                .content("Java is a popular programming language")
                .author(DocumentManager.Author.builder().id("1").name("John Doe").build())
                .created(Instant.parse("2023-01-01T00:00:00Z"))
                .build();

        documentManager.save(doc);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Python"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertTrue(results.isEmpty());
    }
}