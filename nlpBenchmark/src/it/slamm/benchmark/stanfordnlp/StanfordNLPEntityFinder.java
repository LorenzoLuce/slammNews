package it.slamm.benchmark.stanfordnlp;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;



public class StanfordNLPEntityFinder {
	
	private static EtmMonitor monitor;
	private static AbstractSequenceClassifier<CoreLabel> classifier;

	public static void extractEntities(String news) throws Exception {


		// This one is best for dealing with the output as a TSV (tab-separated column) file.
		// The first column gives entities, the second their classes, and the third the remaining text in a document
		EtmPoint point2 = monitor.createPoint("StanfordNLP NameExtraction - extraction");
		String classifyToString = classifier.classifyToString(news, "tabbedEntities", false);
		System.out.print(classifyToString);
		point2.collect();

	}

	public static void main(String[] args) throws Exception {
		BasicEtmConfigurator.configure();
	    monitor = EtmManager.getEtmMonitor();
	    monitor.start();
	    
	    EtmPoint point1 = monitor.createPoint("StanfordNLP NameExtraction - model loading");
	    classifier = CRFClassifier.getClassifier("stanfordNLP-classifiers/english.all.3class.distsim.crf.ser.gz");
	    point1.collect();
	    
		for(int i=1; i<=3; i++) {
			String documentStr = new String(Files.readAllBytes(Paths.get("news/" + i +".txt")));
			extractEntities(documentStr);
			System.out.println("----------------------------------------------------------------");
		}
		monitor.render(new SimpleTextRenderer());

	}

}
