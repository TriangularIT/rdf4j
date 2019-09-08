/*******************************************************************************
 * Copyright (c) 2019 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package com.fluidops.fedx.monitoring;

import com.fluidops.fedx.Config;

public class MonitoringFactory
{

	/**
	 * Create a new monitoring instance depending on 
	 * {@link Config#isEnableMonitoring()}
	 * 
	 * @return the {@link Monitoring} instance
	 */
	public static Monitoring createMonitoring() {
		
		if (Config.getConfig().isEnableMonitoring())
			return new MonitoringImpl();
		return new NoopMonitoringImpl();
	}
}
