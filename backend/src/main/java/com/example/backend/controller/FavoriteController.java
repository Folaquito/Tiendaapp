package com.example.backend.controller;

import com.example.backend.model.FavoriteGame;
import com.example.backend.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository repository;

    @GetMapping
    public List<FavoriteGame> getAllFavorites() {
        return repository.findAll();
    }

    @PostMapping
    public FavoriteGame addFavorite(@RequestBody FavoriteGame game) {
        if (repository.findByGameId(game.getGameId()).isPresent()) {
            return repository.findByGameId(game.getGameId()).get();
        }
        return repository.save(game);
    }

    @DeleteMapping("/{gameId}")
    @Transactional
    public ResponseEntity<Void> removeFavorite(@PathVariable Integer gameId) {
        repository.deleteByGameId(gameId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{gameId}")
    public ResponseEntity<FavoriteGame> updateNote(@PathVariable Integer gameId, @RequestBody FavoriteGame updatedGame) {
        return repository.findByGameId(gameId)
                .map(game -> {
                    game.setNote(updatedGame.getNote());
                    return ResponseEntity.ok(repository.save(game));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
