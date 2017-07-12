package at.ac.univie.mminf.qskos4j.issues.relations;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import at.ac.univie.mminf.qskos4j.issues.FixProposal;
import at.ac.univie.mminf.qskos4j.issues.IssueFix;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;

/**
 * A fix for <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Valueless_Associative_Relations">Valueless Associative Relations</a>.
 * Deletes the extra skos:related or skos:relatedMatch in the data.
 * 
 * @author thomas.francart@sparna.fr
 *
 */
public class ValuelessAssociativeRelationsFix extends IssueFix<CollectionResult<Tuple<IRI>>> {

	public ValuelessAssociativeRelationsFix(ValuelessAssociativeRelations issue, RepositoryConnection connection) {
		super(issue, connection);
	}

	@Override
	public FixProposal getFixProposal(CollectionResult<Tuple<IRI>> issueResult) {
		FixProposal fp = new FixProposal();
		
		GraphQuery query = this.connection.prepareGraphQuery(QueryLanguage.SPARQL, getStatementsToBeDeletedQuery());
		StatementCollector c = new StatementCollector();
		// for each result in the issue result, delete the extra skos:related or relatedMatch link
		issueResult.getData().stream().forEach(tuple -> {
			query.clearBindings();
			query.setBinding("c1", tuple.getFirst());
			query.setBinding("c2", tuple.getSecond());
			query.evaluate(c);
		});
		// save the statements to be deleted, by making sure each statement actually exists in the repository
		fp.collectDeletedStatements(c, connection);
		
		return fp;
	}

	private String getStatementsToBeDeletedQuery() {
        return SparqlPrefix.SKOS +
                "CONSTRUCT { ?c1 skos:related ?c2 . ?c1 skos:relatedMatch ?c2 . }"+
                "WHERE {" +
                	"?c1 skos:related|skos:relatedMatch ?c2 " +
                "}";
    }
	
	
}
