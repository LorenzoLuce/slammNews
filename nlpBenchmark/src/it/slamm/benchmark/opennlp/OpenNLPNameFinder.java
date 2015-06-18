package it.slamm.benchmark.opennlp;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;


public class OpenNLPNameFinder {

	private static EtmMonitor monitor;

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */

	public static void extractNames(String file, NameFinderME nameFinder, 
			SentenceDetectorME sentenceDetector, TokenizerME tokenizer) throws FileNotFoundException {

		EtmPoint point2 = monitor.createPoint("OpenNLP NameExtraction - extraction");

		String sentences[] = sentenceDetector.sentDetect(file);
		for (String sentence : sentences) {
			String tokens[] = tokenizer.tokenize(sentence);
			Span nameSpans[] = nameFinder.find(tokens);
			System.out.println("Found entity: " + Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
		}
		point2.collect();


	}

	public static void main(String[] args) throws IOException {
		
		BasicEtmConfigurator.configure();
		monitor = EtmManager.getEtmMonitor();
		monitor.start();
		EtmPoint point1 = monitor.createPoint("OpenNLP NameExtraction - model loading");

		InputStream modelNameIn = new FileInputStream("openNLP-classifiers/en-ner-person.bin");
		InputStream modelSentIn = new FileInputStream("openNLP-classifiers/en-sent.bin");
		InputStream is = new FileInputStream("openNLP-classifiers/en-token.bin");

		TokenNameFinderModel modelName = new TokenNameFinderModel(modelNameIn);
		SentenceModel modelSent = new SentenceModel(modelSentIn);
		TokenizerModel modelToken = new TokenizerModel(is);

		NameFinderME nameFinder = new NameFinderME(modelName);
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(modelSent);
		TokenizerME tokenizer = new TokenizerME(modelToken);
		point1.collect();

		for(int i=1; i<=3; i++) {
			String documentStr = new String(Files.readAllBytes(Paths.get("news/" + i +".txt")));
			extractNames(documentStr, nameFinder, sentenceDetector, tokenizer);
			System.out.println("----------------------------------------------------------------");
		}
		monitor.render(new SimpleTextRenderer());
		monitor.stop();

	}

}
