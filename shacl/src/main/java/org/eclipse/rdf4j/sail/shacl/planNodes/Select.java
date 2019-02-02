/*******************************************************************************
 * Copyright (c) 2018 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

package org.eclipse.rdf4j.sail.shacl.planNodes;


import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.impl.MapBindingSet;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.QueryParserFactory;
import org.eclipse.rdf4j.query.parser.QueryParserRegistry;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.shacl.ShaclSailConnection;

import java.util.Objects;

/**
 * @author Håvard Ottestad
 */
public class Select implements PlanNode {

	final Repository repository;
	ShaclSailConnection connection;

	String query;

	public Select(Repository repository, String query) {
		this.repository = repository;
		this.query = "select * where { " + query + "} order by ?a";
	}

	public Select(ShaclSailConnection connection, String query) {
		this.connection = connection;
		this.repository = null;
		this.query = "select * where { " + query + "} order by ?a";
	}

	@Override
	public CloseableIteration<Tuple, SailException> iterator() {
		return new CloseableIteration<Tuple, SailException>() {

			CloseableIteration<? extends BindingSet, QueryEvaluationException> bindingSet;
			RepositoryConnection repositoryConnection;

			{
				if (repository != null && connection == null) {
					repositoryConnection = repository.getConnection();
					bindingSet = repositoryConnection.prepareTupleQuery(query).evaluate();
				} else {
					QueryParserFactory queryParserFactory = QueryParserRegistry.getInstance().get(QueryLanguage.SPARQL).get();

					ParsedQuery parsedQuery = queryParserFactory.getParser().parseQuery(query, null);

					bindingSet = connection.evaluate(parsedQuery.getTupleExpr(), parsedQuery.getDataset(), new MapBindingSet(), true);


				}
			}


			@Override
			public void close() throws SailException {
				try {
					bindingSet.close();
				} finally {
					if (repositoryConnection != null) {
						repositoryConnection.close();
					}
				}

			}

			@Override
			public boolean hasNext() throws SailException {
				return bindingSet.hasNext();
			}

			@Override
			public Tuple next() throws SailException {
				return new Tuple(bindingSet.next());
			}

			@Override
			public void remove() throws SailException {

			}
		};
	}

	@Override
	public int depth() {
		return 0;
	}

	@Override
	public void getPlanAsGraphvizDot(StringBuilder stringBuilder) {
		stringBuilder.append(getId() + " [label=\"" + StringEscapeUtils.escapeJava(this.toString()) + "\"];").append("\n");
		if(repository != null){

				stringBuilder.append(System.identityHashCode(repository)+ " -> " +getId()).append("\n");
		}
		if(connection != null){
			stringBuilder.append( System.identityHashCode(connection)+ " -> " +getId()).append("\n");
		}

	}

	@Override
	public String getId() {
		return System.identityHashCode(this)+"";
	}

	@Override
	public IteratorData getIteratorDataType() {
		return IteratorData.tripleBased;
	}

	@Override
	public String toString() {
		return "Select{" +
			"query='" + query + '\'' +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Select select = (Select) o;
		return Objects.equals(repository, select.repository) &&
			Objects.equals(connection, select.connection) &&
			query.equals(select.query);
	}

	@Override
	public int hashCode() {

		return Objects.hash(System.identityHashCode(repository), System.identityHashCode(connection), query);
	}
}
