package net.purevirtual.chell.central.web.agent.bounduary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.control.TournamentManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.Tournament;
import net.purevirtual.chell.central.web.crud.entity.TournamentParticipant;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class TournamentTimer {
    private static final Logger logger = LoggerFactory.getLogger(TournamentTimer.class);
    private static final int ELO_RECALC_COUNT = 2;
    @Inject
    private MatchManager matchManager;
    @Inject
    private TournamentManager tournamentManager;

    @Schedule(hour = "*", minute = "*", second = "*/30", persistent = false)
    public void checkForUnfinishedTournaments() {
        List<Tournament> unfinished = tournamentManager.findUnfinished();
        logger.info("found {} unfinished tournaments", unfinished.size());
        for (Tournament tournament : unfinished) {
            if (tournament.getMatches().stream().allMatch(m->m.getState()==MatchState.FINISHED)) {
                Map<Integer, TournamentParticipant> participantsByConfigId = tournament.getParticipants()
                        .stream()
                        .collect(Collectors.toMap(p->p.getPlayer().getId(), p->p));
                for(int i=0;i<ELO_RECALC_COUNT;i++) {
                    for (Match match : tournament.getMatches()) {
                        TournamentParticipant participant1 = participantsByConfigId.get(match.getPlayer1().getId());
                        TournamentParticipant participant2 = participantsByConfigId.get(match.getPlayer2().getId());
                        int elo1 = participant1.getElo();
                        int elo2 = participant2.getElo();
                        double r1 = Math.pow(10, elo1/400.0);
                        double r2 = Math.pow(10, elo2/400.0);
                        double e1 = r1 / (r1+r2);
                        double e2 = r2 / (r1+r2);
                        double s1 = 0.5d * match.getScore1() / match.getGameCount();
                        double s2 = 0.5d * match.getScore2() / match.getGameCount();

                        int newElo1 = (int) Math.round(elo1 + 32 * (s1 - e1));
                        int newElo2 = (int) Math.round(elo2 + 32 * (s2 - e2));
                        updateElo(match, participant1, newElo1);
                        updateElo(match, participant2, newElo2);
                    }
                }
                tournament.setState(MatchState.FINISHED);
            }
        }
    }
        
    private void updateElo(Match match, TournamentParticipant participant, int newElo) {
        logger.info("Changing elo from {} to {} for {} after match {} in tournament",
                participant.getElo(), newElo, participant.getPlayer().getId(), match.getId());
        participant.setElo(newElo);
    }
}
