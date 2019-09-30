/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.rio.jsonld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterTest;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Peter Ansell
 */
public class JSONLDWriterTest extends RDFWriterTest {
	private final String exNs = "http://example.org/";

	public JSONLDWriterTest() {
		super(new JSONLDWriterFactory(), new JSONLDParserFactory());
	}

	@Override
	protected void setupWriterConfig(WriterConfig config) {
		super.setupWriterConfig(config);
		config.set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
	}

	@Override
	protected void setupParserConfig(ParserConfig config) {
		super.setupParserConfig(config);
		config.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
		config.set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, true);
	}

	@Test
	@Override
	@Ignore("TODO: Determine why this test is breaking")
	public void testIllegalPrefix() throws RDFHandlerException, RDFParseException, IOException {
	}

	@Test
	public void testEmptyNamespace() throws Exception {
		IRI uri1 = vf.createIRI(exNs, "uri1");
		IRI uri2 = vf.createIRI(exNs, "uri2");
		Model model = new LinkedHashModel();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {	
			RDFWriter rdfWriter = rdfWriterFactory.getWriter(out);
			rdfWriter.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
			rdfWriter.handleNamespace("", exNs);
			rdfWriter.handleNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
			rdfWriter.startRDF();
			rdfWriter.handleStatement(vf.createStatement(uri1, DCTERMS.TITLE, vf.createBNode()));
			rdfWriter.handleStatement(vf.createStatement(uri1, uri2, vf.createBNode()));
			rdfWriter.endRDF();

			try (ByteArrayInputStream is = new ByteArrayInputStream(out.toByteArray())) {
				RDFParser rdfParser = rdfParserFactory.getParser();
				rdfParser.setRDFHandler(new StatementCollector(model));
				rdfParser.parse(is, exNs);
				Set<Namespace> namespaces = model.getNamespaces();
			
				assertTrue("Invalid number of namespaces ", namespaces != null && namespaces.size() == 1);
				assertTrue("Namespace DCTERMS not found", namespaces.contains(DCTERMS.NS));
			}
		}
	}

	@Test
	public void testRoundTripNamespaces() throws Exception {
		IRI uri1 = vf.createIRI(exNs, "uri1");
		IRI uri2 = vf.createIRI(exNs, "uri2");
		Literal plainLit = vf.createLiteral("plain", XMLSchema.STRING);

		Statement st1 = vf.createStatement(uri1, uri2, plainLit);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		RDFWriter rdfWriter = rdfWriterFactory.getWriter(out);
		rdfWriter.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
		rdfWriter.handleNamespace("ex", exNs);
		rdfWriter.startRDF();
		rdfWriter.handleStatement(st1);
		rdfWriter.endRDF();

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		RDFParser rdfParser = rdfParserFactory.getParser();
		ParserConfig config = new ParserConfig();
		config.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
		config.set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, true);
		rdfParser.setParserConfig(config);
		rdfParser.setValueFactory(vf);
		Model model = new LinkedHashModel();
		rdfParser.setRDFHandler(new StatementCollector(model));

		rdfParser.parse(in, "foo:bar");

		assertEquals("Unexpected number of statements, found " + model.size(), 1, model.size());

		assertTrue("missing namespaced statement", model.contains(st1));

		if (rdfParser.getRDFFormat().supportsNamespaces()) {
			assertTrue("Expected at least one namespace, found " + model.getNamespaces().size(),
					model.getNamespaces().size() >= 1);
			assertEquals(exNs, model.getNamespace("ex").get().getName());
		}
	}
}
