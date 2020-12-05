package dk.softbois.notificationproducer.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@RequiredArgsConstructor
@Document
public class Phone {
    @Id
    @Getter
    String id;

    @Getter
    @Indexed
    List<String> flightIds;
}
