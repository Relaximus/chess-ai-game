package com.relaximus.chessengine;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static org.springframework.util.StringUtils.hasLength;

@Service
public class AiMoveService {

    @Autowired
    OllamaChatClient chatClient;

    Tuple2<String, String> suggestMove(String fen, String moves, String check, List<String> history) {
        var systemMessage = new SystemMessage("""
                Let's play chess.
                I'll give you current FEN of the game and the list of possible moves at the desk at the moment.
                Could you suggest the next move for the Black side.
                First of all, check if any of your figures under attack and try to safe them with the next move.
                At the same time if there is a possibility to eat white figures without danger to be attack, do it!
                If there are multiple white figures available for attack, chose the most important one for attacking!
                Also I let you know if there is check situation you can use for taking decisions.
                In general, it has to be most effective move to win the game, you can use known plays from history and common chess strategies to win.
                Place the move option inside of tag <mark><mark/>.
                If you don't know the answer just return an empty <mark></mark>
                """);
        List<Message> historyOfChat = history.stream()
                .gather(Gatherers.windowFixed(2))
                .flatMap(strings -> Stream.of(
                        new UserMessage(strings.get(0)),
                        new AssistantMessage(strings.get(1))))
                .collect(toCollection(() -> new ArrayList<>(List.of(systemMessage))));

        var lastUserMessage = new PromptTemplate("""
                So her is the FEN of the chess game.
                             
                {fen}
                             
                The list of possible moves:
                             
                {moves}
                             
                The check situation:
                          
                {check}
                             
                Give me the best move with this input""").createMessage(Map.of("fen", fen, "moves", moves, "check", hasLength(check) ? check + " side has a check situation." : "There is no check situation at the moment"));

        historyOfChat.add(lastUserMessage);
        ChatResponse response = chatClient.call(new Prompt(historyOfChat));

        return Tuples.of(lastUserMessage.getContent(), response.getResult().getOutput().getContent());
    }
}
