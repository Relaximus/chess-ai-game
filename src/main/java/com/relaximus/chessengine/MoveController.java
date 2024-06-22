package com.relaximus.chessengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.*;
import static java.util.Objects.*;

@RestController
@CrossOrigin(origins = "*")
public class MoveController {

    @Autowired
    AiMoveService moveService;

    @PostMapping("/move")
    MoveResponse move(@RequestBody MoveQuery query) {
        Tuple2<String, String> result = moveService.suggestMove(query.fen(), query.moves().toString(),
                query.check(), requireNonNullElse(query.history(), emptyList()));
        return new MoveResponse(result.getT1(), result.getT2());
    }

    record MoveResponse(String question, String answer) {
    }

    record MoveQuery(String fen, List<String> moves, String check, List<String> history) {
    }
}
