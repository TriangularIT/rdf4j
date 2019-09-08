/*******************************************************************************
 * Copyright (c) 2019 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package com.fluidops.fedx.monitoring;

import java.util.List;

import com.fluidops.fedx.endpoint.Endpoint;
import com.fluidops.fedx.monitoring.MonitoringImpl.MonitoringInformation;


public interface MonitoringService extends Monitoring
{
	
	public MonitoringInformation getMonitoringInformation(Endpoint e);
	
	public List<MonitoringInformation> getAllMonitoringInformation();

}
