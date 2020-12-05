package dk.softbois.notificationproducer;


import dk.softbois.notificationproducer.models.Phone;
import dk.softbois.notificationproducer.rabbitmq.RabbitMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RepositoryRestResource
@ResponseBody
@RequestMapping("/notifications")
@Configurable
public class PhoneController {
    private static RabbitMQ rabbitMQ;

    @Autowired
    private PhoneRepo repo;
    private Object Phone;


    @PostConstruct
    public void PostConstruct() {
        rabbitMQ = new RabbitMQ(repo.findAll());
    }

    @GetMapping("/")
    public List<Phone> retrieveAllPhones()
    {
        return (List<Phone>)repo.findAll();
    }



    @PostMapping("/{flightNumber}")
    public ResponseEntity sendFlightUpdates(@PathVariable String flightNumber, @RequestBody String message)
    {
        //Find phones with given flightNumber
        List<Phone> phones = repo.findByFlightNumber(flightNumber);

        if (phones == null || phones.isEmpty())
        {
            return new ResponseEntity("Flight not found", HttpStatus.NOT_FOUND);
        }

        //Get all phoneIds into list
        List<String> phoneIds = new ArrayList<>();
        for (var phone: phones) {
            phoneIds.add(phone.getId());
        }

        //RabbitMQ send notification to queues based on phoneIds
        rabbitMQ.sendFlightUpdate(phoneIds, message);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/register-phone")
    private ResponseEntity registerPhone(@RequestBody Phone phone)
    {
        var res = repo.save(phone);
        rabbitMQ.addQueue(phone.getId());
        return new ResponseEntity(res, HttpStatus.OK);
    }

    @PostMapping("/send-notification")
    public ResponseEntity sendNotificationsMessage(@RequestBody String message){
        rabbitMQ.broadcastNotification(message);
        return new ResponseEntity("Notification pushed", HttpStatus.OK);
    }
}
