package com.relaximus.chessengine;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasLength;

@Service
public class AiMoveService {

    @Autowired
    OllamaChatClient chatClient;

    String suggestMove(String fen, String moves, String check) {
        var systemMessage = new SystemMessage("""
                 You represent a professional chess player.
                 You are playing for Black side.
                 As an input you will receive the FEN of the game, possible moves to chose from and indicator if its check situation or not.
                 Restore the board setup according to the input.
                 Pick up one best move from available moves, it has to be most effective move to win the game.
                 Place the move option inside of tag <mark><mark/>.
                 If you don't know the answer just return an empty <mark></mark>
                """);
        var userMessage = new PromptTemplate("""
                Her are the FEN of the chess game.
                             
                {fen}
                             
                The list of possible moves:
                             
                {moves}
                             
                The check situation:
                          
                {check}
                             
                Give me the best move with this input""")
                .createMessage(Map.of(
                        "fen", fen,
                        "moves", moves,
                        "check", hasLength(check) ? check + " side has a check situation." : "There is no check situation at the moment"
                ));

        ChatResponse response = chatClient.call(new Prompt(List.of(userMessage, systemMessage)));

        System.out.println(STR."FEN: \{fen}");
        System.out.println(STR."MOVES: \{moves}");
        System.out.println(STR."CHECK: \{check}");
        System.out.println(STR."Response: \{response}");

        return response.getResult().getOutput().getContent();
    }
}
