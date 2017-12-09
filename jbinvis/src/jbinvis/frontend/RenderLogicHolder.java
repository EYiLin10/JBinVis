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
    
    public static RenderLogic fromId(int index) {
        return getInstance().get(index);
    }
    
    private RenderLogic[] renderLogics;
    public final int COUNT = 5;
    
    public static final int RL_BYTEMAP = 0;
    public static final int RL_DIGRAPH = 1;
    public static final int RL_FREQ_HISTOGRAM = 2;
    public static final int RL_TRIGRAPH = 3;
    public static final int RL_SPHERE = 4;
    
    private RenderLogicHolder() {
        renderLogics = new RenderLogic[COUNT];
        
        renderLogics[RL_BYTEMAP] = new Bytemap();
        renderLogics[RL_DIGRAPH] = new Digraph();
        renderLogics[RL_FREQ_HISTOGRAM] = new FrequencyHistogram();
        renderLogics[RL_TRIGRAPH] = new Trigraph();
        renderLogics[RL_SPHERE] = new Sphere();
    }
    
    public RenderLogic get(int index) {
        return renderLogics[index];
    }
}
