package arti.parkiet.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;
import org.bson.types.ObjectId;

import java.util.List;

@Serdeable
@MappedEntity
@Introspected
public class PublicKeys {

        @Id
        @Nullable
        private ObjectId id;

        private String key;
        private String email;

        @MappedProperty("import_logs")
        @Nullable
        private List<ImportLog> importLogs;

        public PublicKeys() {
        }

        @Nullable
        public ObjectId getId() {
                return id;
        }

        public void setId(@Nullable ObjectId id) {
                this.id = id;
        }

        public String getKey() {
                return key;
        }

        public void setKey(String key) {
                this.key = key;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        @Nullable
        public List<ImportLog> getImportLogs() {
                return importLogs;
        }

        public void setImportLogs(@Nullable List<ImportLog> importLogs) {
                this.importLogs = importLogs;
        }
}