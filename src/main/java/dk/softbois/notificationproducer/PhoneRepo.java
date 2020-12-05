package dk.softbois.notificationproducer;

import dk.softbois.notificationproducer.models.Phone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface PhoneRepo extends MongoRepository<Phone, String> {

    @Query(value = "{ flightIds:{$in: [\"?0\"]}}")
    List<Phone> findByFlightNumber(String flight_number);
}
