package com.example.SpringBootJoshLong.demo;

import org.hibernate.persister.entity.Loadable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
    CommandLineRunner runner(ReservationRepository rr){
        return (args)->{
            Arrays.asList("Les,Josh,Phil,Sasha,Peter".split(","))
                    .forEach(n->rr.save(new Reservation(n)));

            rr.findAll().forEach(System.out::println);

            rr.findByReservationName("Les").forEach(System.out::println);
        };
    }
}

@Entity
class Reservation {

    @Id
    @GeneratedValue
	private Long id;

	private String reservationName;

	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}

	public Reservation() {
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReservationName() {
		return reservationName;
	}

	@Override
	public String toString() {
		return "Reservations{" +
				"id=" + id +
				", reservationName='" + reservationName + '\'' +
				'}';
	}
}

@RepositoryRestResource
interface  ReservationRepository  extends JpaRepository<Reservation, Long>
{

    Collection<Reservation> findByReservationName(@Param("rn") String rn);
}

@Component
class ReservationResourceProcessor implements ResourceProcessor<Resource<Reservation>>{

    @Override
    public Resource<Reservation> process(Resource<Reservation> reservationResource) {
        reservationResource.add(new Link("http://s3.com/imgs/"+reservationResource.getContent().getId()+".jpg","profile-photo"));

        return reservationResource;
    }
}



@RestController
class ReservartionController{

    @RequestMapping("/reservations")
    Collection<Reservation> reservations(){
        return reservationRepository.findAll();

    }

    @Autowired
    private ReservationRepository reservationRepository;
}


@Controller
class ReservationMvcController{

    @RequestMapping("/reservations.php")
    String reservations (Model model){
        model.addAttribute("reservations",this.reservationRepository.findAll());
        return "reservations"; //src/main/resources/templates/ + $X +.html
    }

    @Autowired
    private ReservationRepository reservationRepository;
}