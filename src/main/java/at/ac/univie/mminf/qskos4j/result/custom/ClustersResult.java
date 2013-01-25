package at.ac.univie.mminf.qskos4j.result.custom;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Resource;

import java.util.Collection;
import java.util.Set;

public class ClustersResult extends CollectionResult<Set<Resource>>
{
	private DirectedGraph<Resource, NamedEdge> graph;
	
	public ClustersResult(Collection<Set<Resource>> data, DirectedGraph<Resource, NamedEdge> graph) {
		super(data);
		this.graph = graph;
	}

	@Override
	public String getShortReport() {
		return generateReport(true);
	}

	@Override
	public String getExtensiveReport() {
		return generateReport(false);
	}
		
	private String generateReport(boolean overviewOnly) {
		StringBuilder report = new StringBuilder();
		long compCount = 1;
		
		if (overviewOnly) {
			report.append("count: ").append(getData().size());
		}
		
		for (Set<Resource> component : getData()) {
			report.append("\ncomponent ").append(compCount).append(", size: ").append(component.size());
			if (!overviewOnly) {
                for (Resource resource : component) {
                    report.append("\n\t").append(resource.toString());
                }
			}
			compCount++;
		}
		
		return report.toString();
	}

	@Override
	public Collection<String> getAsDOT() {
		return new GraphExporter(graph).exportSubGraphs(getData());
	}
}