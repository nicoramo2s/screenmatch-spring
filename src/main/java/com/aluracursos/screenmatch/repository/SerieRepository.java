package com.aluracursos.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
      Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

      List<Serie> findTop5ByOrderByEvaluacionDesc();

      List<Serie> findByGenero(Categoria categoria);

      @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion")
      List<Serie> seriesPortemporadaYEvaluacion(int totalTemporadas, Double evaluacion);

      @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
      List<Episodio> episodioPorNombre(String nombreEpisodio);

      @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
      List<Episodio> top5Episodios(Serie serie);

      @Query("SELECT s FROM Serie s JOIN s.episodios e GROUP BY s ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5")
      List<Serie> lanzamientosMasRecientes();

      @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :idTemporada")
      List<Episodio> obtenerTemporadasPorId(Long id, Long idTemporada);
}
