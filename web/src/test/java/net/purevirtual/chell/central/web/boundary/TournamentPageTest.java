package net.purevirtual.chell.central.web.boundary;

import java.util.ArrayList;
import java.util.Collections;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.control.TournamentManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class TournamentPageTest {
    
    @Mock
    private MatchManager matchManager;  
    
    @Mock
    private GameManager gameManager;
    
    @Mock
    private EngineManager engineManager;
    
    @Mock
    private TournamentManager tournamentManager;
    
    @InjectMocks
    private TournamentPage sut = new TournamentPage();
        
    @Before
    public void setUp() {
        Game game = new Game();
        game.setId(123);
        game.setWhitePlayedByFirstAgent(true);
        Match match = new Match();
        EngineConfig engineConfig = new EngineConfig();
        Engine engine = new Engine();
        engine.setName("name1");
        engine.setId(234);
        engineConfig.setEngine(engine);
        engineConfig.setId(123);
        engineConfig.setDescription("desc 2");
        match.setPlayer1(engineConfig);
        match.setPlayer2(engineConfig);
        game.setMatch(match);
        match.getGames().add(game);
        MockitoAnnotations.initMocks(this);
        when(matchManager.get(any())).thenReturn(match);
        when(matchManager.findAll()).thenReturn(Collections.singletonList(match));
        when(gameManager.findByMatch(any())).thenReturn(Collections.singletonList(game));
    }
    
    @Test
    public void testGet() {
        System.out.println("get");
        int matchId = 0;
        
        String result = sut.get(matchId);
        assertTrue(!result.isBlank());
    }

    @Test
    public void testList() {
        System.out.println("list");
        String result = sut.list();
        assertTrue(!result.isBlank());
    }
    
        
    @Test
    public void testNewMatch() {
        ArrayList<Engine> engines = new ArrayList<>();
        engines.add(new Engine());
        when(engineManager.findAll()).thenReturn(engines);
        String result = sut.newMatch();
        assertTrue(!result.isBlank());
    }
    
}
