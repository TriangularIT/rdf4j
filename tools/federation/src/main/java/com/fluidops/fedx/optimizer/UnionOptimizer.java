/*******************************************************************************
 * Copyright (c) 2019 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package com.fluidops.fedx.optimizer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.Union;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;

import com.fluidops.fedx.algebra.EmptyNUnion;
import com.fluidops.fedx.algebra.EmptyResult;
import com.fluidops.fedx.algebra.NUnion;
import com.fluidops.fedx.exception.OptimizationException;
import com.fluidops.fedx.structures.QueryInfo;


/**
 * Optimizer to flatten the UNION operations.
 * 
 * @author Andreas Schwarte
 *
 */
public class UnionOptimizer extends AbstractQueryModelVisitor<OptimizationException> implements FedXOptimizer
{

	protected final QueryInfo queryInfo;
		
	public UnionOptimizer(QueryInfo queryInfo) {
		super();
		this.queryInfo = queryInfo;
	}

	@Override
	public void optimize(TupleExpr tupleExpr) {
		tupleExpr.visit(this);
	}
	
	
	@Override
	public void meet(Union union) {
		
		// retrieve the union arguments, also those of nested unions
		List<TupleExpr> args = new ArrayList<TupleExpr>();
		handleUnionArgs(union, args);
		
		// remove any tuple expressions that do not produce any result
		List<TupleExpr> filtered = new ArrayList<TupleExpr>(args.size());
		for (TupleExpr arg : args) {
			if (arg instanceof EmptyResult)
				continue;
			filtered.add(arg);
		}
		
		// create a NUnion having the arguments in one layer
		// however, check if we only have zero or one argument first
		if (filtered.size()==0) {
			union.replaceWith(new EmptyNUnion(args, queryInfo));
		}
		
		else if (filtered.size()==1) {
			union.replaceWith(filtered.get(0));
		}
		
		else {
			union.replaceWith( new NUnion(filtered, queryInfo) );			
		}
	}
	
	/**
	 * Add the union arguments to the args list, includes a recursion 
	 * step for nested unions.
	 * 
	 * @param union
	 * @param args
	 */
	protected void handleUnionArgs(Union union, List<TupleExpr> args) {
		
		if (union.getLeftArg() instanceof Union) {
			handleUnionArgs((Union)union.getLeftArg(), args);
		} else {
			args.add(union.getLeftArg());
		}
		
		if (union.getRightArg() instanceof Union) {
			handleUnionArgs((Union)union.getRightArg(), args);
		} else {
			args.add(union.getRightArg());
		}
	}

}
