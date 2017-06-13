package com.ureca.simplecnn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	private static Logger log = LoggerFactory.getLogger(App.class);
	
    protected static int numExamples = 100;
    protected static int numLabels = 7;
    protected static int batchSize = 128;
    protected static int numEpochs = 100;
    
    public static void main( String[] args ) throws Exception
    {
    	org.apache.log4j.BasicConfigurator.configure();
    	
    	Random rng = new Random();
    	
    	ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
    	File mainPath = new File("../Tools/output/");
    	FileSplit fileSplit = new FileSplit(mainPath, NativeImageLoader.ALLOWED_FORMATS, rng);
    	BalancedPathFilter pathFilter = new BalancedPathFilter(rng, labelMaker, numExamples, numLabels, batchSize);

        InputSplit[] inputSplit = fileSplit.sample(pathFilter, 0.8, 0.2);
        InputSplit trainData = inputSplit[0];
        InputSplit testData = inputSplit[1];   
        
        ImageRecordReader recordReader = new ImageRecordReader(256,256,1,labelMaker);
        recordReader.initialize(trainData);
       
        RecordReaderDataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);
        
        DataNormalization normalizer = new ImagePreProcessingScaler(0,1);
        normalizer.fit(dataIter);
        dataIter.setPreProcessor(normalizer);
        
        log.info("** BUILD MODEL **");

    	ConvolutionLayer layer0 = new ConvolutionLayer.Builder(5,5)
    	        .nIn(1)
    	        .nOut(16)
    	        .stride(1,1)
    	        .padding(2,2)
    	        .weightInit(WeightInit.XAVIER)
    	        .name("First convolution layer")
    	        .activation(Activation.RELU)
    	        .build();

    	SubsamplingLayer layer1 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
    	        .kernelSize(2,2)
    	        .stride(2,2)
    	        .name("First subsampling layer")
    	        .build();

    	ConvolutionLayer layer2 = new ConvolutionLayer.Builder(5,5)
    	        .nOut(20)
    	        .stride(1,1)
    	        .padding(2,2)
    	        .weightInit(WeightInit.XAVIER)
    	        .name("Second convolution layer")
    	        .activation(Activation.RELU)
    	        .build();

    	SubsamplingLayer layer3 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
    	        .kernelSize(2,2)
    	        .stride(2,2)
    	        .name("Second subsampling layer")
    	        .build();

    	ConvolutionLayer layer4 = new ConvolutionLayer.Builder(5,5)
    	        .nOut(20)
    	        .stride(1,1)
    	        .padding(2,2)
    	        .weightInit(WeightInit.XAVIER)
    	        .name("Third convolution layer")
    	        .activation(Activation.RELU)
    	        .build();

    	SubsamplingLayer layer5 = new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
    	        .kernelSize(2,2)
    	        .stride(2,2)
    	        .name("Third subsampling layer")
    	        .build();

    	DenseLayer layer6 = new DenseLayer.Builder()
    			.activation(Activation.RELU)
    			.nOut(512)
    			.weightInit(WeightInit.XAVIER)
    			.name("Dense layer")
    			.build();
    	
    	OutputLayer layer7 = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
    	        .activation(Activation.SOFTMAX)
    	        .weightInit(WeightInit.XAVIER)
    	        .name("Output")
    	        .nOut(numLabels)
    	        .build();
    	
    	MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
    	        .seed(1200)
    	        .iterations(1)
    	        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    	        .learningRate(0.01)
    	        .regularization(true)
    	        .l2(1e-4)
    	        .updater(Updater.NESTEROVS)
    	        .momentum(0.9)
    	        .list()
    	            .layer(0, layer0)
    	            .layer(1, layer1)
    	            .layer(2, layer2)
    	            .layer(3, layer3)
    	            .layer(4, layer4)
    	            .layer(5, layer5)
    	            .layer(6, layer6)
    	            .layer(7,  layer7)
    	        .pretrain(false)
    	        .backprop(true)
    	        .setInputType(InputType.convolutional(256,256,1))
    	        .build();
    	
    	MultiLayerNetwork network = new MultiLayerNetwork(configuration);
    	network.init();
    	
    	network.setListeners(new ScoreIterationListener(1));
    	
    	for(int i=0;i<numEpochs;i++) {
    		network.fit(dataIter);
    	}
    	
    	recordReader.reset();
    	recordReader.initialize(testData);
    	
    	RecordReaderDataSetIterator testIter = new RecordReaderDataSetIterator(recordReader,batchSize,1,numLabels);
    	normalizer.fit(testIter);
    	testIter.setPreProcessor(normalizer);
    	
    	Evaluation eval = new Evaluation(numLabels);
    	while(testIter.hasNext()) {
    		DataSet iter = testIter.next();
    		INDArray output = network.output(iter.getFeatureMatrix());
    		eval.eval(iter.getLabels(), output);
    	}
    	log.info(eval.stats());
    	
    	log.info("** SAVE MODEL **");
    	
    	File modelLocation = new File("trained_model.zip");
    	ModelSerializer.writeModel(network, modelLocation, false);
    }
}
