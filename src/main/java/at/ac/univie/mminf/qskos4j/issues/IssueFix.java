package at.ac.univie.mminf.qskos4j.issues;

import org.eclipse.rdf4j.repository.RepositoryConnection;

import at.ac.univie.mminf.qskos4j.result.Result;

/**
 * A Fix algorithm for an issue result.
 * 
 * @author thomas.francart@sparna.fr
 *
 * @param <T> The type of result this fix can be computed on;
 */
public abstract class IssueFix<T extends Result<?>> {

	protected Issue<T> issue;
	protected RepositoryConnection connection;
	
	public IssueFix(Issue<T> issue, RepositoryConnection connection) {
		this.issue = issue;
		this.connection = connection;
	}
	
	/**
	 * Computes and returns a fix proposal for the given issue result
	 * @param issueResult
	 * @return
	 */
	public abstract FixProposal getFixProposal(T issueResult);
	
}
