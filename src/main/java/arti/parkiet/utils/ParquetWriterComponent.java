package arti.parkiet.utils;

import arti.parkiet.domain.PublicKeys;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Singleton
public class ParquetWriterComponent {

    @Value("${storage.export.compression:SNAPPY}") // SNAPPY jako domyślny, jeśli zapomnisz wpisu
    protected String compressionType;
    private static final Logger LOG = LoggerFactory.getLogger(ParquetWriterComponent.class);

    // Metoda pomocnicza do mapowania String -> Codec
    private CompressionCodecName getCodec() {
        try {
            CompressionCodecName codec = CompressionCodecName.valueOf(compressionType.toUpperCase());
            LOG.info(">>> Parquet: Wybrano kodek: {}", codec);
            return codec;
        } catch (IllegalArgumentException | NullPointerException e) {
            LOG.warn(">>> Parquet: Błąd parsowania '{}'. Używam domyślnego SNAPPY.", compressionType);
            return CompressionCodecName.SNAPPY;
        }
    }

    public void write(String fullPath, List<PublicKeys> data) throws IOException {
        CompressionCodecName codecToUse = getCodec();
        LOG.info("Rozpoczynam proces zapisu. Kodek: {}, Ścieżka: {}", codecToUse, fullPath);

        Path path = new Path(fullPath);
        ReflectData reflectData = ReflectData.AllowNull.get();
        Schema schema = reflectData.getSchema(PublicKeys.class);

        Configuration conf = new Configuration();
        HadoopOutputFile outputFile = HadoopOutputFile.fromPath(path, conf);

        long startTime = System.nanoTime(); // Dokładniejszy pomiar dla małych zbiorów

        try (ParquetWriter<PublicKeys> writer = AvroParquetWriter.<PublicKeys>builder(outputFile)
                .withSchema(schema)
                .withDataModel(reflectData)
                .withConf(conf)
                .withCompressionCodec(codecToUse)
                .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
                .build()) {

            if (data.isEmpty()) {
                LOG.warn("Lista danych jest pusta! Zapisuję tylko schemat.");
            }

            for (PublicKeys record : data) {
                writer.write(record);
            }

            // Writer zamknie się tutaj automatycznie (zrzucając dane z bufora na dysk)
        } catch (IOException e) {
            LOG.error("Błąd podczas zapisu Parquet", e);
            throw e;
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000; // Konwersja na milisekundy

        // Pobranie rozmiaru pliku po jego fizycznym zamknięciu
        java.io.File fileOnDisk = new java.io.File(fullPath);
        long fileSize = fileOnDisk.exists() ? fileOnDisk.length() : 0;

        LOG.info("=== RAPORT OPERACJI ===");
        LOG.info("Status: ZAKOŃCZONO");
        LOG.info("Rekordy: {}", data.size());
        LOG.info("Rozmiar pliku: {} bajtów", fileSize);
        LOG.info("Czas operacji: {} ms", durationMs);
        LOG.info("Wykorzystany kodek: {}", codecToUse);
        LOG.info("=======================");
    }

    public void setCompressionType(String compressionType) {
        this.LOG.info(">>> Zmiana kodeka na: {}", compressionType);
        this.compressionType = compressionType;
    }
}