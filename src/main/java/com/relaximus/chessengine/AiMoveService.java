package com.relaximus.chessengine;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasLength;

@Service
public class AiMoveService {

    static String TEST_PGN = """
            [Event "January 2 Late 2024"]
            [Date "2024.01.02"]
            [Round "5"]
            [White "Nakamura, Hikaru"]
            [Black "Van Foreest, Jorden"]
            [Result "1-0"]
            [Board "1"]
            [TimeControl "180+1"]
            [WhiteFideId "2016192"]
            [BlackFideId "1039784"]
            [WhiteElo "3277"]
            [BlackElo "3008"]
            [WhiteTeam "USA"]
            [BlackTeam "NED"]
            [WhiteClock "0:00:17"]
            [BlackClock "0:00:00"]
                
            1. e4 {[%clk 0:03:00]}  e5 {[%clk 0:02:59]} 2. Bc4 {[%clk 0:03:00]}  Nf6 {[%clk 0:02:58]} 3. d3 {[%clk 0:03:00]}  Bc5 {[%clk 0:02:59]} 4. Nc3 {[%clk 0:03:00]}  d6 {[%clk 0:02:58]} 5. h3 {[%clk 0:02:58]}  c6 {[%clk 0:02:58]} 6. Nge2 {[%clk 0:02:57]}  a5 {[%clk 0:02:58]} 7. a4 {[%clk 0:02:54]}  Be6 {[%clk 0:02:58]} 8. Ng3 {[%clk 0:02:54]}  Nbd7 {[%clk 0:02:56]} 9. O-O {[%clk 0:02:54]}  O-O {[%clk 0:02:46]} 10. Qf3 {[%clk 0:02:49]}  Kh8 {[%clk 0:02:46]} 11. Nf5 {[%clk 0:02:47]}  Nb6 {[%clk 0:02:40]} 12. Bg5 {[%clk 0:02:39]}  Nbd7 {[%clk 0:02:14]} 13. Rad1 {[%clk 0:02:30]}  Qe8 {[%clk 0:02:13]} 14. b3 {[%clk 0:02:25]}  Ng8 {[%clk 0:02:07]} 15. Ne2 {[%clk 0:02:08]}  g6 {[%clk 0:02:06]} 16. Nh6 {[%clk 0:02:01]}  f6 {[%clk 0:02:06]} 17. Be3 {[%clk 0:01:41]}  Bxe3 {[%clk 0:02:00]} 18. Qxe3 {[%clk 0:01:41]}  f5 {[%clk 0:01:30]} 19. exf5 {[%clk 0:01:40]}  gxf5 {[%clk 0:01:31]} 20. Nxg8 {[%clk 0:01:39]}  Rxg8 {[%clk 0:01:29]} 21. Bxe6 {[%clk 0:01:33]}  Qxe6 {[%clk 0:01:30]} 22. f4 {[%clk 0:01:33]}  Rg6 {[%clk 0:01:27]} 23. Rf2 {[%clk 0:01:19]}  Rag8 {[%clk 0:01:27]} 24. Kh1 {[%clk 0:01:01]}  Qd5 {[%clk 0:01:20]} 25. c4 {[%clk 0:00:54]}  Qe6 {[%clk 0:01:06]} 26. fxe5 {[%clk 0:00:55]}  dxe5 {[%clk 0:01:03]} 27. d4 {[%clk 0:00:53]}  f4 {[%clk 0:00:54]} 28. Qe4 {[%clk 0:00:35]}  Rh6 {[%clk 0:00:21]} 29. dxe5 {[%clk 0:00:23]}  Nc5 {[%clk 0:00:21]} 30. Qf3 {[%clk 0:00:18]}  Qxe5 {[%clk 0:00:19]} 31. Nxf4 {[%clk 0:00:19]}  Ne4 {[%clk 0:00:17]}
            """;

    @Autowired
    OllamaChatClient chatClient;
//    OpenAiChatClient chatClient;

    String suggestMove(String fen, String moves, String check) {
        var systemMessage = new SystemMessage("""
                 You represent a professional chess player.
                 You are playing for Black side.
                 As an input you will receive the FEN of the game, possible moves to chose from and indicator if its check situation or not.
                 Restore the board setup according to the input.
                 Pick up one best move from available moves, it has to be most effective move to win the game.
                 Place the move option inside of tag <move><move/>.
                 If you don't know the answer just return an empty <move></move>
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

        var pattern = Pattern.compile("<move>(.+?)</move>", Pattern.DOTALL);
        var matcher = pattern.matcher(response.getResult().getOutput().getContent());
        boolean found = matcher.find();
        return found ? matcher.group(1) : "";
    }
}
