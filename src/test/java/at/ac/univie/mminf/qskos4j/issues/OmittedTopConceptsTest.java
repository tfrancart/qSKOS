package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.conceptscheme.ConceptSchemes;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.OmittedTopConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;


public class OmittedTopConceptsTest {

	private OmittedTopConcepts omittedTopConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        omittedTopConcepts = new OmittedTopConcepts(new ConceptSchemes());
        omittedTopConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("missingTopConcepts.rdf").getConnection());
	}
	
	@Test
	public void testConceptSchemesWithoutTopConceptsCount() throws OpenRDFException {
		Assert.assertEquals(2, omittedTopConcepts.getResult().getData().size());
	}
	
}
