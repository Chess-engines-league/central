package net.purevirtual.chell.central.web.boundary;

import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.Game;
import org.slf4j.LoggerFactory;
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
        WebContext context = newContext();
        context.setVariable("game", game);
        context.setVariable("fenList", new Gson().toJson(getFenList(game)));
        return getTemplateEngine().process("games/game", context);
    }

    @GET
    public String list() {
        WebContext context = newContext();
        List<Game> games = gameManager.findAll();
        context.setVariable("games", games);
        return getTemplateEngine().process("games/list", context);
    }

    private List<String> getFenList(Game game) {
        List<String> result = new ArrayList<>();
        List<String> cumulativeMoves = new ArrayList<>();
        try {
            for (String move : game.getMoves()) {

                cumulativeMoves.add(move);
                MoveList list = new MoveList();
                list.loadFromText(String.join(" ", cumulativeMoves));
                result.add(list.getFen());

            }

        } catch (MoveConversionException ex) {
            logger.error("Failed to parse game move", ex);
        }
        return result;
    }

}
