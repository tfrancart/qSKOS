package at.ac.univie.mminf.qskos4j.criteria;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class OutLinkFinder extends Criterion {

	private final Logger logger = LoggerFactory.getLogger(OutLinkFinder.class);
	
	private String publishingHost, authoritativeUriSubstring;
	private Map<URI, List<URL>> extResourcesForConcept;
	
	public OutLinkFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public CollectionResult<URI> findMissingOutLinks(
		Collection<URI> concepts,
		String publishingHost,
		String authoritativeUriSubstring) throws OpenRDFException 
	{
		extResourcesForConcept = new HashMap<URI, List<URL>>();
		this.publishingHost = publishingHost;
		this.authoritativeUriSubstring = authoritativeUriSubstring;
		
		findResourcesForConcepts(concepts);
		if (publishingHost == null && authoritativeUriSubstring == null) {
			guessPublishingHost();
		}
		retainExternalResources();
		
		return new CollectionResult<URI>(extractUnlinkedConcepts());
	}
	
	private void findResourcesForConcepts(Collection<URI> concepts) throws OpenRDFException 
	{
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(concepts, progressMonitor, "finding resources");
		
		while (conceptIt.hasNext()) {
			URI concept = conceptIt.next();			
			extResourcesForConcept.put(concept, findResourcesForConcept(concept));
		}
	}
	
	private List<URL> findResourcesForConcept(URI concept) 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		String query = createIRIQuery(concept); 
		
		TupleQueryResult result = vocabRepository.query(query);
		List<URL> resourceList = identifyResources(result);
		
		return resourceList;
	}
	
	private String createIRIQuery(URI concept) {
		return "SELECT DISTINCT ?iri "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+
				"WHERE {{<"+concept.stringValue()+"> ?p ?iri .} UNION "+
					"{?iri ?p <"+concept.stringValue()+"> .}"+
					"FILTER isIRI(?iri) "+
					"FILTER regex(str(?iri), \"^http\")}";
	}
		
	private void guessPublishingHost() throws QueryEvaluationException {
		HostNameOccurrencies hostNameOccurencies = new HostNameOccurrencies();
		
		Iterator<List<URL>> resourcesListIt = new MonitoredIterator<List<URL>>(
			extResourcesForConcept.values(),
			progressMonitor,
			"guessing publishing host");
		while (resourcesListIt.hasNext()) {
			for (URL conceptResource : resourcesListIt.next()) {
				hostNameOccurencies.put(conceptResource.getHost());
			}
		}
		
		publishingHost = hostNameOccurencies.getMostOftenOccuringHostName();
		logger.info("Guessed publishing host: '" +publishingHost+ "'");
	}
	
	private List<URL> identifyResources(TupleQueryResult iriTuples) 
		throws QueryEvaluationException 
	{
		List<URL> ret = new ArrayList<URL>();
		
		while (iriTuples.hasNext()) {
			Value iri = iriTuples.next().getValue("iri");
			
			try {
				URL url = new URL(iri.stringValue());
				ret.add(url);
			} 
			catch (MalformedURLException e) {
				continue;
			}			
		}
		
		return ret;
	}
	
	private void retainExternalResources() {
		List<URL> validExternalResources = new ArrayList<URL>();
		
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(
			extResourcesForConcept.keySet(),
			progressMonitor,
			"retaining external resources");
		while (conceptIt.hasNext()) {
			URI concept = conceptIt.next();
			
			List<URL> resourceURLs = extResourcesForConcept.get(concept);

			for (URL url : resourceURLs) {
				if (isExternalResource(url) && isNonSkosURL(url)) {
					validExternalResources.add(url);
				}
			}
			
			resourceURLs.retainAll(validExternalResources);
		}
	}
	
	private boolean isExternalResource(URL url) {
		if (publishingHost != null) {
			return !url.getHost().equals(publishingHost);	
		}
		else if (authoritativeUriSubstring != null) {
			return url.toString().toLowerCase().contains(authoritativeUriSubstring.toLowerCase());
		}
		
		throw new IllegalArgumentException("publishing host or authoritative URI substring must not be null");
	}
	
	private boolean isNonSkosURL(URL url) {
		return !url.toString().contains(SparqlPrefix.SKOS.getNameSpace());
	}
	
	private Collection<URI> extractUnlinkedConcepts() {
		Collection<URI> unlinkedConcepts = new HashSet<URI>();
		
		for (URI concept : extResourcesForConcept.keySet()) {
			if (extResourcesForConcept.get(concept).isEmpty()) {
				unlinkedConcepts.add(concept);
			}
		}
		
		return unlinkedConcepts;
	}
	
	@SuppressWarnings("serial")
	private class HostNameOccurrencies extends HashMap<String, Integer>
	{
		HostNameOccurrencies() {
			super();
		}
		
		void put(String hostname) {
			Integer occurencies = get(hostname);
			put(hostname, occurencies == null ? 1 : ++occurencies);
		}
		
		String getMostOftenOccuringHostName() {
			SortedSet<Map.Entry<String, Integer>> sortedEntries = new TreeSet<Map.Entry<String, Integer>>(
				new Comparator<Map.Entry<String, Integer>>() 
				{
					@Override 
					public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				}
			);
			
			sortedEntries.addAll(entrySet());
			return sortedEntries.first().getKey();
		}
	}
}