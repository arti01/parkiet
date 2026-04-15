package arti.parkiet.repository;

import arti.parkiet.domain.PublicKeys;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;
import org.bson.types.ObjectId;

import java.util.List;

@MongoRepository
public interface PublicKeysRepository extends CrudRepository<PublicKeys, ObjectId> {
    List<PublicKeys> findByIdBetween(ObjectId startId, ObjectId endId);
}
