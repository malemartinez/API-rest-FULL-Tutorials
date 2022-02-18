package com.books.integrate.spring.react.controller;

import java.util.*;

import com.books.integrate.spring.react.model.Tutorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.books.integrate.spring.react.repository.TutorialRepository;

//@CrossOrigin(origins = "http://localhost:8091")
@RestController
@RequestMapping("/api")
public class TutorialController {

	@Autowired
	TutorialRepository tutorialRepository;

	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
		try {
			List<Tutorial> tutorials = new ArrayList<Tutorial>();

			if (title == null)
				tutorialRepository.findAll().forEach(tutorials::add);
			else
				tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}


	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
		try {
			Tutorial _tutorial = tutorialRepository
					.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(),  tutorial.isPublished() ,tutorial.getPrice()));
			return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			Tutorial _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());
			return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

//HttpStatus
	@DeleteMapping( path = "/tutorials/{id}")
	public ResponseEntity<String> deleteTutorial(@PathVariable("id") long id) {
		try {
			tutorialRepository.deleteById(id);
				return new ResponseEntity<>("Tutorials DELETE!! ",HttpStatus.NO_CONTENT);
			} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@DeleteMapping("/tutorials/delete")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			tutorialRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}

	}

	@GetMapping("/tutorials/published")
	public ResponseEntity<List<Tutorial>> findByPublished() {
		try {
			List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	//Eliminar por titulo
	@DeleteMapping( path= "/tutorials/delete/{title}")
	public ResponseEntity<String> deleteTutorialByTitle(@PathVariable("title") String title ) {
		try {
			List<Tutorial> tutorial = this.tutorialRepository.findByTitleContaining(title);
			Tutorial tutorialObject = tutorial.get(0);
			tutorialRepository.delete(tutorialObject);

			return new ResponseEntity<>("Tutorials DELETE!! ",HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	//Modificando el titulo dando el id
	@PatchMapping(path = "/{id}")
	public ResponseEntity<String> ActualizarTitulo(@RequestParam("title")  String title, @PathVariable("id") Long id){
		List<Tutorial> tutorial = this.tutorialRepository.findByTitleContaining(title);
		Tutorial tutorialObject = tutorial.get(0);
		tutorialObject.setTitle(title);
		tutorialRepository.save(tutorialObject);
		return new ResponseEntity<>( "Titulo Modificado", HttpStatus.OK);
	}
	//Actualizar por titulo

	@PutMapping(path = "/update/{title}")
	public ResponseEntity<String> ActualizarTitulo(@PathVariable("title") String title, @RequestBody Tutorial tutorial){
		List<Tutorial> _tutoria = this.tutorialRepository.findByTitleContaining(title);
		Tutorial tutorialObject = _tutoria.get(0);
		tutorialObject.setTitle(tutorial.getTitle());
		tutorialObject.setDescription(tutorial.getDescription());
		tutorialObject.setPublished(tutorial.isPublished());
		tutorialObject.setPrice(tutorial.getPrice());
		tutorialRepository.save(tutorialObject);
		return new ResponseEntity<>( "Titulo Actualizado", HttpStatus.OK);
	}
	//consultar por precio

	@GetMapping("/tutorials/prices{price}")
	public ResponseEntity<List<Tutorial>> consultarPrecios(@PathVariable("price") double price){
		List<Tutorial> tutorialPrice = tutorialRepository.findByPrice(price);
		if(tutorialPrice.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		}
		return new ResponseEntity<>(tutorialPrice,HttpStatus.OK);


	}

}
