package net.purevirtual.chell.central.web.boundary;

import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

@Path("/games")
@Produces(MediaType.TEXT_HTML)
public class GamePage extends PageResource {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GamePage.class);
    @Inject
    private MatchManager matchManager;

    @Inject
    private GameManager gameManager;

    @GET
    @Path("/{gameId}")
    public String get(@PathParam("gameId") int gameId) {
        Game game = gameManager.get(gameId);
        HashMap<String,Object> model = new HashMap<>();
        EngineConfig white,black;
        if (game.isWhitePlayedByFirstAgent()) {
            white = game.getMatch().getPlayer1();
            black = game.getMatch().getPlayer2();
        } else {
            white = game.getMatch().getPlayer2();
            black = game.getMatch().getPlayer1();
        }
        
        model.put("game", game);
        model.put("match", game.getMatch());
        model.put("white", white);
        model.put("black", black);
        List<SingleGameMove> fenList = getFenList(game);
        model.put("fenList", fenList);
        model.put("fenListJson", new Gson().toJson(fenList));
        return process("games/game", model);
    }

    @GET
    public String list() {
        HashMap<String,Object> model = new HashMap<>();
        List<Game> games = gameManager.findAll();
        model.put("games", games);
        return process("games/list", model);
    }

    private List<SingleGameMove> getFenList(Game game) {
        List<SingleGameMove> result = new ArrayList<>();
        List<String> cumulativeMoves = new ArrayList<>();
        int i =0;
        SingleGameMove startPosition = new SingleGameMove();
        startPosition.fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        startPosition.index = i++;
        result.add(startPosition);
        try {
            for (BoardMove boardMove : game.getBoardState().getBoardMoves()) {
                cumulativeMoves.add(boardMove.getMove());
                MoveList list = new MoveList();
                list.loadFromText(String.join(" ", cumulativeMoves));
                SingleGameMove singleGameMove = new SingleGameMove();
                singleGameMove.fen = list.getFen();
                singleGameMove.move = boardMove.getMove();
                singleGameMove.comment = boardMove.getComment();
                singleGameMove.ponder = boardMove.getPonder();
                singleGameMove.index = i++;
                result.add(singleGameMove);
            }
        } catch (MoveConversionException ex) {
            logger.error("Failed to parse game move", ex);
        }
        return result;
    }
    
    private static class SingleGameMove {
        int index;
        String move;
        String fen;
        String ponder;
        String comment;
    }

}
