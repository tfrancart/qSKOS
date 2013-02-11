package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.report.NumberReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 13:58
 *
 * Finds the number of relations involving SKOS lexical labels (prefLabel, altLabel, hiddenLabel).
 *
 */
public class LexicalRelations extends Issue<NumberReport<Long>> {

    private final Logger logger = LoggerFactory.getLogger(LexicalRelations.class);

    private InvolvedConcepts involvedConcepts;

    public LexicalRelations(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts.getVocabRepository(),
              "cl",
              "Concept Labels",
              "Counts the number of relations between all concepts and lexical labels (prefLabel, altLabel, hiddenLabel and subproperties thereof)",
              IssueType.STATISTICAL
        );
        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected NumberReport<Long> invoke() throws OpenRDFException {
        long relationsCount = 0;

        for (Value concept : involvedConcepts.getReport().getData()) {
            try {
                TupleQueryResult result = vocabRepository.query(createLexicalLabelQuery(concept));
                relationsCount += TupleQueryResultUtil.countResults(result);
            }
            catch (OpenRDFException e) {
                logger.error("Error finding labels for concept '" +concept+ "'");
            }
        }

        return new NumberReport<Long>(relationsCount);
    }

    private String createLexicalLabelQuery(Value concept) {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.SKOSXL +" "+ SparqlPrefix.RDFS +
                "SELECT DISTINCT ?skoslabelType ?literal WHERE {" +
                "<" +concept.stringValue()+ "> ?skoslabelType ?literal ." +
                "{?skoslabelType rdfs:subPropertyOf* skos:prefLabel}" +
                "UNION" +
                "{?skoslabelType rdfs:subPropertyOf* skos:altLabel}" +
                "UNION" +
                "{?skoslabelType rdfs:subPropertyOf* skos:hiddenLabel}" +
                "}";
    }

}