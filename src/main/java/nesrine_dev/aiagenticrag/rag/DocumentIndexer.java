package nesrine_dev.aiagenticrag.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DocumentIndexer {
    @Value("classpath:/pdfs/cv.pdf")
    private Resource pdfResource;
    @Value("store.json")
    private String fileStore;
    private SimpleVectorStore vectorStore;

    public DocumentIndexer(SimpleVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void loadFile(MultipartFile pdfFile) throws IOException {
        Path path = Paths.get("src", "main", "resources", "store");
//        File file = new File(path.toFile().getAbsolutePath(), fileStore);
        File storeDir = path.toFile();
        storeDir.mkdirs();
        File file = new File(storeDir.getAbsolutePath(), fileStore);

        if (file.exists()) {
            vectorStore.load(file);
        }

        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
        List<Document> documents = pdfDocumentReader.read();
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> chunks = textSplitter.apply(documents);
        vectorStore.add(chunks);
        vectorStore.save(file);
    }
}
