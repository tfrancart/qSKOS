package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.inlinks.MissingInLinks;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;

public class MissingInLinksTest {

    private MissingInLinks missingInLinks;
    private AuthoritativeConcepts authoritativeConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        authoritativeConcepts = new AuthoritativeConcepts(new InvolvedConcepts());

        missingInLinks = new MissingInLinks(authoritativeConcepts);
        missingInLinks.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("rankConcepts.rdf").getConnection());
        missingInLinks.addRepositoryLoopback();
    }

    @Test
    public void testInLinksAsDbPedia() throws OpenRDFException {
        authoritativeConcepts.setAuthResourceIdentifier("dbpedia.org");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertTrue(conceptsMissingInLinks.isEmpty());
    }

    @Test
    public void testInLinksAsSTW() throws OpenRDFException {
        authoritativeConcepts.setAuthResourceIdentifier("zbw.eu");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertEquals(2, conceptsMissingInLinks.size());
    }

    @Test
    public void testInLinksAsBnf() throws OpenRDFException {
        authoritativeConcepts.setAuthResourceIdentifier("data.bnf.fr");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertEquals(1, conceptsMissingInLinks.size());
    }


    @Test
    public void testInLinksAsLocal() throws OpenRDFException {
        authoritativeConcepts.setAuthResourceIdentifier("myvocab.org");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertEquals(1, conceptsMissingInLinks.size());
    }
}
