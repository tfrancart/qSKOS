package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.outlinks.HttpURIs;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 15:33
 */
public class HttpURIsTest {

    private HttpURIs httpURIs1, httpURIs2;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        httpURIs1 = new HttpURIs();
        httpURIs1.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());

        httpURIs2 = new HttpURIs();
        httpURIs2.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("resources.rdf").getConnection());
    }

    @Test
    public void testConceptsHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(21, httpURIs1.getResult().getData().size());
    }

    @Test
    public void testResourcesHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(8, httpURIs2.getResult().getData().size());
    }
}
