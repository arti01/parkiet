package arti.parkiet.controller;

import arti.parkiet.service.ExportService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import io.micronaut.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@Controller("/export")
public class ExportController {

    private static final Logger LOG = LoggerFactory.getLogger(ExportController.class);
    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @Get("/zapisz")
    public String export(
            @QueryValue @Nullable String startId,
            @QueryValue @Nullable String endId
    ) {
        return exportService.exportRangeToDisk(startId, endId);
    }

    @Get("/pobierz")
    public HttpResponse<StreamedFile> download (
            @QueryValue @Nullable String startId,
            @QueryValue @Nullable String endId
    ) throws IOException {
        String filePath = exportService.exportRangeToDisk(startId, endId);
        File file = new File(filePath);

        if (!file.exists()) {
            return HttpResponse.notFound();
        }

        // Tworzymy strumień z anonimową nadpisaniem metody close()
        // To zadziała na 100%, bo każdy serwer (Netty) musi zamknąć strumień po wysłaniu pliku.
        InputStream inputStream = new FileInputStream(file) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    // To wykona się zawsze, nawet jeśli zamknięcie strumienia rzuci błędem
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        LOG.info("Sprzątanie po eksporcie: Plik {} usunięty: {}", file.getName(), deleted);
                    }
                }
            }
        };

        return HttpResponse.ok(new StreamedFile(inputStream, MediaType.of("application/octet-stream"))
                .attach(file.getName()));
    }
}
