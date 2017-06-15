package jbinvis.backend;

import java.io.IOException;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class DigraphConvNet {
	private MultiLayerNetwork model;
	
	/**
	 * Create a new instance of the CNN using trained network
	 * @param convDataPath
	 * @return
	 */
	public static DigraphConvNet create(String convDataPath) {
		try {
			DigraphConvNet net = new DigraphConvNet(convDataPath);
			return net;
		} catch (Exception e) {
			return null;
		}
	}
	
	private DigraphConvNet(String convData) throws Exception {
		model = ModelSerializer.restoreMultiLayerNetwork(convData);
	}
	
	
}
