package net.purevirtual.chell.central.web.agent.control;

import javax.inject.Inject;
import net.purevirtual.chell.central.web.crud.control.GameManager;

import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.Agent;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;

public class MatchMaker {
    @Inject
    private MatchManager matchManager;
    @Inject
    private GameManager gameManager;
    @Inject
    private GameManager matchgameManager;

    public Match newMatch(Agent agent1, Agent agent2, int gameCount) {
        Match match = new Match();
        match.setAgent1(agent1);
        match.setAgent2(agent2);
        match.setGameCount(gameCount);
        match.setResult("");
        match.setState(MatchState.PENDING);
        matchManager.save(match);
        for (int gameNumber = 1; gameNumber <= gameCount; gameNumber++) {
            Game game = new Game();
            game.setMatch(match);
            game.setGameNumber(gameNumber);
            game.setResult(GameResult.PENDING);
            game.setBoardState("");
            game.setWhitePlayedByFirstAgent(gameNumber % 2 == 1);
            gameManager.save(game);
        }
        return match;
    }
}
