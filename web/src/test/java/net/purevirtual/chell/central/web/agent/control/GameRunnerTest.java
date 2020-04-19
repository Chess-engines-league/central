package net.purevirtual.chell.central.web.agent.control;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.ejb.AsyncResult;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
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
    private EngineConfig whiteConfig;
    
    @Mock
    private UciAgent black;
    
    @Mock
    private EngineConfig blackConfig;
    
    @Mock
    private GameManager gameManager;

        
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        CompletableFuture<Void> t = new CompletableFuture<>();
        t.complete(null);
        when(white.reset(any())).thenReturn(t);
        when(black.reset(any())).thenReturn(t);
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testRun() throws Exception {
        logger.info("white = {}",white);
        CompletableFuture<String> move1 = mockMove("f2f3");
        CompletableFuture<String> move2 = mockMove("e7e5");
        CompletableFuture<String> move3 = mockMove("g2g4");
        CompletableFuture<String> move4 = mockMove("d8h4");
        CompletableFuture<Void> mockReset = mockReset();
        
        when(white.move(any(), anyLong())).thenReturn(move1, move3);
        when(black.move(any(), anyLong())).thenReturn(move2, move4);
        when(white.reset(any())).thenReturn(mockReset);
        when(black.reset(any())).thenReturn(mockReset);
        when(white.assignGame(any())).thenReturn(Boolean.TRUE);
        when(black.assignGame(any())).thenReturn(Boolean.TRUE);
        LiveGame game = new LiveGame(new Game(), white, black, whiteConfig, blackConfig);
        GameResult result = sut.runSync(game);
        assertTrue(result.equals(GameResult.BLACK));
    }
    
    private static CompletableFuture<String> mockMove(String input) throws Exception {
        CompletableFuture<String> t = new CompletableFuture<>();
        t.complete(input);
        return t;
//        Future<String>  result = Mockito.mock(Future.class);
//        when(result.get()).thenReturn(input);
//        when(result.get(anyLong(), any(TimeUnit.class))).thenReturn(input);
//        return result;
    }
    
    private static CompletableFuture<Void> mockReset() throws Exception {
        CompletableFuture<Void> t = new CompletableFuture<>();
        t.complete(null);
        return t;
//        Future<Void>  result = Mockito.mock(Future.class);
//        when(result.get()).thenReturn(null);
//        when(result.get(anyLong(), any(TimeUnit.class))).thenReturn(null);
//        return result;
    }
    
}
