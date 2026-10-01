package com.empresa.backend.tarea;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TareaRequest(
		@NotBlank(message = "El titulo es obligatorio")
		@Size(max = 200, message = "El titulo no puede superar los 200 caracteres")
		String titulo,
		Boolean completada) {
}
