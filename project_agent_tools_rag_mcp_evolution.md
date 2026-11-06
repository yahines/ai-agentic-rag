---
name: agent-tools-evolution
description: Propositions d'évolution — AgentTools avec repository JPA + web service upload PDF pour le RAG
metadata:
  type: project
---

## 1. AgentTools — connexion à un vrai repository JPA

**Why:** Actuellement les données (salary=65000, seniority=10) sont hardcodées — à remplacer quand une BDD sera en place.

**How to apply:** Implémenter quand l'utilisateur veut connecter AgentTools à une vraie source de données.

```java
@Component
@RequiredArgsConstructor
public class AgentTools {

    private final EmployeeRepository employeeRepository;

    @Tool(description = "Get infos about an employee")
    public EmployeeInfo getEmployeeInfo(
            @ToolParam(description = "Employee Name") String employeeName) {
        return employeeRepository.findByName(employeeName)
                .map(e -> new EmployeeInfo(e.getName(), e.getSalary(), e.getSeniority()))
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeName));
    }
}
```

Nécessite :
- `EmployeeRepository extends JpaRepository<Employee, Long>` avec méthode `findByName(String name)`
- Entité `Employee` JPA
- H2 déjà configuré dans le projet

---

## 2. Web service — upload PDF et indexation dans le RAG

**Why:** Permettre à l'utilisateur de télécharger un PDF dynamiquement et l'utiliser dans le RAG, sans redémarrer l'application.

**How to apply:** Créer `DocumentController` et `DocumentService` dans le projet.

### DocumentController.java
```java
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".pdf")) {
            return ResponseEntity.badRequest().body("Fichier PDF requis");
        }
        documentService.indexPdf(file);
        return ResponseEntity.ok("Document indexé avec succès : " + file.getOriginalFilename());
    }
}
```

### DocumentService.java
```java
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final SimpleVectorStore vectorStore;

    public void indexPdf(MultipartFile file) {
        try {
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            List<Document> chunks = new TokenTextSplitter()
                    .apply(new PagePdfDocumentReader(resource).read());
            vectorStore.add(chunks);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'indexation du PDF", e);
        }
    }
}
```

### Test curl
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@monDocument.pdf"
```

### Flux
1. `POST /api/documents/upload` avec le fichier en `multipart/form-data`
2. PDF lu page par page → découpé en chunks → embeddings Ollama
3. Chunks ajoutés au `SimpleVectorStore` existant (déjà chargé avec `cv.pdf` au démarrage)
4. L'agent RAG répond sur le nouveau document immédiatement
