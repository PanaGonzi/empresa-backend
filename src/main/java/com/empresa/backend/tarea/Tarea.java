package com.empresa.backend.tarea;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarea")
public class Tarea {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String titulo;

	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(nullable = false)
	private boolean completada;

	@Column(name = "creada_en", nullable = false, updatable = false)
	private LocalDateTime creadaEn;

	@PrePersist
	void alCrear() {
		// Siempre en UTC, independiente de la zona horaria de la maquina o el contenedor
		creadaEn = LocalDateTime.now(ZoneOffset.UTC);
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public boolean isCompletada() {
		return completada;
	}

	public void setCompletada(boolean completada) {
		this.completada = completada;
	}

	public LocalDateTime getCreadaEn() {
		return creadaEn;
	}
}
