/*******************************************************************************
 * Copyright (c) 2019 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package com.fluidops.fedx.exception;


/**
 * Base class for any FedX Exception.
 * 
 * @author Andreas Schwarte
 *
 */
public class FedXException extends RuntimeException {

	private static final long serialVersionUID = -3973697449786957158L;

	public FedXException() {
		super();
	}

	public FedXException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public FedXException(String arg0) {
		super(arg0);
	}

	public FedXException(Throwable arg0) {
		super(arg0);
	}
}
