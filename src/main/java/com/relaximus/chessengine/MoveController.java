package com.relaximus.chessengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class MoveController {

    @Autowired
    AiMoveService moveService;

    @GetMapping("/move")
    String move(@RequestParam(required = false) String fen, @RequestParam(required = false) String moves, @RequestParam(required = false) String check) {
        return moveService.suggestMove(fen, moves, check);
    }
}
