package at.ac.univie.mminf.qskos4j.issues.inlinks.test;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;

public class MissingInLinksTest extends IssueTestCase {
    private QSkos qSkosRankConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        qSkosRankConcepts = setUpIssue("rankConcepts.rdf");
        qSkosRankConcepts.addRepositoryLoopback();
        qSkosRankConcepts.setExtAccessDelayMillis(0);
    }

    @Test
    public void testInLinksAsDbPedia() throws OpenRDFException {
        qSkosRankConcepts.setAuthoritativeResourceIdentifier("dbpedia.org");

        Collection<URI> conceptsMissingInLinks = qSkosRankConcepts.findMissingInLinks().getData();
        Assert.assertTrue(conceptsMissingInLinks.isEmpty());
    }

    @Test
    public void testInLinksAsSTW() throws OpenRDFException {
        qSkosRankConcepts.setAuthoritativeResourceIdentifier("zbw.eu");

        Collection<URI> conceptsMissingInLinks = qSkosRankConcepts.findMissingInLinks().getData();
        Assert.assertEquals(2, conceptsMissingInLinks.size());
    }

    @Test
    public void testInLinksAsBnf() throws OpenRDFException {
        qSkosRankConcepts.setAuthoritativeResourceIdentifier("data.bnf.fr");

        Collection<URI> conceptsMissingInLinks = qSkosRankConcepts.findMissingInLinks().getData();
        Assert.assertEquals(1, conceptsMissingInLinks.size());
    }


    @Test
    public void testInLinksAsLocal() throws OpenRDFException {
        qSkosRankConcepts.setAuthoritativeResourceIdentifier("myvocab.org");

        Collection<URI> conceptsMissingInLinks = qSkosRankConcepts.findMissingInLinks().getData();
        Assert.assertEquals(1, conceptsMissingInLinks.size());
    }
}
