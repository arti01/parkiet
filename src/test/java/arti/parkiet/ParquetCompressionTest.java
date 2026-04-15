package arti.parkiet;

import arti.parkiet.domain.ImportLog;
import arti.parkiet.domain.PublicKeys;
import arti.parkiet.utils.ParquetWriterComponent;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@MicronautTest
class ParquetCompressionTest {

    @Inject
    ParquetWriterComponent writerComponent;

    @Test
    void runCompressionRace() throws IOException {
        int recordCount = 50000;
        List<PublicKeys> data = generateMockData(recordCount);

        String[] codecs = {"UNCOMPRESSED", "SNAPPY", "GZIP", "ZSTD"};

        System.out.println("\n=== WYŚCIG ALGORYTMÓW (Rekordy: " + recordCount + ") ===");

        for (String codec : codecs) {
            String path = "/home/arti01/tmp/test_" + codec.toLowerCase() + ".parquet";

            // Teraz używamy publicznego settera
            writerComponent.setCompressionType(codec);

            writerComponent.write(path, data);
        }
    }

    private List<PublicKeys> generateMockData(int count) {
        List<PublicKeys> list = new ArrayList<>();
        String[] providers = {"gmail.com", "onet.eu", "yahoo.com"};

        for (int i = 0; i < count; i++) {
            PublicKeys pk = new PublicKeys();
            pk.setEmail("tester_" + i + "@" + providers[i % 3]);
            pk.setKey("PGP-KEY-DATA-MOCK-" + UUID.randomUUID());
            pk.setId(new ObjectId());

            List<ImportLog> logs = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                ImportLog log = new ImportLog();
                log.setMessage("Status check for record " + i);
                log.setStatus(j % 2 == 0 ? "SUCCESS" : "RETRY");
                log.setCreatedAt("2026-04-15T12:00:00");
                logs.add(log);
            }
            pk.setImportLogs(logs);
            list.add(pk);
        }
        return list;
    }
}