package com.empresa.backend.tarea;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TareaControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	TareaRepository repository;

	@BeforeEach
	void limpiar() {
		repository.deleteAll();
	}

	private String crear(String json) throws Exception {
		return mvc.perform(post("/api/tareas").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
	}

	@Test
	void crearYListar() throws Exception {
		crear("{\"titulo\":\"Montar el CI\"}");

		mvc.perform(get("/api/tareas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].titulo").value("Montar el CI"))
				.andExpect(jsonPath("$[0].completada").value(false))
				.andExpect(jsonPath("$[0].creadaEn").exists());
	}

	@Test
	void fechaDeCreacionSeGuardaEnUtc() throws Exception {
		// La JVM va en Tokio (UTC+9) durante todo el ciclo: la fecha guardada debe seguir siendo UTC
		java.util.TimeZone original = java.util.TimeZone.getDefault();
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Tokyo"));
		try {
			crear("{\"titulo\":\"Zona horaria\"}");
			java.time.LocalDateTime creada = repository.findAll().get(0).getCreadaEn();
			java.time.LocalDateTime ahoraUtc = java.time.LocalDateTime.now(java.time.ZoneOffset.UTC);
			org.junit.jupiter.api.Assertions.assertTrue(
					java.time.Duration.between(creada, ahoraUtc).abs().toMinutes() < 1,
					"creadaEn debe estar en UTC, pero es " + creada);
		} finally {
			java.util.TimeZone.setDefault(original);
		}
	}

	@Test
	void rechazaTituloVacio() throws Exception {
		mvc.perform(post("/api/tareas").contentType(MediaType.APPLICATION_JSON).content("{\"titulo\":\"   \"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void rechazaTituloDemasiadoLargo() throws Exception {
		String largo = "x".repeat(201);
		mvc.perform(post("/api/tareas").contentType(MediaType.APPLICATION_JSON)
				.content("{\"titulo\":\"" + largo + "\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void actualizarMarcaComoCompletada() throws Exception {
		crear("{\"titulo\":\"Escribir tests\"}");
		long id = repository.findAll().get(0).getId();

		mvc.perform(put("/api/tareas/" + id).contentType(MediaType.APPLICATION_JSON)
				.content("{\"titulo\":\"Escribir tests\",\"completada\":true}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.completada").value(true));

		mvc.perform(get("/api/tareas/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.completada").value(true));
	}

	@Test
	void borrarYNoEncontrada() throws Exception {
		crear("{\"titulo\":\"Temporal\"}");
		long id = repository.findAll().get(0).getId();

		mvc.perform(delete("/api/tareas/" + id)).andExpect(status().isNoContent());
		mvc.perform(get("/api/tareas/" + id)).andExpect(status().isNotFound());
		mvc.perform(delete("/api/tareas/" + id)).andExpect(status().isNotFound());
	}
}
