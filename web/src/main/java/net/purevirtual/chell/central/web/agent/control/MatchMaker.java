package net.purevirtual.chell.central.web.agent.control;

import java.io.Serializable;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.crud.control.GameManager;

import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.Tournament;
import net.purevirtual.chell.central.web.crud.entity.dto.MatchConfig;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;

@Stateless
public class MatchMaker {
    @Inject
    private MatchManager matchManager;
    @Inject
    private GameManager gameManager;
    @Inject
    private GameManager matchgameManager;

    public Match newMatch(EngineConfig player1, EngineConfig player2, int gameCount, MatchConfig matchConfig, Tournament tournament ) {
        Objects.requireNonNull(player1, "player1");
        Objects.requireNonNull(player2, "player2");
        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setGameCount(gameCount);
        match.setResult("");
        match.setState(MatchState.PENDING);
        match.setConfig(matchConfig);
        match.setTournament(tournament);
        matchManager.save(match);
        for (int gameNumber = 1; gameNumber <= gameCount; gameNumber++) {
            Game game = new Game();
            game.setMatch(match);
            game.setGameNumber(gameNumber);
            game.setResult(GameResult.PENDING);
            game.setBoardState(null);
            game.setWhitePlayedByFirstAgent(gameNumber % 2 == 1);
            gameManager.save(game);
        }
        return match;
    }
}
