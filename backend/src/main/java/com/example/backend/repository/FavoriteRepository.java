package com.example.backend.repository;

import com.example.backend.model.FavoriteGame;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<FavoriteGame, Long> {
    Optional<FavoriteGame> findByGameId(Integer gameId);
    void deleteByGameId(Integer gameId);
}
