package net.purevirtual.chell.central.web.boundary;

import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.entity.Agent;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
public class GamePageTest {
    
    @Mock
    private GameManager gameManager;  
    
    @InjectMocks
    private GamePage sut = new GamePage();
    
    @Before
    public void setUp() {
        Game game = new Game();
        game.setId(123);
        game.setWhitePlayedByFirstAgent(true);
        Match match = new Match();
        EngineConfig engineConfig = new EngineConfig();
        engineConfig.setAgent(new Agent());
        match.setPlayer1(engineConfig);
        match.setPlayer2(engineConfig);
        game.setMatch(match);
        MockitoAnnotations.initMocks(this);
        when(gameManager.get(any())).thenReturn(game);
    }

    /**
     * Test of get method, of class GamePage.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        int gameId = 0;
        
        String expResult = "";
        String result = sut.get(gameId);
        assertEquals(expResult, result);
    }

    /**
     * Test of list method, of class GamePage.
     */
    @Test
    public void testList() {
        System.out.println("list");
        String expResult = "";
        String result = sut.list();
        assertEquals(expResult, result);
    }
    
}
