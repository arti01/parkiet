package arti.parkiet.service;

import arti.parkiet.domain.PublicKeys;
import arti.parkiet.repository.PublicKeysRepository;
import arti.parkiet.utils.ParquetWriterComponent;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.StreamSupport;

@Singleton
public class ExportService {

    private final PublicKeysRepository repository;
    private final ParquetWriterComponent parquetWriter;


    @Value("${storage.export.path}")
    protected String exportBasePath;

    public ExportService(PublicKeysRepository repository, ParquetWriterComponent parquetWriter) {
        this.repository = repository;
        this.parquetWriter = parquetWriter;
    }

    public String exportToDisk() {
        List<PublicKeys> keys = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .toList();
        return processExport(keys, "full");
    }

    public String exportRangeToDisk(String startId, String endId) {
        if (startId == null || endId == null) {
            return exportToDisk();
        }

        ObjectId start = new ObjectId(startId);
        ObjectId end = new ObjectId(endId);
        List<PublicKeys> keys = repository.findByIdBetween(start, end);

        return processExport(keys, "range_" + startId + "_to_" + endId);
    }

    // Wspólna metoda zajmująca się logiką zapisu
    private String processExport(List<PublicKeys> keys, String contextInfo) {
        String fileName = "export_" + System.currentTimeMillis() + ".parquet";
        String fullPath = exportBasePath + "/" + fileName;

        try {
            parquetWriter.write(fullPath, keys); // TU DZIEJE SIĘ MAGIA
            System.out.println("Plik fizycznie zapisany: " + fullPath);
        } catch (Exception e) {
            System.err.println("BŁĄD ZAPISU: " + e.getMessage());
            e.printStackTrace();
            return "Błąd zapisu pliku!";
        }

        return fullPath;
    }
}