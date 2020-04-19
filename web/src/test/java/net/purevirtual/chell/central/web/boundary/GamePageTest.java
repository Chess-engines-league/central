package net.purevirtual.chell.central.web.boundary;

import java.util.Collections;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
public class GamePageTest {
    
    @Mock
    private GameManager gameManager;  
    @Mock
    private MatchManager matchManager;  
    
    @InjectMocks
    private GamePage sut = new GamePage();
    
    @Before
    public void setUp() {
        Game game = new Game();
        game.setId(123);
        game.setWhitePlayedByFirstAgent(true);
        Match match = new Match();
        EngineConfig engineConfig = new EngineConfig();
        engineConfig.setEngine(new Engine());
        match.setPlayer1(engineConfig);
        match.setPlayer2(engineConfig);
        game.setMatch(match);
        match.getGames().add(game);
        MockitoAnnotations.initMocks(this);
        when(gameManager.get(any())).thenReturn(game);
        when(gameManager.findAll()).thenReturn(Collections.singletonList(game));
        when(matchManager.findAll()).thenReturn(Collections.singletonList(match));
        
    }

    /**
     * Test of get method, of class GamePage.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        int gameId = 0;
        
        String result = sut.get(gameId);
        assertTrue(!result.isBlank());
    }

    /**
     * Test of list method, of class GamePage.
     */
    @Test
    public void testList() {
        System.out.println("list");
        String result = sut.list();
        assertTrue(!result.isBlank());
    }
    
}
