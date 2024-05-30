package com.example.booksuggestion.controller;


import com.example.booksuggestion.service.BookSuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookSuggestion {

    @Autowired
    private BookSuggestionService bookSuggestionService;

    @PostMapping("/recommend")
    public List<String> getRecommendations(@RequestBody Map<String, String> answers) {
        return bookSuggestionService.getRecommendations(answers);
    }
}
