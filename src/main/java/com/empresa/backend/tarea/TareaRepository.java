package com.empresa.backend.tarea;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

	List<Tarea> findAllByOrderByCreadaEnDescIdDesc();
}
