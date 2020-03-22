package net.purevirtual.chell.central.web.agent.control;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.ejb.AsyncResult;
import net.purevirtual.chell.central.web.agent.entity.Game;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRunnerTest {
    private static final Logger logger = LoggerFactory.getLogger(GameRunnerTest.class);
    
    @InjectMocks
    private GameRunner sut = new GameRunner();
   
    @Mock
    private UciAgent white;
    
    @Mock
    private UciAgent black;

        
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(white.reset()).thenReturn(new AsyncResult<>(null));
        when(black.reset()).thenReturn(new AsyncResult<>(null));
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testRun() throws Exception {
        logger.info("white = {}",white);
        Future<String> move1 = mockMove("f2f3");
        Future<String> move2 = mockMove("e7e5");
        Future<String> move3 = mockMove("g2g4");
        Future<String> move4 = mockMove("d8h4");
        
        when(white.move(any(), anyLong())).thenReturn(move1, move3);               
        when(black.move(any(), anyLong())).thenReturn(move2, move4);
        Game game = new Game(white, black);
        sut.run(game);
        assertTrue(game.getResult().equals("BLACK"));
    }
    
    private static Future<String> mockMove(String input) throws Exception {
        Future<String>  result = Mockito.mock(Future.class);
        when(result.get()).thenReturn(input);
        when(result.get(anyLong(), any(TimeUnit.class))).thenReturn(input);
        return result;
    }
    
}
