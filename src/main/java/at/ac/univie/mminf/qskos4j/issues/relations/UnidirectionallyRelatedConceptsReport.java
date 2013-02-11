package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.Pair;
import org.openrdf.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class UnidirectionallyRelatedConceptsReport extends Report<Map<Pair<Resource>, String>> {

	public UnidirectionallyRelatedConceptsReport(Map<Pair<Resource>, String> data) {
		super(data);
	}

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionReport<Pair<Resource>>(getData().keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveReport());
                break;
        }
    }

    private String generateExtensiveReport()
    {
		StringBuilder extensiveReport = new StringBuilder();
		
		for (Pair<Resource> concepts : getData().keySet()) {
			extensiveReport.append("concepts: ").append(concepts.toString()).append(", related by: '").append(getData().get(concepts)).append("'\n");
		}

		return extensiveReport.toString();
	}

}