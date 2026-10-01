package com.empresa.backend.tarea;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

	private final TareaRepository repository;

	public TareaController(TareaRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<TareaResponse> listar() {
		return repository.findAllByOrderByCreadaEnDescIdDesc().stream().map(TareaResponse::desde).toList();
	}

	@GetMapping("/{id}")
	public TareaResponse obtener(@PathVariable Long id) {
		return TareaResponse.desde(buscar(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TareaResponse crear(@Valid @RequestBody TareaRequest peticion) {
		Tarea tarea = new Tarea();
		aplicar(tarea, peticion);
		return TareaResponse.desde(repository.save(tarea));
	}

	@PutMapping("/{id}")
	public TareaResponse actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequest peticion) {
		Tarea tarea = buscar(id);
		aplicar(tarea, peticion);
		return TareaResponse.desde(repository.save(tarea));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void borrar(@PathVariable Long id) {
		repository.delete(buscar(id));
	}

	private Tarea buscar(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea " + id + " no encontrada"));
	}

	private void aplicar(Tarea tarea, TareaRequest peticion) {
		tarea.setTitulo(peticion.titulo().trim());
		if (peticion.completada() != null) {
			tarea.setCompletada(peticion.completada());
		}
	}
}
