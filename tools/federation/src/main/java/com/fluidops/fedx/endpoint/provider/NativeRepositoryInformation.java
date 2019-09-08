/*******************************************************************************
 * Copyright (c) 2019 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package com.fluidops.fedx.endpoint.provider;

import java.io.File;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import com.fluidops.fedx.Config;
import com.fluidops.fedx.endpoint.EndpointType;
import com.fluidops.fedx.util.Vocabulary;


/**
 * Graph information for RDF4J {@link NativeStore} initialization.
 * 
 * <p>
 * Format:
 * </p>
 * 
 * <pre>
 * <%name%> a sd:Service ;
 *  	fedx:store "NativeStore" ;
 *  	fedx:RepositoryLocation "%location%".
 * 
 * relative path (to {@link Config#getBaseDir()}) in a "repositories" subfolder
 * 
 * <http://DBpedia> a sd:Service ;
 *  	fedx:store "NativeStore" ;
 *  	fedx:repositoryLocation "data\\repositories\\native-storage.dbpedia".
 *  
 * absolute Path
 * 
 * <http://DBpedia> a sd:Service ;
 *  	fedx:store "NativeStore" ;
 *  	fedx:repositoryLocation "D:\\data\\repositories\\native-storage.dbpedia".
 * </pre>
 * 
 * <p>
 * Note: the id is constructed from the location:
 * repositories\\native-storage.dbpedia => native-storage.dbpedia
 * </p>
 * 
 * 
 * @author Andreas Schwarte
 *
 */
public class NativeRepositoryInformation extends RepositoryInformation {

	public NativeRepositoryInformation(Model graph, Resource repNode) {
		super(EndpointType.NativeStore);
		initialize(graph, repNode);
	}

	public NativeRepositoryInformation(String name, String location) {
		super(new File(location).getName(), name, location, EndpointType.NativeStore);
	}

	protected void initialize(Model graph, Resource repNode) {
		
		// name: the node's value
		setProperty("name", repNode.stringValue());
				
		// location
		Model location = graph.filter(repNode, Vocabulary.FEDX.REPOSITORY_LOCATION, null);
		String repoLocation = location.iterator().next().getObject().stringValue();
		setProperty("location", repoLocation);
		
		// id: the name of the location
		setProperty("id", new File(repoLocation).getName());
	}
}
