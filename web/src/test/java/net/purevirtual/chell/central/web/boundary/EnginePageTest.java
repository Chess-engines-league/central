package net.purevirtual.chell.central.web.boundary;

import java.util.Collections;
import net.purevirtual.chell.central.web.crud.control.AgentManager;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.enums.EngineType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class EnginePageTest {
    
    @Mock
    private MatchManager matchManager;  
    
    @Mock
    private GameManager gameManager;
    
    @Mock
    private AgentManager agentManager;
    
    @InjectMocks
    private EnginePage sut = new EnginePage();
        
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Engine agent = new Engine();
        agent.setType(EngineType.OTHER);
        when(agentManager.findAll()).thenReturn(Collections.singletonList(agent));
        when(agentManager.get(any())).thenReturn(agent);
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
    
}
