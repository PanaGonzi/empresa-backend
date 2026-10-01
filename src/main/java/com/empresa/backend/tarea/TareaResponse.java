package com.empresa.backend.tarea;

import java.time.LocalDateTime;

public record TareaResponse(Long id, String titulo, boolean completada, LocalDateTime creadaEn) {

	static TareaResponse desde(Tarea tarea) {
		return new TareaResponse(tarea.getId(), tarea.getTitulo(), tarea.isCompletada(), tarea.getCreadaEn());
	}
}
