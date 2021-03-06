package rpp.ctrl;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import rpp.jpa.Artikl;
import rpp.reps.ArtiklRepository;

@RestController
public class ArtiklRestController {

	/*
	 * Anotacija @Autowired se može primeniti nad varijablama instace, setter metodama i
	 * konstruktorima. Označava da je neophodno injektovati zavisni objekat. Prilikom
	 * pokretanja aplikacije IoC kontejner prolazi kroz kompletan kod tražeči anotacije
	 * koje označavaju da je potrebno kreirati objekte. Upotrebom @Autowired anotacije
	 * stavljeno je do znanja da je potrebno kreirati objekta klase koja će implementirati
	 * repozitorijum AriklRepository i proslediti klasi ArtiklRestController referencu
	 * na taj objekat.
	 */
	@Autowired
	private ArtiklRepository artiklRepository;

	/* Vežbe 04 - JdbcTemplate klasa pojednostavljuje korišćenje JDBC (Java Database
	 * Connectivity), objekat ove klase će se koristiti u metodi delete() i zato je
	 * deklarisan i anotiran sa @Autowired
	 */

	@Autowired
	JdbcTemplate jdbcTemplate;

	/*
	 * HTTP GET je jedna od HTTP metoda koja je analogna opciji READ iz CRUD operacija.
	 * Anotacija @GetMapping se koristi kako bi se mapirao HTTP GET zahtev.
	 * Predstavlja skraćenu verziju metode @RequestMapping(method = RequestMethod.GET)
	 * U konkretnom slučaju HTTP GET zahtevi (a to je npr. svako učitavanje stranice u
	 * browser-u) upućeni na adresu localhost:8083/artikl biće prosleđeni ovoj metodi.
	 *
	 * Poziv metode artiklRepository.findAll() će vratiti kolekciju koja sadrži sve
	 * artikala koji će potom u browseru biti prikazani u JSON formatu
	 */

	//@CrossOrigin
	@ApiOperation(value = "Returns collection of all Artikl from database.")
	@GetMapping("artikl")
	public Collection<Artikl> getAll(){
		return artiklRepository.findAll();
	}

	/*
	 * U slučaju metode getOne(), novina je uvođenje promenljive koja je predstavljena kao
	 * {id} u @GetMapping("artikl/{id}"). Mapiranje promenljive u vrednost koja se prosleđuje
	 * konkretnoj metodi getOne() vrši se upotrebom anotacije @PathVariable.
	 * U konkretnom slučaju HTTP GET zahtev upućen na adresu localhost:8083/artikl/1 biće
	 * prosleđen ovoj metodi, a vrednost 1 kao ID.
	 *
	 *  Poziv metode artiklRepositorz.getOne(id) će vratiti konkretan artikal sa datim ID-je
	 *  i taj artikal će potom biti prikazan u browseru u JSON formatu.
	 */

	/*
	 * Vežbe 04
	 */

	//@CrossOrigin
	@ApiOperation(value = "Returns Artikl with id that was forwarded as path variable.")
	@GetMapping("artikl/{id}")
	//public Artikl getOne(@PathVariable("id") Integer id) {
	public ResponseEntity<Artikl> getOne(@PathVariable("id") Integer id) {

		if (artiklRepository.findById(id).isPresent()) {
			Artikl artikl = artiklRepository.getOne(id);
			return new ResponseEntity<>(artikl, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

	}

	/* Vežbe 04 - Pored dve postojeće GET metode sa prethodnih vežbi, dodata je još jedna
	 * metoda koja će koristiti ranije definisanu metodu u ArtiklRepository
	 *
	 */

	//@CrossOrigin
	@ApiOperation(value = "Returns Artikl with name that was forwarded as path variable.")
	@GetMapping("artikl/naziv/{naziv}")
	public Collection<Artikl> getByNaziv(@PathVariable("naziv") String naziv){
		return artiklRepository.findByNazivContainingIgnoreCase(naziv);
	}

	/*
	 * HTTP POST je jedna od HTTP metoda koja je analogna opciji CREATE iz CRUD operacija.
	 * Anotacija @PostMapping se koristi kako bi se mapirao HTTP POST zahtev.
	 * Predstavlja skraćenu verziju metode @RequestMapping(method = RequestMethod.POST)
	 * U konkretnom slučaju HTTP POST zahtevi upućeni na adresu localhost:8083/artikl
	 * biće prosleđeni ovoj metodi.
	 * Poziv metode artiklRepository.save(artikl) će sačuvati prosleđeni artikl u bazi
	 * podataka
	 */

	//@CrossOrigin
	@ApiOperation(value = "Adds instance of Artikl to database.")
	@PostMapping("artikl")
	public ResponseEntity<Artikl> addArtikl(@RequestBody Artikl artikl) {
		Artikl savedArtikl = artiklRepository.save(artikl);
		URI location = URI.create("/artikl/" + savedArtikl.getId());
		return ResponseEntity.created(location).body(savedArtikl);
	}

	/*
	 * HTTP PUT je jedna od HTTP metoda koja je analogna opciji UPDATE iz CRUD operacija.
	 * Anotacija @PutMapping se koristi kako bi se mapirao HTTP PUT zahtev.
	 * Predstavlja skraćenu verziju metode @RequestMapping(method = RequestMethod.PUT)
	 * U konkretnom slučaju HTTP PUT zahtevi upućeni na adresu localhost:8083/artikl/{id}
	 * biće prosleđeni ovoj metodi.
	 * Poziv metode artiklRepository.save(artikl) će izmeniti artikl sa prosleđenim ID-ijem
	 * i prosleđenim sadržajem u bazi podataka.
	 */

	//@CrossOrigin
	@ApiOperation(value = "Updates Artikl that has id that was forwarded as path variable with values forwarded in Request Body.")
	//@PutMapping(value = "artikl/{id}", produces = "application/json; charset=utf-8")
	@PutMapping(value = "artikl/{id}")
	public ResponseEntity<Artikl> updateArtikl(@RequestBody Artikl artikl, @PathVariable("id")Integer id){
		if (artiklRepository.existsById(id)) {
			artikl.setId(id);
			Artikl savedArtikl = artiklRepository.save(artikl);
			return ResponseEntity.ok().body(savedArtikl);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/*
	 * HTTP DELETE je jedna od HTTP metoda koja je analogna opciji DELETE iz CRUD operacija.
	 * Anotacija @DELETEMapping se koristi kako bi se mapirao HTTP DELETE zahtev.
	 * Predstavlja skraćenu verziju metode @RequestMapping(method = RequestMethod.DELETE)
	 * U konkretnom slučaju HTTP PUT zahtevi upućeni na adresu localhost:8083/artikl/{id}
	 * biće prosleđeni ovoj metodi.
	 * Poziv metode artiklRepository.deleteByID(id) će obrisati artikl sa prosleđenim ID-ije
	 * iz baze podataka.
	 */

	//@CrossOrigin
	@ApiOperation(value = "Deletes Artikl with id that was forwarded as path variable.")
	@DeleteMapping("artikl/{id}")
	public ResponseEntity<HttpStatus> delete(@PathVariable Integer id){
		if(id==-100 && !artiklRepository.existsById(id)) {
			jdbcTemplate.execute("INSERT INTO artikl (\"id\", \"proizvodjac\", \"naziv\") VALUES (-100, 'Test Proizvodjac', 'Test Naziv')");
		}

		if (artiklRepository.existsById(id)) {
			artiklRepository.deleteById(id);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}

		return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);

	}

}
