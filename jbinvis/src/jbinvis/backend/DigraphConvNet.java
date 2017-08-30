package jbinvis.backend;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import jbinvis.renderer.CanvasTexture;

public class DigraphConvNet {

	
	// labels
	// doc, gif, jpg, pdf, png, ppt, xls
	
	private MultiLayerNetwork model;
	private NativeImageLoader loader;
	private ByteToImage bti;
	private List<LabelProbability> probabilities;
	
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
		loader = new NativeImageLoader(256,256,1);
		bti = new ByteToImage(256, 256);
		
		probabilities = new ArrayList<LabelProbability>();
		probabilities.add(new LabelProbability(0, "doc"));
		probabilities.add(new LabelProbability(0, "gif"));
		probabilities.add(new LabelProbability(0, "jpg"));
		probabilities.add(new LabelProbability(0, "pdf"));
		probabilities.add(new LabelProbability(0, "png"));
		probabilities.add(new LabelProbability(0, "ppt"));
		probabilities.add(new LabelProbability(0, "xls"));
	}
	
	
	public List<LabelProbability> evaluate(CanvasTexture digraphTexture) {
		bti.put(digraphTexture);
		BufferedImage image = bti.getImage();
		INDArray imgArr;
		
		try {
			imgArr = loader.asMatrix(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		INDArray output = model.output(imgArr);
		for(int i=0;i<probabilities.size();i++) {
			probabilities.get(i).setProbability(output.getDouble(i));
		}
		
		return probabilities;
	}
}
