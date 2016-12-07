package jbinvis.frontend;

import jbinvis.renderer.RenderLogic;
import jbinvis.visualisations.*;

/**
 * Contains static references to render logic
 * @author Billy
 */
public class RenderLogicHolder {
    private static RenderLogicHolder _singleton = null;
    
    public static RenderLogicHolder getInstance() {
        if(_singleton == null) 
            _singleton = new RenderLogicHolder();
        return _singleton;
    }
    
    private RenderLogic[] renderLogics;
    private final int COUNT = 2;
    
    public static final int RL_BYTEMAP = 0;
    
    
    private RenderLogicHolder() {
        renderLogics = new RenderLogic[COUNT];
        
        renderLogics[RL_BYTEMAP] = new Bytemap();
    }
    
    public RenderLogic get(int index) {
        return renderLogics[index];
    }
}
